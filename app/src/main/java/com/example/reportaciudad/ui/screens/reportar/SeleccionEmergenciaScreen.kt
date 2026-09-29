package com.example.reportaciudad.ui.screens.reportar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.data.model.TipoEmergencia
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.EmergenciaClaro
import com.example.reportaciudad.ui.theme.EmergenciaDeshabilitado
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme
import com.example.reportaciudad.ui.theme.Superficie

@Composable
fun SeleccionEmergenciaScreen(
    onVolver: () -> Unit = {},
    onSeleccionar: (TipoEmergencia) -> Unit = {}
) {
    var seleccion by rememberSaveable { mutableStateOf<TipoEmergencia?>(null) }

    PasoReporte(
        paso = 2,
        titulo = "Emergencia",
        subtitulo = "Elegí esta opción solo si está pasando frente a vos.",
        onVolver = onVolver
    ) {
        Spacer(Modifier.height(34.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TipoEmergencia.entries.forEach { tipo ->
                TarjetaEmergencia(
                    tipo = tipo,
                    seleccionada = seleccion == tipo,
                    onClick = { seleccion = if (seleccion == tipo) null else tipo },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("opcion_${tipo.name.lowercase()}")
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { seleccion?.let(onSeleccionar) },
            enabled = seleccion != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = Emergencia,
                contentColor = Color.White,
                disabledContainerColor = EmergenciaDeshabilitado,
                disabledContentColor = Color.White.copy(alpha = 0.85f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .defaultMinSize(minHeight = 56.dp)
                .testTag("boton_seleccionar_emergencia")
        ) {
            Text(
                text = "Seleccionar emergencia",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp)
            )
        }
    }
}

@Composable
private fun TarjetaEmergencia(
    tipo: TipoEmergencia,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = 3.dp,
                color = if (seleccionada) Emergencia else Color.Transparent,
                shape = RoundedCornerShape(34.dp)
            )
            .padding(6.dp)
    ) {
        Surface(
            selected = seleccionada,
            onClick = onClick,
            shape = RoundedCornerShape(28.dp),
            color = if (seleccionada) Emergencia else Superficie,
            contentColor = if (seleccionada) Color.White else Emergencia,
            modifier = Modifier
                .fillMaxSize()
                .semantics { role = Role.RadioButton }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            if (seleccionada) Color.White.copy(alpha = 0.2f) else EmergenciaClaro,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = tipo.icono,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = tipo.nombre,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp, lineHeight = 22.sp),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

private val TipoEmergencia.icono: ImageVector
    get() = when (this) {
        TipoEmergencia.INCENDIO -> IconoFuego
        TipoEmergencia.HUMO -> IconoHumo
    }

private val IconoFuego = iconoDeLinea(
    "Fuego", 2f,
    "M12 12c2-2.96 0-7-1-8 0 3.04-1.77 4.74-3 6-1.23 1.26-2 3.24-2 5a6 6 0 1 0 12 0c0-1.53-1.06-3.94-2-5-1.79 3-2.79 3-4 2z"
)

private val IconoHumo = iconoDeLinea(
    "Humo", 2f,
    "M7 14a4.6 4.4 0 0 1 0-9 5 4.5 0 0 1 11 2h1a3.5 3.5 0 0 1 0 7",
    "M5 17h14",
    "M8 20h8"
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SeleccionEmergenciaScreenPreview() {
    ReportaCiudadTheme {
        SeleccionEmergenciaScreen()
    }
}
