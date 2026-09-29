package com.example.reportaciudad.ui.screens.reportar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.ui.theme.Neutro300
import com.example.reportaciudad.ui.theme.Texto

// Barra de pasos del flujo de reporte: segmentos completados en verde y "Paso X de N" a la derecha
@Composable
fun BarraProgreso(paso: Int, total: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = "Paso $paso de $total" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(total) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .background(
                            if (i < paso) MaterialTheme.colorScheme.primary else Neutro300,
                            CircleShape
                        )
                )
            }
        }
        Text(
            text = "Paso $paso de $total",
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = Texto
        )
    }
}
