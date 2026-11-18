package com.example.reportaciudad.ui.screens.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.ui.screens.reportar.localizacion.PinCentral
import com.example.reportaciudad.ui.screens.reportar.resumen.MapaFijo
import com.example.reportaciudad.ui.theme.Neutro200
import com.example.reportaciudad.ui.theme.Superficie

@Composable
fun TarjetaMapa(latitud: Double, longitud: Double, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Superficie, RoundedCornerShape(28.dp))
            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Neutro200)
        ) {
            MapaFijo(latitud, longitud, Modifier.fillMaxSize())
            PinCentral(Modifier.align(Alignment.Center), ancho = 28.dp, alto = 36.dp)
        }
        Text(
            text = "Santa Cruz de la Sierra",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
