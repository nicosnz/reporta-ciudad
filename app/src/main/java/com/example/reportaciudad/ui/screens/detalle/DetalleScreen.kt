package com.example.reportaciudad.ui.screens.detalle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.reportaciudad.R
import com.example.reportaciudad.data.RepositorioReportes
import com.example.reportaciudad.data.model.Familia
import com.example.reportaciudad.data.model.Reporte
import java.io.File
import com.example.reportaciudad.ui.theme.AmbientalEtiqueta
import com.example.reportaciudad.ui.theme.AmbientalEtiquetaTexto
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme
import com.example.reportaciudad.ui.theme.Superficie
import com.example.reportaciudad.ui.theme.UrbanoEtiqueta
import com.example.reportaciudad.ui.theme.UrbanoEtiquetaTexto

@Composable
fun DetalleScreen(onBack:() -> Unit = {}, reporteId: Int) {

    val reporte = RepositorioReportes.buscar(reporteId) ?: return

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

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Encabezado(reporte)

            val modificadorFoto = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(32.dp))
            if (reporte.fotoRuta != null) {
                // Foto sacada con la cámara al crear el reporte
                AsyncImage(
                    model = File(reporte.fotoRuta),
                    contentDescription = "Foto del reporte",
                    contentScale = ContentScale.Crop,
                    modifier = modificadorFoto
                )
            } else {
                // Reportes de ejemplo: foto de muestra
                Image(
                    painter = painterResource(R.drawable.bache_foto),
                    contentDescription = "Foto del reporte",
                    contentScale = ContentScale.Crop,
                    modifier = modificadorFoto
                )
            }

            TarjetaUbicacion(lugar = reporte.lugar)
        }
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

