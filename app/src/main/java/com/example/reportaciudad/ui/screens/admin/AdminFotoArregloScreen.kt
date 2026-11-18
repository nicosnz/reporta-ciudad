package com.example.reportaciudad.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.reportaciudad.ui.screens.componentes.AvisoSobreCamara
import com.example.reportaciudad.ui.screens.componentes.BotonDisparo
import com.example.reportaciudad.ui.screens.componentes.BotonVolver
import com.example.reportaciudad.ui.screens.componentes.GuiasEncuadre
import com.example.reportaciudad.ui.screens.componentes.SinPermiso
import com.example.reportaciudad.ui.screens.componentes.ZonaAcciones
import com.example.reportaciudad.ui.screens.componentes.ZonaCamara
import com.example.reportaciudad.ui.screens.componentes.abrirAjustesDeLaApp
import com.example.reportaciudad.ui.screens.componentes.rememberPermisoCamara
import com.example.reportaciudad.ui.screens.reportar.componentes.iconoDeLinea
import com.example.reportaciudad.ui.screens.reportar.fotografia.VisorCamara
import com.example.reportaciudad.ui.screens.reportar.fotografia.rememberControladorCamara
import com.example.reportaciudad.ui.screens.reportar.fotografia.tomarFoto
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.EmergenciaClaro
import com.example.reportaciudad.ui.theme.Fondo
import com.example.reportaciudad.ui.theme.Neutro200
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme
import com.example.reportaciudad.ui.theme.Superficie
import com.example.reportaciudad.ui.theme.Urbano
import com.example.reportaciudad.ui.theme.UrbanoDeshabilitado
import com.example.reportaciudad.ui.theme.UrbanoOscuro
import com.example.reportaciudad.viewmodel.AdminFotoArregloUiState
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AdminFotoArregloScreen(
    estado: AdminFotoArregloUiState,
    crearArchivoFoto: () -> File,
    onFotoSacada: (String) -> Unit = {},
    onFotoElegida: (Uri) -> Unit = {},
    onRepetir: () -> Unit = {},
    onConfirmar: () -> Unit = {},
    onVolver: () -> Unit = {},
    onResuelto: () -> Unit = {}
) {
    LaunchedEffect(estado.resuelto) { if (estado.resuelto) onResuelto() }
    BackHandler(enabled = estado.subiendo) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BotonVolver(onClick = onVolver, habilitado = !estado.subiendo)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Foto del arreglo",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        letterSpacing = (-0.025).em
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    text = if (estado.fotoRuta == null) "Saca una foto de cómo quedó." else "¿Se ve bien el arreglo?",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                    color = Neutro700
                )
            }
        }

        if (estado.fotoRuta == null) {
            Captura(
                crearArchivoFoto = crearArchivoFoto,
                onFotoSacada = onFotoSacada,
                onFotoElegida = onFotoElegida
            )
        } else {
            VistaPrevia(
                estado = estado,
                fotoRuta = estado.fotoRuta,
                onRepetir = onRepetir,
                onConfirmar = onConfirmar
            )
        }
    }
}

@Composable
private fun ColumnScope.Captura(
    crearArchivoFoto: () -> File,
    onFotoSacada: (String) -> Unit,
    onFotoElegida: (Uri) -> Unit
) {
    val context = LocalContext.current
    val permiso = rememberPermisoCamara()
    val controlador = rememberControladorCamara()
    val scope = rememberCoroutineScope()
    val destello = remember { Animatable(0f) }
    var capturando by remember { mutableStateOf(false) }
    var errorCaptura by remember { mutableStateOf(false) }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onFotoElegida(uri)
    }

    fun disparar() {
        if (capturando) return
        capturando = true
        errorCaptura = false
        scope.launch {
            launch {
                destello.snapTo(1f)
                destello.animateTo(0f, tween(durationMillis = 260, easing = LinearOutSlowInEasing))
            }
            val archivo = crearArchivoFoto()
            try {
                controlador.tomarFoto(archivo, ContextCompat.getMainExecutor(context))
                onFotoSacada(archivo.absolutePath)
            } catch (e: ImageCaptureException) {
                archivo.delete()
                errorCaptura = true
            } catch (e: IllegalStateException) {
                archivo.delete()
                errorCaptura = true
            } finally {
                capturando = false
            }
        }
    }

    ZonaCamara(Modifier.weight(1f)) {
        if (permiso.concedido) {
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
        } else {
            SinPermiso(
                bloqueado = permiso.bloqueado,
                onPedir = permiso.pedir,
                onAbrirAjustes = { abrirAjustesDeLaApp(context) },
                textoPedir = "Necesitamos la cámara para sacar la foto del arreglo."
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
        BotonGaleria(
            onClick = { galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            modifier = Modifier.align(Alignment.CenterStart)
        )
        BotonDisparo(
            habilitado = permiso.concedido && !capturando,
            onClick = ::disparar
        )
    }
}

@Composable
private fun BotonGaleria(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Superficie,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.heightIn(min = 48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(IconoGaleria, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(
                text = "Galería",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            )
        }
    }
}

@Composable
private fun ColumnScope.VistaPrevia(
    estado: AdminFotoArregloUiState,
    fotoRuta: String,
    onRepetir: () -> Unit,
    onConfirmar: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(top = 18.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Neutro200)
    ) {
        AsyncImage(
            model = File(fotoRuta),
            contentDescription = "Foto nueva del arreglo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        EtiquetaFoto(
            texto = "Después",
            color = UrbanoOscuro,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
                .size(96.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Neutro200)
                .border(3.dp, Color.White, RoundedCornerShape(20.dp))
        ) {
            if (estado.fotoAntes != null) {
                AsyncImage(
                    model = estado.fotoAntes,
                    contentDescription = "Foto del problema, antes del arreglo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            EtiquetaFoto(
                texto = "Antes",
                color = Neutro700,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
            )
        }
        if (estado.subiendo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .semantics { contentDescription = "Subiendo foto" },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }

    if (estado.error) {
        Text(
            text = "No se pudo guardar el arreglo. Revisa tu conexión e intenta de nuevo.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
            color = Emergencia,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .background(EmergenciaClaro, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onRepetir,
            enabled = !estado.subiendo,
            colors = ButtonDefaults.buttonColors(
                containerColor = Superficie,
                contentColor = MaterialTheme.colorScheme.onBackground,
                disabledContainerColor = Superficie.copy(alpha = 0.6f),
                disabledContentColor = Neutro700
            ),
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp)
        ) {
            Text("Repetir", style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp))
        }
        Button(
            onClick = onConfirmar,
            enabled = !estado.subiendo,
            colors = ButtonDefaults.buttonColors(
                containerColor = Urbano,
                contentColor = Color.White,
                disabledContainerColor = UrbanoDeshabilitado,
                disabledContentColor = Color.White
            ),
            modifier = Modifier
                .weight(1.4f)
                .heightIn(min = 56.dp)
        ) {
            if (estado.subiendo) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.5.dp, modifier = Modifier.size(22.dp))
            } else {
                Text("Confirmar resuelto", style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp))
            }
        }
    }
}

@Composable
private fun EtiquetaFoto(texto: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
        color = color,
        modifier = modifier
            .background(Fondo.copy(alpha = 0.92f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

private val IconoGaleria = iconoDeLinea(
    "Galeria", 2.25f,
    "M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z",
    "M8.5 8.5a1.5 1.5 0 1 0 3 0a1.5 1.5 0 1 0-3 0",
    "M21 15l-5-5L5 21"
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminFotoArregloPreviewPreview() {
    ReportaCiudadTheme {
        AdminFotoArregloScreen(
            estado = AdminFotoArregloUiState(fotoRuta = "/sin/foto.jpg"),
            crearArchivoFoto = { File("") }
        )
    }
}
