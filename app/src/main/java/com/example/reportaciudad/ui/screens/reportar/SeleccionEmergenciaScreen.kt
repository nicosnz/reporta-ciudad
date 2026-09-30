package com.example.reportaciudad.ui.screens.reportar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reportaciudad.data.model.TipoEmergencia
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.EmergenciaClaro
import com.example.reportaciudad.ui.theme.EmergenciaDeshabilitado
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme

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
                TarjetaOpcion(
                    nombre = tipo.nombre,
                    icono = tipo.icono,
                    seleccionada = seleccion == tipo,
                    colorLleno = Emergencia,
                    colorFondoIcono = EmergenciaClaro,
                    colorContenido = Emergencia,
                    onClick = { seleccion = if (seleccion == tipo) null else tipo },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("opcion_${tipo.name.lowercase()}")
                )
            }
        }

        Spacer(Modifier.weight(1f))

        BotonSeleccionar(
            texto = "Seleccionar emergencia",
            habilitado = seleccion != null,
            color = Emergencia,
            colorDeshabilitado = EmergenciaDeshabilitado,
            onClick = { seleccion?.let(onSeleccionar) },
            modifier = Modifier.testTag("boton_seleccionar_emergencia")
        )
    }
}

internal val TipoEmergencia.icono: ImageVector
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

