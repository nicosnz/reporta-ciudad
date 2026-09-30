package com.example.reportaciudad.ui.screens.reportar.resumen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LifecycleResumeEffect
import coil3.compose.AsyncImage
import com.example.reportaciudad.data.model.ReporteFormulario
import com.example.reportaciudad.data.model.Familia
import com.example.reportaciudad.data.model.TipoProblema
import com.example.reportaciudad.ui.theme.AmbientalEtiqueta
import com.example.reportaciudad.ui.theme.AmbientalEtiquetaTexto
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.EmergenciaClaro
import com.example.reportaciudad.ui.theme.Neutro200
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme
import com.example.reportaciudad.ui.theme.Superficie
import com.example.reportaciudad.ui.theme.UrbanoEtiqueta
import com.example.reportaciudad.ui.theme.UrbanoEtiquetaTexto
import org.osmdroid.util.GeoPoint
import java.io.File
import java.util.Locale
import com.example.reportaciudad.ui.screens.reportar.componentes.PasoReporte
import com.example.reportaciudad.ui.screens.reportar.componentes.iconoDeLinea
import com.example.reportaciudad.ui.screens.reportar.localizacion.PinCentral
import com.example.reportaciudad.ui.screens.reportar.localizacion.ZOOM_CALLE
import com.example.reportaciudad.ui.screens.reportar.localizacion.crearMapa
import com.example.reportaciudad.ui.screens.reportar.emergencia.icono
import com.example.reportaciudad.ui.screens.reportar.problema.icono

// Paso 5 del flujo de reporte: revisar lo elegido en los pasos anteriores y enviarlo.
@Composable
fun ResumenScreen(
    borrador: ReporteFormulario,
    onVolver: () -> Unit = {},
    onEnviar: (ReporteFormulario) -> Unit = {},
    onVerMisReportes: () -> Unit = {}
) {
    var enviado by rememberSaveable { mutableStateOf(false) }

    PasoReporte(
        paso = 5,
        titulo = "Resumen",
        subtitulo = "Revisa tu reporte antes de enviarlo.",
        onVolver = onVolver,
        compacto = true
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            borrador.categoria()?.let { TarjetaCategoria(it) }
            borrador.fotoRuta?.let { TarjetaFoto(it) }
            val latitud = borrador.latitud
            val longitud = borrador.longitud
            if (latitud != null && longitud != null) TarjetaUbicacion(latitud, longitud)
        }

        Button(
            onClick = {
                onEnviar(borrador)
                enviado = true
            },
            // Una vez enviado queda deshabilitado, para no mandarlo dos veces
            enabled = borrador.estaCompleto && !enviado,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 12.dp, end = 4.dp, bottom = 16.dp)
                .defaultMinSize(minHeight = 56.dp)
                .testTag("boton_enviar_reporte")
        ) {
            Text(
                text = "Enviar reporte",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp)
            )
        }
    }

    if (enviado) ModalReporteEnviado(onVerMisReportes)
}

// Qué se muestra en la tarjeta de categoría según lo elegido en el paso 2
private class InfoCategoria(
    val tipo: String,
    val nombre: String,
    val icono: ImageVector,
    val fondoIcono: Color,
    val colorIcono: Color
)

private fun ReporteFormulario.categoria(): InfoCategoria? {
    emergencia?.let {
        return InfoCategoria("Emergencia en curso", it.nombre, it.icono, EmergenciaClaro, Emergencia)
    }
    return problema?.let { it.infoCategoria() }
}

private fun TipoProblema.infoCategoria() = when (familia) {
    Familia.URBANO -> InfoCategoria("Reporte de rutina · urbano", nombre, icono, UrbanoEtiqueta, UrbanoEtiquetaTexto)
    Familia.AMBIENTAL -> InfoCategoria("Reporte de rutina · ambiental", nombre, icono, AmbientalEtiqueta, AmbientalEtiquetaTexto)
}

@Composable
private fun TarjetaCategoria(info: InfoCategoria) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Superficie, RoundedCornerShape(28.dp))
            .padding(start = 10.dp, top = 10.dp, end = 12.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .background(info.fondoIcono, CircleShape)
        ) {
            Icon(info.icono, contentDescription = null, tint = info.colorIcono, modifier = Modifier.size(26.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = info.tipo,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = Neutro700
            )
            Text(
                text = info.nombre,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

// Tarjeta con contenido arriba (foto o mapa) y un pie de texto debajo
@Composable
private fun TarjetaConImagen(imagen: @Composable () -> Unit, pie: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Superficie, RoundedCornerShape(28.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        imagen()
        Box(Modifier.padding(start = 8.dp, end = 4.dp, bottom = 4.dp)) { pie() }
    }
}

@Composable
private fun TarjetaFoto(ruta: String) {
    TarjetaConImagen(
        imagen = {
            AsyncImage(
                model = File(ruta),
                contentDescription = "Foto del problema",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Neutro200)
            )
        },
        pie = {
            Text(
                text = "Fotografía",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    )
}

@Composable
private fun TarjetaUbicacion(latitud: Double, longitud: Double) {
    TarjetaConImagen(
        imagen = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Neutro200)
            ) {
                MapaFijo(latitud, longitud, Modifier.fillMaxSize())
                PinCentral(Modifier.align(Alignment.Center), ancho = 28.dp, alto = 36.dp)
            }
        },
        pie = {
            Column {
                Text(
                    text = "Santa Cruz de la Sierra",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    // Locale.US: siempre con punto decimal, como se escriben las coordenadas
                    text = String.format(Locale.US, "%.5f, %.5f", latitud, longitud),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = Neutro700
                )
            }
        }
    )
}

// Mapa de OpenStreetMap centrado en el punto del reporte; solo se muestra, no se mueve
@Composable
private fun MapaFijo(latitud: Double, longitud: Double, modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) return   // el Preview no puede cargar el mapa
    val context = LocalContext.current
    val mapa = remember(latitud, longitud) {
        crearMapa(context).apply {
            controller.setZoom(ZOOM_CALLE)
            controller.setCenter(GeoPoint(latitud, longitud))
        }
    }
    LifecycleResumeEffect(mapa) {
        mapa.onResume()
        onPauseOrDispose { mapa.onPause() }
    }
    DisposableEffect(mapa) { onDispose { mapa.onDetach() } }

    AndroidView(
        factory = { mapa },
        modifier = modifier.semantics { contentDescription = "Mapa con la ubicación del reporte" }
    )
}

// Ventana que confirma el envío. Desde acá solo se puede ir a Mis reportes (también con Atrás).
@Composable
private fun ModalReporteEnviado(onVerMisReportes: () -> Unit) {
    Dialog(
        onDismissRequest = onVerMisReportes,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(40.dp))
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(40.dp))
                    .padding(start = 24.dp, top = 32.dp, end = 24.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(84.dp)
                        .background(AmbientalEtiqueta, CircleShape)
                ) {
                    Icon(IconoCheck, contentDescription = null, tint = AmbientalEtiquetaTexto, modifier = Modifier.size(40.dp))
                }
                Text(
                    text = "¡Reporte enviado!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        letterSpacing = (-0.025).em
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    text = "Tu reporte ya está en el mapa público. Te avisamos cuando cambie de estado.",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
                    color = Neutro700,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onVerMisReportes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .defaultMinSize(minHeight = 56.dp)
                        .testTag("boton_ver_mis_reportes")
                ) {
                    Text("Ver mis reportes", style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp))
                }
            }
        }
    }
}

private val IconoCheck = iconoDeLinea("Check", 2.75f, "M20 6L9 17l-5-5")

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ResumenScreenPreview() {
    ReportaCiudadTheme {
        ResumenScreen(
            borrador = ReporteFormulario(
                problema = TipoProblema.BACHE,
                fotoRuta = "",
                latitud = -17.7833,
                longitud = -63.1821
            )
        )
    }
}
