package com.example.reportaciudad.ui.screens.reportar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme
import com.example.reportaciudad.ui.theme.Superficie

// Paso 1 del flujo de reporte: elegir entre emergencia en curso o reporte de rutina
@Composable
fun TipoReporteScreen(
    onVolver: () -> Unit = {},
    onEmergencia: () -> Unit = {},
    onRutina: () -> Unit = {}
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
            paso = 1,
            total = 5,
            modifier = Modifier.padding(start = 4.dp, top = 12.dp, end = 4.dp)
        )

        BotonVolver(
            onClick = onVolver,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "¿Qué está pasando?",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 30.sp,
                lineHeight = 36.sp,
                letterSpacing = (-0.025).em
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 4.dp, top = 24.dp, end = 4.dp)
        )
        Text(
            text = "Elige lo que está pasando para ayudarte.",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
            color = Neutro700,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp)
        )

        Spacer(Modifier.height(64.dp))

        BotonOpcion(
            texto = "Emergencia en curso",
            icono = IconoSirena,
            color = Emergencia,
            onClick = onEmergencia
        )
        Text(
            text = "ó",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        )
        BotonOpcion(
            texto = "Reporte de rutina",
            icono = IconoDocumento,
            color = MaterialTheme.colorScheme.primary,
            onClick = onRutina
        )
    }
}

// Opción grande: mínimo 84 dp de alto y un icono además del color.
// Texto de 19 sp en 800 cuenta como texto grande para WCAG AA (≥ 3:1): blanco sobre Emergencia 7.6:1, sobre Urbano 4.4:1
@Composable
private fun BotonOpcion(texto: String, icono: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = color,
        contentColor = Color.White,
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 84.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 19.sp, lineHeight = 24.sp)
            )
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }
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

private fun iconoDeLinea(nombre: String, grosor: Float, vararg trazos: String): ImageVector =
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

private val IconoSirena = iconoDeLinea(
    "Sirena", 2f,
    "M8 16v-4a4 4 0 0 1 8 0v4",
    "M3 12h1M12 3v1M20 12h1M5.6 5.6l0.7 0.7M18.4 5.6l-0.7 0.7",
    "M7 16h10a1 1 0 0 1 1 1v2a1 1 0 0 1-1 1H7a1 1 0 0 1-1-1v-2a1 1 0 0 1 1-1z"
)

private val IconoDocumento = iconoDeLinea(
    "Documento", 2f,
    "M14 3v4a1 1 0 0 0 1 1h4",
    "M17 21H7a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h7l5 5v11a2 2 0 0 1-2 2z",
    "M9 17v-5M12 17v-1M15 17v-3"
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TipoReporteScreenPreview() {
    ReportaCiudadTheme {
        TipoReporteScreen()
    }
}
