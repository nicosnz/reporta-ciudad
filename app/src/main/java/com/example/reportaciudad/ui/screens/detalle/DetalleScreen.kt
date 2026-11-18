package com.example.reportaciudad.ui.screens.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.ui.screens.componentes.BarraEstado
import com.example.reportaciudad.ui.screens.componentes.BotonVolver
import com.example.reportaciudad.ui.screens.componentes.EncabezadoReporte
import com.example.reportaciudad.ui.screens.componentes.FotosAntesDespues
import com.example.reportaciudad.ui.screens.componentes.TarjetaMapa
import com.example.reportaciudad.ui.screens.componentes.explicacion
import com.example.reportaciudad.ui.theme.Neutro900
import com.example.reportaciudad.viewmodel.DetalleUiState
import java.io.File
import com.example.reportaciudad.ui.theme.EmergenciaClaro
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.Neutro200
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.Superficie

@Composable
fun DetalleScreen(estado: DetalleUiState, onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        BotonVolver(
            onClick = onBack,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp)
        )

        when (estado) {
            DetalleUiState.Cargando -> Unit
            DetalleUiState.NoExiste -> Text(
                text = "Este reporte ya no existe.",
                style = MaterialTheme.typography.bodyLarge,
                color = Neutro700,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
            )
            is DetalleUiState.Encontrado -> ContenidoDetalle(estado.reporte)
        }
    }
}

@Composable
private fun ColumnScope.ContenidoDetalle(reporte: Reporte) {
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EncabezadoReporte(
            categoria = reporte.categoria,
            familia = reporte.familia,
            titulo = reporte.titulo,
            fecha = "Enviado ${reporte.fecha}"
        )

        if (reporte.enviado) TarjetaEstado(reporte) else TarjetaSinEnviar()

        val fotoArreglo = reporte.fotoArregloUrl
        if (reporte.estado == EstadoReporte.RESUELTO && fotoArreglo != null) {
            FotosAntesDespues(
                antes = reporte.modeloFoto(),
                despues = fotoArreglo,
                descripcionAntes = "Tu foto, antes del arreglo"
            )
        } else {
            reporte.modeloFoto()?.let { foto ->
                AsyncImage(
                    model = foto,
                    contentDescription = "Foto del reporte",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Neutro200)
                )
            }
        }

        TarjetaMapa(latitud = reporte.latitud, longitud = reporte.longitud)
    }
}

private fun Reporte.modeloFoto(): Any? =
    fotoRuta?.let(::File)?.takeIf { it.exists() } ?: fotoUrl

@Composable
private fun TarjetaEstado(reporte: Reporte) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Superficie, RoundedCornerShape(24.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BarraEstado(estado = reporte.estado, anchoSegmento = 22.dp, altoSegmento = 5.dp, tamanoTexto = 15.sp)
        Text(
            text = reporte.estado.explicacion,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 21.sp),
            color = MaterialTheme.colorScheme.onBackground
        )
        if (reporte.estado == EstadoReporte.RECHAZADO) {
            reporte.motivoRechazo?.let { MotivoRechazoCaja(it) }
        }
    }
}

@Composable
private fun MotivoRechazoCaja(motivo: MotivoRechazo) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("Motivo: ${motivo.nombre}.") }
            if (motivo.explicacion.isNotEmpty()) append(" ${motivo.explicacion}")
        },
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 19.sp),
        color = Emergencia,
        modifier = Modifier
            .fillMaxWidth()
            .background(EmergenciaClaro, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    )
}

@Composable
private fun TarjetaSinEnviar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Superficie, RoundedCornerShape(24.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Sin enviar",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
            color = Neutro900
        )
        Text(
            text = "Todavía no llegó a la municipalidad. Lo enviamos cuando vuelva la señal.",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 21.sp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
