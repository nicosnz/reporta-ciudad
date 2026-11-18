package com.example.reportaciudad.ui.screens.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.reportaciudad.ui.theme.Fondo
import com.example.reportaciudad.ui.theme.Neutro200
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.UrbanoOscuro

@Composable
fun FotosAntesDespues(
    antes: Any?,
    despues: String,
    descripcionAntes: String = "Foto del problema, antes del arreglo",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FotoConEtiqueta(
            modelo = antes,
            etiqueta = "Antes",
            colorEtiqueta = Neutro700,
            descripcion = descripcionAntes,
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
