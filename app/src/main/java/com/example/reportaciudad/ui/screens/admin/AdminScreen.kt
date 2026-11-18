package com.example.reportaciudad.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.reportaciudad.data.model.Bandeja
import com.example.reportaciudad.data.model.Familia
import com.example.reportaciudad.data.model.ReporteServidor
import com.example.reportaciudad.ui.screens.componentes.BarraEstado
import com.example.reportaciudad.ui.screens.componentes.BotonSalir
import com.example.reportaciudad.ui.screens.reportar.componentes.iconoDeLinea
import com.example.reportaciudad.ui.theme.AmbientalOscuro
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.Neutro200
import com.example.reportaciudad.ui.theme.Neutro300
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.Superficie
import com.example.reportaciudad.ui.theme.Urbano
import com.example.reportaciudad.ui.theme.UrbanoOscuro
import com.example.reportaciudad.viewmodel.AdminPanelUiState

// Pantalla de inicio del administrador: los reportes de todos los vecinos, en 3 bandejas
@Composable
fun AdminScreen(
    estado: AdminPanelUiState,
    onElegirBandeja: (Bandeja) -> Unit = {},
    onAbrirReporte: (String) -> Unit = {},
    onReintentar: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    // Primera carga o error sin nada que mostrar: ocupan el lugar de la lista
    val sinDatos = estado.reportes.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Encabezado(estado = estado, onCerrarSesion = onCerrarSesion)
            }
            when {
                sinDatos && estado.cargando -> item { Cargando() }
                sinDatos && estado.error -> item { AvisoError(onReintentar = onReintentar) }
                else -> {
                    // Había lista pero no se pudo actualizar: se sigue viendo la anterior
                    if (estado.error) item { AvisoError(onReintentar = onReintentar, compacto = true) }
                    if (estado.visibles.isEmpty()) {
                        item { SinReportes(estado.bandeja) }
                    } else {
                        items(estado.visibles, key = { it.remoteId }) { reporte ->
                            FilaReporte(reporte, onClick = { onAbrirReporte(reporte.remoteId) })
                        }
                    }
                }
            }
        }

        BarraBandejas(
            actual = estado.bandeja,
            pendientes = estado.cantidad(Bandeja.PENDIENTES),
            onElegir = onElegirBandeja
        )
    }
}

@Composable
private fun Encabezado(estado: AdminPanelUiState, onCerrarSesion: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = estado.bandeja.nombre,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    letterSpacing = (-0.025).em
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (estado.reportes.isEmpty()) "Reportes de los vecinos"
                else subtitulo(estado.bandeja, estado.cantidad(estado.bandeja)),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
                color = Neutro700
            )
        }
        BotonSalir(onClick = onCerrarSesion)
    }
}

private fun subtitulo(bandeja: Bandeja, n: Int): String {
    val reportes = if (n == 1) "1 reporte" else "$n reportes"
    return when (bandeja) {
        Bandeja.PENDIENTES -> "$reportes por atender"
        Bandeja.EN_CURSO -> "$reportes con cuadrilla asignada"
        Bandeja.CERRADOS -> if (n == 1) "1 reporte resuelto o rechazado" else "$n reportes resueltos o rechazados"
    }
}

@Composable
private fun FilaReporte(reporte: ReporteServidor, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(26.dp),
        color = Superficie,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 14.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Miniatura(
                fotoUrl = reporte.fotoUrl,
                colorAro = when (reporte.familia) {
                    Familia.URBANO -> UrbanoOscuro
                    Familia.AMBIENTAL -> AmbientalOscuro
                    Familia.EMERGENCIA -> Emergencia
                }
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = reporte.titulo,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${reporte.categoria} · ${reporte.antiguedad}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                    color = Neutro700
                )
                BarraEstado(estado = reporte.estado, modifier = Modifier.padding(top = 2.dp))
            }
            Icon(
                imageVector = IconoChevron,
                contentDescription = null,
                tint = Neutro700,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Foto del reporte en un círculo con el aro de su familia. Sin foto (o mientras carga): rayas.
@Composable
private fun Miniatura(fotoUrl: String?, colorAro: Color) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .drawBehind {
                val paso = 12.dp.toPx() / Math.sqrt(2.0).toFloat()
                drawRect(
                    Brush.linearGradient(
                        0f to Neutro200, 0.5f to Neutro200, 0.5f to Neutro300, 1f to Neutro300,
                        start = Offset.Zero,
                        end = Offset(paso, paso),
                        tileMode = TileMode.Repeated
                    )
                )
            }
            .border(3.dp, colorAro, CircleShape)
    ) {
        if (fotoUrl != null) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun Cargando() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp)
            .semantics { contentDescription = "Cargando reportes" },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Urbano)
    }
}

@Composable
private fun SinReportes(bandeja: Bandeja) {
    Text(
        text = when (bandeja) {
            Bandeja.PENDIENTES -> "No hay reportes pendientes."
            Bandeja.EN_CURSO -> "No hay reportes en curso."
            Bandeja.CERRADOS -> "Todavía no hay reportes cerrados."
        },
        style = MaterialTheme.typography.bodyLarge,
        color = Neutro700,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
    )
}

// compacto: arriba de una lista vieja. Si no: ocupa el lugar de la lista.
@Composable
private fun AvisoError(onReintentar: () -> Unit, compacto: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (compacto) 0.dp else 48.dp)
            .background(Superficie, RoundedCornerShape(24.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (compacto) "No se pudo actualizar la lista. Revisa tu conexión."
            else "No se pudieron cargar los reportes. Revisa tu conexión.",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onReintentar,
            colors = ButtonDefaults.buttonColors(containerColor = Urbano, contentColor = Color.White),
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text("Reintentar", style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp))
        }
    }
}

@Composable
private fun BarraBandejas(actual: Bandeja, pendientes: Int, onElegir: (Bandeja) -> Unit) {
    val colorDivisor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.16f)
    val colores = NavigationBarItemDefaults.colors(
        selectedIconColor = UrbanoOscuro,
        selectedTextColor = UrbanoOscuro,
        indicatorColor = Color.Transparent,
        unselectedIconColor = Neutro700,
        unselectedTextColor = Neutro700
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.drawBehind {
            drawLine(colorDivisor, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
        }
    ) {
        Bandeja.entries.forEach { bandeja ->
            NavigationBarItem(
                selected = bandeja == actual,
                onClick = { onElegir(bandeja) },
                colors = colores,
                icon = {
                    // Solo Pendientes lleva contador: es lo que le toca atender al administrador
                    if (bandeja == Bandeja.PENDIENTES && pendientes > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Emergencia, contentColor = Color.White) {
                                    Text(if (pendientes > 99) "99+" else pendientes.toString())
                                }
                            }
                        ) {
                            Icon(bandeja.icono, contentDescription = null, modifier = Modifier.size(22.dp))
                        }
                    } else {
                        Icon(bandeja.icono, contentDescription = null, modifier = Modifier.size(22.dp))
                    }
                },
                label = {
                    Text(
                        text = bandeja.nombre,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                },
                modifier = Modifier.semantics {
                    if (bandeja == Bandeja.PENDIENTES && pendientes > 0) {
                        contentDescription = "${bandeja.nombre}, $pendientes por atender"
                    }
                }
            )
        }
    }
}

private val Bandeja.icono: ImageVector
    get() = when (this) {
        Bandeja.PENDIENTES -> IconoBandeja
        Bandeja.EN_CURSO -> IconoCamion
        Bandeja.CERRADOS -> IconoCerrado
    }

private val IconoChevron = iconoDeLinea("Chevron", 2.25f, "M9 18l6-6-6-6")

private val IconoBandeja = iconoDeLinea(
    "Bandeja", 2.25f,
    "M22 12h-6l-2 3h-4l-2-3H2",
    "M5.45 5.11L2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"
)

private val IconoCamion = iconoDeLinea(
    "Camion", 2.25f,
    "M1 4h14v12H1z",
    "M15 8h4l3 3v5h-7",
    "M3.5 18.5a2 2 0 1 0 4 0a2 2 0 1 0-4 0",
    "M15.5 18.5a2 2 0 1 0 4 0a2 2 0 1 0-4 0"
)

private val IconoCerrado = iconoDeLinea(
    "Cerrado", 2.25f,
    "M22 11.08V12a10 10 0 1 1-5.93-9.14",
    "M22 4L12 14.01l-3-3"
)
