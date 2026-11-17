package com.example.reportaciudad.ui.screens.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.Familia
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.Reporte
import com.example.reportaciudad.data.model.TipoProblema
import com.example.reportaciudad.ui.screens.componentes.BarraEstado
import com.example.reportaciudad.ui.screens.componentes.explicacion
import com.example.reportaciudad.ui.theme.Fondo
import com.example.reportaciudad.ui.theme.Neutro900
import com.example.reportaciudad.ui.theme.UrbanoOscuro
import com.example.reportaciudad.viewmodel.DetalleUiState
import java.io.File
import com.example.reportaciudad.ui.theme.EmergenciaClaro
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.AmbientalEtiqueta
import com.example.reportaciudad.ui.theme.AmbientalEtiquetaTexto
import com.example.reportaciudad.ui.theme.Neutro200
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme
import com.example.reportaciudad.ui.theme.Superficie
import com.example.reportaciudad.ui.theme.UrbanoEtiqueta
import com.example.reportaciudad.ui.theme.UrbanoEtiquetaTexto

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
        Encabezado(reporte)

        if (reporte.enviado) TarjetaEstado(reporte) else TarjetaSinEnviar()

        val fotoArreglo = reporte.fotoArregloUrl
        if (reporte.estado == EstadoReporte.RESUELTO && fotoArreglo != null) {
            FotosAntesDespues(antes = reporte.modeloFoto(), despues = fotoArreglo)
        } else {
            // Sin foto no se muestra ninguna de ejemplo
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

        TarjetaUbicacion(lugar = reporte.lugar)
    }
}

// La foto del teléfono si todavía está (carga sin internet); si no, la de Cloudinary
private fun Reporte.modeloFoto(): Any? =
    fotoRuta?.let(::File)?.takeIf { it.exists() } ?: fotoUrl

@Composable
private fun FotosAntesDespues(antes: Any?, despues: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FotoConEtiqueta(
            modelo = antes,
            etiqueta = "Antes",
            colorEtiqueta = Neutro700,
            descripcion = "Tu foto, antes del arreglo",
            modifier = Modifier.weight(1f)
        )
        FotoConEtiqueta(
            modelo = despues,
            etiqueta = "Después",
            colorEtiqueta = UrbanoOscuro,
            descripcion = "Foto del arreglo",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FotoConEtiqueta(
    modelo: Any?,
    etiqueta: String,
    colorEtiqueta: Color,
    descripcion: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(190.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Neutro200)
    ) {
        if (modelo != null) {
            AsyncImage(
                model = modelo,
                contentDescription = descripcion,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = colorEtiqueta,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .background(Fondo.copy(alpha = 0.92f), CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun BotonVolver(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Superficie,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = IconoVolver,
                contentDescription = "Volver",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
private fun Encabezado(reporte: Reporte) {
    val (fondoEtiqueta, textoEtiqueta) = when (reporte.familia) {
        Familia.URBANO -> UrbanoEtiqueta to UrbanoEtiquetaTexto
        Familia.AMBIENTAL -> AmbientalEtiqueta to AmbientalEtiquetaTexto
        Familia.EMERGENCIA -> EmergenciaClaro to Emergencia
    }

    Column(
        modifier = Modifier.padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = reporte.categoria,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
            color = textoEtiqueta,
            modifier = Modifier
                .background(fondoEtiqueta, CircleShape)
                .padding(horizontal = 12.dp, vertical = 5.dp)
        )
        Text(
            text = reporte.titulo,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp,
                lineHeight = 32.sp,
                letterSpacing = (-0.025).em
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Enviado ${reporte.fecha}",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = Neutro700
        )
    }
}


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

// El reporte quedó guardado en el teléfono y todavía no llegó al servidor: no tiene estado
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

@Composable
private fun TarjetaUbicacion(lugar: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Superficie, RoundedCornerShape(28.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        PinUbicacion()
        Column {
            Text(
                text = lugar,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Santa Cruz de la Sierra",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = Neutro700
            )
        }
    }
}

// Gota verde con un punto crema: una forma con tres esquinas redondas y una en punta, girada -45°
@Composable
private fun PinUbicacion() {
    val formaGota = RoundedCornerShape(
        topStart = CornerSize(50),
        topEnd = CornerSize(50),
        bottomEnd = CornerSize(50),
        bottomStart = CornerSize(4.dp)
    )
    Box(
        modifier = Modifier
            .size(36.dp)
            .rotate(-45f)
            .shadow(4.dp, formaGota)
            .background(MaterialTheme.colorScheme.primary, formaGota),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(12.dp)
                .background(MaterialTheme.colorScheme.background, CircleShape)
        )
    }
}

private val IconoVolver: ImageVector = ImageVector.Builder(
    name = "Volver",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    listOf("M12 19l-7-7 7-7", "M19 12H5").forEach { trazo ->
        addPath(
            pathData = addPathNodes(trazo),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2.75f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        )
    }
}.build()

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DetalleScreenPreview() {
    ReportaCiudadTheme {
        DetalleScreen(
            estado = DetalleUiState.Encontrado(
                Reporte(id = 1, problema = TipoProblema.BACHE, latitud = -17.7833, longitud = -63.1821)
            )
        )
    }
}
