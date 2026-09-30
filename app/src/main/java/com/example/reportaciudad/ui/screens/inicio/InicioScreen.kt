package com.example.reportaciudad.ui.screens.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.reportaciudad.data.reportes.reportes as todosLosReportes
import com.example.reportaciudad.data.model.Familia
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.ui.theme.AmbientalOscuro
import com.example.reportaciudad.ui.theme.Neutro200
import com.example.reportaciudad.ui.theme.Neutro300
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme
import com.example.reportaciudad.ui.theme.Superficie
import com.example.reportaciudad.ui.theme.UrbanoOscuro

@Composable
fun InicioScreen(
    navController: NavController,
    reportes: List<Reporte> = todosLosReportes,
    onReportar: () -> Unit = {}
) {
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Mis reportes",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        lineHeight = 36.sp,
                        letterSpacing = (-0.025).em
                    ),
                    color = MaterialTheme.colorScheme.onBackground,

                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
                )
            }
            if (reportes.isEmpty()) {
                item {
                    Text(
                        text = "Todavía no hiciste ningún reporte.\nToca Reportar para enviar el primero.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Neutro700,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp)
                    )
                }
            }
            else{
                items(reportes, key = { it.id }) { reporte ->
                    FilaReporte(reporte = reporte, onClick = { navController.navigate("detalle/${reporte.id}") })
                }
            }

        }

        BarraInferior(onReportar = onReportar)
    }
}

@Composable
private fun FilaReporte(reporte: Reporte, onClick: () -> Unit) {
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
            MiniaturaReporte(
                colorAro = when (reporte.familia) {
                    Familia.URBANO -> UrbanoOscuro
                    Familia.AMBIENTAL -> AmbientalOscuro
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
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${reporte.categoria} · ${reporte.antiguedad}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                    color = Neutro700
                )
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


@Composable
private fun MiniaturaReporte(colorAro: Color) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .drawBehind {
                val paso = 12.dp.toPx() / Math.sqrt(2.0).toFloat()
                val rayas = Brush.linearGradient(
                    0f to Neutro200, 0.5f to Neutro200, 0.5f to Neutro300, 1f to Neutro300,
                    start = Offset.Zero,
                    end = Offset(paso, paso),
                    tileMode = TileMode.Repeated
                )
                drawCircle(rayas)
            }
            .border(3.dp, colorAro, CircleShape)
    )
}

@Composable
private fun BarraInferior(onReportar: () -> Unit) {
    val colorDivisor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.16f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                drawLine(colorDivisor, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
            }
            .navigationBarsPadding()
            .padding(start = 10.dp, top = 12.dp, end = 10.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onReportar,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shadowElevation = 12.dp,
            modifier = Modifier
                .sobresalir(34.dp)
                .size(62.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = IconoCamara,
                    contentDescription = "Reportar",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Reportar",
            style = MaterialTheme.typography.labelMedium,
            color = UrbanoOscuro
        )
    }
}


private fun Modifier.sobresalir(alto: Dp) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val desplazamiento = alto.roundToPx()
    layout(placeable.width, placeable.height - desplazamiento) {
        placeable.place(0, -desplazamiento)
    }
}

private fun iconoDeLinea(nombre: String, vararg trazos: String): ImageVector =
    ImageVector.Builder(
        name = nombre,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        trazos.forEach { trazo ->
            addPath(
                pathData = addPathNodes(trazo),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            )
        }
    }.build()

private val IconoChevron = iconoDeLinea("Chevron", "M9 18l6-6-6-6")

private val IconoCamara = iconoDeLinea(
    "Camara",
    "M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z",
    "M9 13a3 3 0 1 0 6 0a3 3 0 1 0 -6 0"
)

