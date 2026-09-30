package com.example.reportaciudad.ui.screens.reportar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reportaciudad.data.model.BorradorReporte
import com.example.reportaciudad.ui.theme.Neutro700

@Composable
fun ResumenScreen(borrador: BorradorReporte, onVolver: () -> Unit = {}) {
    PasoReporte(
        paso = 5,
        titulo = "Resumen",
        subtitulo = "Revisa tu reporte antes de enviarlo.",
        onVolver = onVolver
    ) {
        Text(
            text = "Próximamente\nUbicación: ${borrador.latitud}, ${borrador.longitud}",
            style = MaterialTheme.typography.bodyLarge,
            color = Neutro700,
            modifier = Modifier.padding(start = 4.dp, top = 48.dp, end = 4.dp)
        )
    }
}
