package com.example.reportaciudad.ui.screens.reportar.fotografia

import androidx.activity.compose.BackHandler
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.reportaciudad.ui.theme.AmbientalEtiqueta
import com.example.reportaciudad.ui.theme.AmbientalEtiquetaTexto
import kotlinx.coroutines.launch
import java.io.File
import com.example.reportaciudad.ui.screens.componentes.AvisoSobreCamara
import com.example.reportaciudad.ui.screens.componentes.BotonDisparo
import com.example.reportaciudad.ui.screens.componentes.GuiasEncuadre
import com.example.reportaciudad.ui.screens.componentes.SinPermiso
import com.example.reportaciudad.ui.screens.componentes.ZonaAcciones
import com.example.reportaciudad.ui.screens.componentes.ZonaCamara
import com.example.reportaciudad.ui.screens.componentes.abrirAjustesDeLaApp
import com.example.reportaciudad.ui.screens.componentes.rememberPermisoCamara
import com.example.reportaciudad.ui.screens.reportar.componentes.PasoReporte
import com.example.reportaciudad.ui.screens.reportar.componentes.iconoDeLinea

// Paso 3. La foto vive en el borrador del ViewModel: la pantalla solo la muestra y avisa lo que pasa.
@Composable
fun FotografiaScreen(
    fotoRuta: String?,
    crearArchivoFoto: () -> File,
    onFotoSacada: (String) -> Unit = {},
    onRepetir: () -> Unit = {},
    onUsarFoto: () -> Unit = {},
    // Volver descarta la foto (si había) antes de salir del paso
    onVolver: () -> Unit = {}
) {
    BackHandler(enabled = fotoRuta != null) { onVolver() }

    PasoReporte(
        paso = 3,
        titulo = "Fotografía",
        subtitulo = "Saca una foto clara del problema.",
        onVolver = onVolver,
        compacto = true
    ) {
        ContenidoCamara(
            fotoRuta = fotoRuta,
            crearArchivoFoto = crearArchivoFoto,
            onFotoSacada = onFotoSacada,
            onRepetir = onRepetir,
            onUsarFoto = onUsarFoto
        )
    }
}

@Composable
private fun ColumnScope.ContenidoCamara(
    fotoRuta: String?,
    crearArchivoFoto: () -> File,
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
            val archivo = crearArchivoFoto()
            try {
                controlador.tomarFoto(archivo, ContextCompat.getMainExecutor(context))
                onFotoSacada(archivo.absolutePath)
            } catch (e: ImageCaptureException) {
                archivo.delete()
                errorCaptura = true
            } catch (e: IllegalStateException) {
                // La cámara no llegó a iniciar (sin cámara trasera, o la usa otra app)
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
                onAbrirAjustes = { abrirAjustesDeLaApp(context) },
                textoPedir = "Necesitamos la cámara para sacar la foto del problema."
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


private val IconoCheck = iconoDeLinea("Check", 2.75f, "M20 6L9 17l-5-5")


