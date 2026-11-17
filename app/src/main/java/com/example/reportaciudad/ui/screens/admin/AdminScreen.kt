package com.example.reportaciudad.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.ui.screens.componentes.BotonSalir
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme
import com.example.reportaciudad.ui.theme.Superficie

// Provisoria: el panel para gestionar reportes es otra historia
@Composable
fun AdminScreen(
    nombre: String,
    onCerrarSesion: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(start = 16.dp, top = 20.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reportes",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        lineHeight = 40.sp,
                        letterSpacing = (-0.025).em
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Administrador · $nombre",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
                    color = Neutro700
                )
            }
            BotonSalir(onClick = onCerrarSesion)
        }

        Text(
            text = "El panel para revisar, asignar y cerrar reportes llega en la próxima versión.",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .background(Superficie, RoundedCornerShape(24.dp))
                .padding(18.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminScreenPreview() {
    ReportaCiudadTheme {
        AdminScreen(nombre = "admin1")
    }
}
