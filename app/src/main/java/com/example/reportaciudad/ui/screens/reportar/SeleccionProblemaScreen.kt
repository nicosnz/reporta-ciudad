package com.example.reportaciudad.ui.screens.reportar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme

// Paso 2 del flujo de reporte: selección de problema.
// TODO: el contenido llega con su propia HU; por ahora solo el encabezado del paso.
@Composable
fun SeleccionProblemaScreen(onVolver: () -> Unit = {}) {
    PasoReporte(
        paso = 2,
        titulo = "¿Qué problema quieres reportar?",
        subtitulo = "Elige el tipo de problema.",
        onVolver = onVolver
    ) {
        Text(
            text = "Próximamente",
            style = MaterialTheme.typography.bodyLarge,
            color = Neutro700,
            modifier = Modifier.padding(start = 4.dp, top = 48.dp, end = 4.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SeleccionProblemaScreenPreview() {
    ReportaCiudadTheme {
        SeleccionProblemaScreen()
    }
}
