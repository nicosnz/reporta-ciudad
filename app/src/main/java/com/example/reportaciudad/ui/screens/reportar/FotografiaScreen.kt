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

@Composable
fun FotografiaScreen(onVolver: () -> Unit = {}) {
    PasoReporte(
        paso = 3,
        titulo = "Fotografía",
        subtitulo = "Sacá una foto de lo que está pasando.",
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
private fun FotografiaScreenPreview() {
    ReportaCiudadTheme {
        FotografiaScreen()
    }
}
