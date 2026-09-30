package com.example.reportaciudad.ui.screens.bienvenida

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.ui.theme.UrbanoClaro

@Composable
fun BienvenidaScreen(onInicio: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 120.dp)
                .requiredSize(520.dp)
                .clip(CircleShape)
                .background(UrbanoClaro)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ReportaCiudad",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 40.sp,
                    lineHeight = 40.sp,
                    letterSpacing = (-0.025).em
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            LogoReportaCiudad()

            Spacer(Modifier.height(64.dp))

            Text(
                text = "Santa Cruz, mirada por todos",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 28.sp,
                    lineHeight = 35.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.02).em
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onInicio,
                modifier = Modifier.defaultMinSize(minHeight = 56.dp),
                contentPadding = PaddingValues(horizontal = 32.dp)
            ) {
                Text(
                    text = "Ir al Inicio",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp)
                )
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = FlechaDerecha,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}


@Composable
private fun LogoReportaCiudad() {
    val verde = MaterialTheme.colorScheme.primary
    val crema = MaterialTheme.colorScheme.background

    Canvas(
        modifier = Modifier
            .size(132.dp)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .background(verde, CircleShape)
    ) {
        val u = size.width / 24f

        drawLine(crema, Offset(12 * u, 2 * u), Offset(12 * u, 22 * u), strokeWidth = 0.9f * u)
        drawLine(crema, Offset(2 * u, 12 * u), Offset(22 * u, 12 * u), strokeWidth = 0.9f * u)

        drawPath(estrella(u, radio = 6.5f, curva = 0.9f), crema)
        drawPath(estrella(u, radio = 3.4f, curva = 0.4f), verde)
    }
}


private fun estrella(u: Float, radio: Float, curva: Float): Path {
    val c = 12f
    return Path().apply {
        moveTo(c * u, (c - radio) * u)
        quadraticTo((c + curva) * u, (c - curva) * u, (c + radio) * u, c * u)
        quadraticTo((c + curva) * u, (c + curva) * u, c * u, (c + radio) * u)
        quadraticTo((c - curva) * u, (c + curva) * u, (c - radio) * u, c * u)
        quadraticTo((c - curva) * u, (c - curva) * u, c * u, (c - radio) * u)
        close()
    }
}


private val FlechaDerecha: ImageVector = ImageVector.Builder(
    name = "FlechaDerecha",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 2.75f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round
    ) {
        moveTo(5f, 12f)
        horizontalLineTo(19f)
        moveTo(12f, 5f)
        lineTo(19f, 12f)
        lineTo(12f, 19f)
    }
}.build()

