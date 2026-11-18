package com.example.reportaciudad.ui.screens.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.ui.theme.AmbientalOscuro
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.EstadoAsignado
import com.example.reportaciudad.ui.theme.Neutro300
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.Urbano
import com.example.reportaciudad.ui.theme.UrbanoOscuro

val EstadoReporte.color: Color
    get() = when (this) {
        EstadoReporte.RECIBIDO -> Neutro700
        EstadoReporte.EN_REVISION -> AmbientalOscuro
        EstadoReporte.ASIGNADO -> EstadoAsignado
        EstadoReporte.RESUELTO -> UrbanoOscuro
        EstadoReporte.RECHAZADO -> Emergencia
        EstadoReporte.CANCELADO -> Neutro700
    }

val EstadoReporte.explicacion: String
    get() = when (this) {
        EstadoReporte.RECIBIDO -> "Tu reporte llegó a la municipalidad. Todavía nadie lo revisó."
        EstadoReporte.EN_REVISION ->
            "La municipalidad ya vio tu reporte y lo está evaluando. Todavía no se mandó a nadie a arreglarlo."
        EstadoReporte.ASIGNADO ->
            "Tu reporte ya está en manos del equipo que lo va a arreglar, como la cuadrilla de bacheo. " +
                "Todavía no está terminado."
        EstadoReporte.RESUELTO -> "¡Listo! El problema ya se arregló. Abajo puedes ver cómo quedó. Gracias por avisar."
        EstadoReporte.RECHAZADO -> "La municipalidad no va a atender este reporte."
        EstadoReporte.CANCELADO -> "Cancelaste este reporte antes de que lo revisaran. La municipalidad no lo va a atender."
    }

@Composable
fun BarraEstado(
    estado: EstadoReporte,
    modifier: Modifier = Modifier,
    anchoSegmento: Dp = 18.dp,
    altoSegmento: Dp = 4.dp,
    tamanoTexto: TextUnit = 12.sp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(4) { i ->
            val color = when {
                estado == EstadoReporte.RECHAZADO -> Emergencia
                estado == EstadoReporte.CANCELADO -> Neutro300
                i < estado.paso -> Urbano
                else -> Neutro300
            }
            Box(
                Modifier
                    .size(anchoSegmento, altoSegmento)
                    .background(color, RoundedCornerShape(altoSegmento / 2))
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = estado.nombre,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = tamanoTexto,
                fontWeight = FontWeight.ExtraBold
            ),
            color = estado.color
        )
    }
}
