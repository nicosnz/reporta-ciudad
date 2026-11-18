package com.example.reportaciudad.ui.screens.componentes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

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

internal val IconoCheck = iconoDeLinea("Check", 3f, "M20 6L9 17l-5-5")

internal val IconoVolver = iconoDeLinea("Volver", 2.75f, "M12 19l-7-7 7-7", "M19 12H5")

internal val IconoChevron = iconoDeLinea("Chevron", 2.5f, "M9 18l6-6-6-6")

internal val IconoCamara = iconoDeLinea(
    "Camara", 2.75f,
    "M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z",
    "M9 13a3 3 0 1 0 6 0a3 3 0 1 0 -6 0"
)
