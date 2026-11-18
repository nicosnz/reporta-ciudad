package com.example.reportaciudad.ui.screens.reportar.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.ui.screens.componentes.BotonVolver
import com.example.reportaciudad.ui.theme.Neutro700

const val TOTAL_PASOS = 5

// Estructura común de cada paso del flujo de reporte: barra de progreso, volver, título y subtítulo.
// Cada pantalla pone su contenido debajo sin tocar este encabezado.
@Composable
fun PasoReporte(
    paso: Int,
    titulo: String,
    subtitulo: String,
    onVolver: () -> Unit,
    // Compacto: volver y título en la misma fila, para pasos que necesitan todo el alto (cámara, mapa)
    compacto: Boolean = false,
    contenido: @Composable ColumnScope.() -> Unit
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
            paso = paso,
            total = TOTAL_PASOS,
            modifier = Modifier.padding(start = 4.dp, top = 12.dp, end = 4.dp)
        )

        if (compacto) {
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BotonVolver(onClick = onVolver)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 26.sp,
                            lineHeight = 30.sp,
                            letterSpacing = (-0.025).em
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.semantics { heading() }
                    )
                    Text(
                        text = subtitulo,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                        color = Neutro700
                    )
                }
            }
        } else {
            BotonVolver(
                onClick = onVolver,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    letterSpacing = (-0.025).em
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(start = 4.dp, top = 24.dp, end = 4.dp)
                    .semantics { heading() }
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
                color = Neutro700,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp)
            )
        }

        contenido()
    }
}
