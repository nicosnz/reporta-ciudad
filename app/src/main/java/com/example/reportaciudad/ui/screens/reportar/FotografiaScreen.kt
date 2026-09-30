package com.example.reportaciudad.ui.screens.reportar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import coil3.compose.AsyncImage
import com.example.reportaciudad.data.AlmacenFotos
import com.example.reportaciudad.ui.theme.AmbientalEtiqueta
import com.example.reportaciudad.ui.theme.AmbientalEtiquetaTexto
import com.example.reportaciudad.ui.theme.Neutro900
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FotografiaScreen(
    onVolver: () -> Unit = {},
    onFotoTomada: (String) -> Unit = {}
) {

    var fotoRuta by rememberSaveable { mutableStateOf<String?>(null) }


    val descartarYVolver = {
        AlmacenFotos.borrar(fotoRuta)
        fotoRuta = null
        onVolver()
    }
    BackHandler(enabled = fotoRuta != null) { descartarYVolver() }

    PasoReporte(
        paso = 3,
        titulo = "Fotografía",
        subtitulo = "Saca una foto clara del problema.",
        onVolver = descartarYVolver,
        compacto = true
    ) {
        if (LocalInspectionMode.current) {

            ZonaCamara(Modifier.weight(1f)) { GuiasEncuadre() }
            ZonaAcciones { BotonDisparo(habilitado = true, onClick = {}) }
        } else {
            ContenidoCamara(
                fotoRuta = fotoRuta,
                onFotoSacada = { fotoRuta = it },
                onRepetir = {
                    AlmacenFotos.borrar(fotoRuta)
                    fotoRuta = null
                },
                onUsarFoto = { fotoRuta?.let(onFotoTomada) }
            )
        }
    }
}

@Composable
private fun ColumnScope.ContenidoCamara(
    fotoRuta: String?,
    onFotoSacada: (String) -> Unit,
    onRepetir: () -> Unit,
    onUsarFoto: () -> Unit
) {
    val context = LocalContext.current
    val permiso = rememberPermisoCamara()
    val controlador = rememberControladorCamara()
    val scope = rememberCoroutineScope()
    val destello = remember { Animatable(0f) }
    var capturando by remember { mutableStateOf(false) }
    var errorCaptura by remember { mutableStateOf(false) }

    fun disparar() {
        if (capturando) return
        capturando = true
        errorCaptura = false
        scope.launch {
            launch {
                destello.snapTo(1f)
                destello.animateTo(0f, tween(durationMillis = 260, easing = LinearOutSlowInEasing))
            }
            val archivo = AlmacenFotos.crearArchivo(context)
            try {
                controlador.tomarFoto(archivo, ContextCompat.getMainExecutor(context))
                onFotoSacada(archivo.absolutePath)
            } catch (e: ImageCaptureException) {
                archivo.delete()
                errorCaptura = true
            } finally {
                capturando = false
            }
        }
    }

    ZonaCamara(Modifier.weight(1f)) {
        when {
            fotoRuta != null -> {
                AsyncImage(
                    model = File(fotoRuta),
                    contentDescription = "Foto sacada del problema",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                EtiquetaFotoTomada(Modifier.padding(16.dp))
            }
            permiso.concedido -> {
                VisorCamara(controlador, Modifier.fillMaxSize())
                GuiasEncuadre()
                if (errorCaptura) {
                    AvisoSobreCamara(
                        texto = "No se pudo sacar la foto. Intenta de nuevo.",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 20.dp)
                    )
                }
            }
            else -> SinPermiso(
                bloqueado = permiso.bloqueado,
                onPedir = permiso.pedir,
                onAbrirAjustes = { abrirAjustesDeLaApp(context) }
            )
        }


        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = destello.value }
                .background(Color.White)
        )
    }

    ZonaAcciones {
        if (fotoRuta == null) {
            BotonDisparo(
                habilitado = permiso.concedido && !capturando,
                onClick = ::disparar
            )
        } else {
            BotonesRevision(onRepetir = onRepetir, onUsarFoto = onUsarFoto)
        }
    }
}


@Composable
private fun ZonaCamara(modifier: Modifier = Modifier, contenido: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(Neutro900),
        content = contenido
    )
}


@Composable
private fun ZonaAcciones(contenido: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) { contenido() }
}


@Composable
private fun GuiasEncuadre() {
    Canvas(
        Modifier
            .fillMaxSize()
            .padding(22.dp)
    ) {
        val largo = 36.dp.toPx()
        val radio = 18.dp.toPx()
        val trazo = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        val esquina = Path().apply {
            moveTo(0f, largo)
            lineTo(0f, radio)
            arcTo(Rect(0f, 0f, radio * 2, radio * 2), 180f, 90f, false)
            lineTo(largo, 0f)
        }

        listOf(1f to 1f, -1f to 1f, 1f to -1f, -1f to -1f).forEach { (sx, sy) ->
            scale(sx, sy, pivot = Offset(size.width / 2, size.height / 2)) {
                drawPath(esquina, Color.White, style = trazo)
            }
        }
    }
}

@Composable
private fun EtiquetaFotoTomada(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(AmbientalEtiqueta, CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(IconoCheck, contentDescription = null, tint = AmbientalEtiquetaTexto, modifier = Modifier.size(14.dp))
        Text(
            text = "Foto tomada",
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = AmbientalEtiquetaTexto
        )
    }
}

@Composable
private fun AvisoSobreCamara(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
        color = Color.White,
        modifier = modifier
            .background(Neutro900.copy(alpha = 0.6f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun SinPermiso(bloqueado: Boolean, onPedir: () -> Unit, onAbrirAjustes: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(IconoCamara, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        Text(
            text = if (bloqueado) {
                "El permiso de cámara está desactivado. Actívalo en los ajustes para sacar la foto."
            } else {
                "Necesitamos la cámara para sacar la foto del problema."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = if (bloqueado) onAbrirAjustes else onPedir,
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .testTag("boton_permiso_camara")
        ) {
            Text(if (bloqueado) "Abrir ajustes" else "Permitir cámara")
        }
    }
}


@Composable
private fun BotonDisparo(habilitado: Boolean, onClick: () -> Unit) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(if (presionado) 0.94f else 1f, label = "escalaDisparo")

    Box(
        modifier = Modifier
            .size(96.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
                alpha = if (habilitado) 1f else 0.5f
            }
            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .padding(8.dp)
            .shadow(6.dp, CircleShape)
            .background(Color.White, CircleShape)
            .clickable(
                interactionSource = interaccion,
                indication = null,
                enabled = habilitado,
                role = Role.Button,
                onClick = onClick
            )
            .semantics { contentDescription = "Tomar foto" }
            .testTag("boton_tomar_foto")
    )
}

@Composable
private fun BotonesRevision(onRepetir: () -> Unit, onUsarFoto: () -> Unit) {
    val estiloTexto = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onRepetir,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.16f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 56.dp)
                .testTag("boton_repetir_foto")
        ) {
            Text("Repetir", style = estiloTexto)
        }
        Button(
            onClick = onUsarFoto,
            modifier = Modifier
                .weight(1.4f)
                .defaultMinSize(minHeight = 56.dp)
                .testTag("boton_usar_foto")
        ) {
            Text("Usar foto", style = estiloTexto)
        }
    }
}


private class PermisoCamara(val concedido: Boolean, val bloqueado: Boolean, val pedir: () -> Unit)

@Composable
private fun rememberPermisoCamara(): PermisoCamara {
    val context = LocalContext.current
    val activity = LocalActivity.current
    var concedido by remember { mutableStateOf(tienePermisoCamara(context)) }
    var bloqueado by remember { mutableStateOf(false) }

    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        concedido = ok

        bloqueado = !ok && activity != null &&
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
    }


    LifecycleResumeEffect(Unit) {
        concedido = tienePermisoCamara(context)
        if (concedido) bloqueado = false
        onPauseOrDispose { }
    }


    var pedidoInicial by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!concedido && !pedidoInicial) {
            pedidoInicial = true
            lanzador.launch(Manifest.permission.CAMERA)
        }
    }

    return PermisoCamara(concedido, bloqueado) { lanzador.launch(Manifest.permission.CAMERA) }
}

private fun tienePermisoCamara(context: Context) =
    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED

private fun abrirAjustesDeLaApp(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

private val IconoCheck = iconoDeLinea("Check", 2.75f, "M20 6L9 17l-5-5")

private val IconoCamara = iconoDeLinea(
    "Camara", 2f,
    "M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z",
    "M9 13a3 3 0 1 0 6 0a3 3 0 1 0 -6 0"
)

