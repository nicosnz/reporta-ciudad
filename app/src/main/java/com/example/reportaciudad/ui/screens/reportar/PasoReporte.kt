package com.example.reportaciudad.ui.screens.reportar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.Superficie

const val TOTAL_PASOS = 5

// Estructura común de cada paso del flujo de reporte: barra de progreso, volver, título y subtítulo.
// Cada pantalla pone su contenido debajo sin tocar este encabezado.
@Composable
fun PasoReporte(
    paso: Int,
    titulo: String,
    subtitulo: String,
    onVolver: () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        BarraProgreso(
            paso = paso,
            total = TOTAL_PASOS,
            modifier = Modifier.padding(start = 4.dp, top = 12.dp, end = 4.dp)
        )

        BotonVolver(
            onClick = onVolver,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 30.sp,
                lineHeight = 36.sp,
                letterSpacing = (-0.025).em
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(start = 4.dp, top = 24.dp, end = 4.dp)
                .semantics { heading() }
        )
        Text(
            text = subtitulo,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
            color = Neutro700,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp)
        )

        contenido()
    }
}

@Composable
private fun BotonVolver(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Superficie,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = IconoVolver,
                contentDescription = "Volver",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

internal fun iconoDeLinea(nombre: String, grosor: Float, vararg trazos: String): ImageVector =
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
                strokeLineWidth = grosor,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            )
        }
    }.build()

private val IconoVolver = iconoDeLinea("Volver", 2.75f, "M12 19l-7-7 7-7", "M19 12H5")
