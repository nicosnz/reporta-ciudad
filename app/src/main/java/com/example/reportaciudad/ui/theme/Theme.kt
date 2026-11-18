package com.example.reportaciudad.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Urbano,
    onPrimary = Color.White,
    secondary = Superficie,
    tertiary = Ambiental,
    background = Fondo,
    onBackground = Texto,
    surface = Fondo,
    onSurface = Texto

)

@Composable
fun ReportaCiudadTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
