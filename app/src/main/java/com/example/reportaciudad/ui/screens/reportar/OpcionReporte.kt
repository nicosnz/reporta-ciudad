package com.example.reportaciudad.ui.screens.reportar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.ui.theme.Superficie

@Composable
fun TarjetaOpcion(
    nombre: String,
    icono: ImageVector,
    seleccionada: Boolean,
    colorLleno: Color,
    colorFondoIcono: Color,
    colorContenido: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = 3.dp,
                color = if (seleccionada) colorLleno else Color.Transparent,
                shape = RoundedCornerShape(34.dp)
            )
            .padding(6.dp)
    ) {
        Surface(
            selected = seleccionada,
            onClick = onClick,
            shape = RoundedCornerShape(28.dp),
            color = if (seleccionada) colorLleno else Superficie,
            contentColor = if (seleccionada) Color.White else colorContenido,
            modifier = Modifier
                .fillMaxSize()
                .semantics { role = Role.RadioButton }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            if (seleccionada) Color.White.copy(alpha = 0.2f) else colorFondoIcono,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp, lineHeight = 22.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
fun BotonSeleccionar(
    texto: String,
    habilitado: Boolean,
    color: Color,
    colorDeshabilitado: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = colorDeshabilitado,
            disabledContentColor = Color.White.copy(alpha = 0.85f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .defaultMinSize(minHeight = 56.dp)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp)
        )
    }
}
