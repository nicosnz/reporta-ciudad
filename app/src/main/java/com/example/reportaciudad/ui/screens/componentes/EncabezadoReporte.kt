package com.example.reportaciudad.ui.screens.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.Familia
import com.example.reportaciudad.ui.theme.AmbientalEtiqueta
import com.example.reportaciudad.ui.theme.AmbientalEtiquetaTexto
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.EmergenciaClaro
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.UrbanoEtiqueta
import com.example.reportaciudad.ui.theme.UrbanoEtiquetaTexto

@Composable
fun EncabezadoReporte(
    categoria: String,
    familia: Familia,
    titulo: String,
    fecha: String,
    modifier: Modifier = Modifier,
    estado: EstadoReporte? = null
) {
    val (fondoEtiqueta, textoEtiqueta) = when (familia) {
        Familia.URBANO -> UrbanoEtiqueta to UrbanoEtiquetaTexto
        Familia.AMBIENTAL -> AmbientalEtiqueta to AmbientalEtiquetaTexto
        Familia.EMERGENCIA -> EmergenciaClaro to Emergencia
    }

    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = categoria,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                color = textoEtiqueta,
                modifier = Modifier
                    .background(fondoEtiqueta, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            )
            if (estado != null) BarraEstado(estado = estado)
        }
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                lineHeight = 32.sp,
                letterSpacing = (-0.025).em
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = fecha,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = Neutro700
        )
    }
}
