package com.example.reportaciudad.ui.screens.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.ui.theme.Superficie

// Cerrar sesión: píldora "Salir" del encabezado (vecino y admin)
@Composable
fun BotonSalir(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Superficie,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.heightIn(min = 44.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = IconoSalir, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(
                text = "Salir",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp)
            )
        }
    }
}

private val IconoSalir: ImageVector = ImageVector.Builder(
    name = "Salir",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    listOf(
        "M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4",
        "M16 17l5-5-5-5",
        "M21 12H9"
    ).forEach { trazo ->
        addPath(
            pathData = addPathNodes(trazo),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2.25f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        )
    }
}.build()
