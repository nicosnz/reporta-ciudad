package com.example.reportaciudad.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.reportaciudad.R

// Fuente oficial de la app
val Manrope = FontFamily(
    Font(R.font.manrope_extralight, FontWeight.ExtraLight),
    Font(R.font.manrope_light, FontWeight.Light),
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
    Font(R.font.manrope_extrabold, FontWeight.ExtraBold)
)

// Estilos por defecto de Material 3, todos con Manrope:
// títulos y botones (display, headline, title, label) en 800, texto (body) en 400
private val base = Typography()

val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    displayMedium = base.displayMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    displaySmall = base.displaySmall.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    headlineLarge = base.headlineLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    headlineMedium = base.headlineMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    headlineSmall = base.headlineSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    titleLarge = base.titleLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    titleMedium = base.titleMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    titleSmall = base.titleSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    bodyLarge = base.bodyLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Normal),
    bodyMedium = base.bodyMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.Normal),
    bodySmall = base.bodySmall.copy(fontFamily = Manrope, fontWeight = FontWeight.Normal),
    labelLarge = base.labelLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    labelMedium = base.labelMedium.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold),
    labelSmall = base.labelSmall.copy(fontFamily = Manrope, fontWeight = FontWeight.ExtraBold)
)
