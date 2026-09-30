package com.example.reportaciudad.ui.screens.reportar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reportaciudad.data.model.Familia
import com.example.reportaciudad.data.model.TipoProblema
import com.example.reportaciudad.ui.theme.AmbientalEtiqueta
import com.example.reportaciudad.ui.theme.AmbientalEtiquetaTexto
import com.example.reportaciudad.ui.theme.AmbientalOscuro
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme
import com.example.reportaciudad.ui.theme.UrbanoDeshabilitado
import com.example.reportaciudad.ui.theme.UrbanoEtiqueta
import com.example.reportaciudad.ui.theme.UrbanoEtiquetaTexto
import com.example.reportaciudad.ui.theme.UrbanoOscuro

@Composable
fun SeleccionProblemaScreen(
    onVolver: () -> Unit = {},
    onSeleccionar: (TipoProblema) -> Unit = {}
) {
    var seleccion by rememberSaveable { mutableStateOf<TipoProblema?>(null) }

    PasoReporte(
        paso = 2,
        titulo = "Reporte",
        subtitulo = "Contanos qué encontraste.",
        onVolver = onVolver
    ) {
        Spacer(Modifier.height(18.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TipoProblema.entries.chunked(2).forEach { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    fila.forEach { tipo ->
                        val (lleno, fondoIcono, contenido) = tipo.familia.colores
                        TarjetaOpcion(
                            nombre = tipo.nombre,
                            icono = tipo.icono,
                            seleccionada = seleccion == tipo,
                            colorLleno = lleno,
                            colorFondoIcono = fondoIcono,
                            colorContenido = contenido,
                            onClick = { seleccion = if (seleccion == tipo) null else tipo },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("opcion_${tipo.name.lowercase()}")
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        BotonSeleccionar(
            texto = "Seleccionar problema",
            habilitado = seleccion != null,
            color = UrbanoOscuro,
            colorDeshabilitado = UrbanoDeshabilitado,
            onClick = { seleccion?.let(onSeleccionar) },
            modifier = Modifier.testTag("boton_seleccionar_problema")
        )
    }
}

private val Familia.colores: Triple<Color, Color, Color>
    get() = when (this) {
        Familia.URBANO -> Triple(UrbanoOscuro, UrbanoEtiqueta, UrbanoEtiquetaTexto)
        Familia.AMBIENTAL -> Triple(AmbientalOscuro, AmbientalEtiqueta, AmbientalEtiquetaTexto)
    }

internal val TipoProblema.icono: ImageVector
    get() = when (this) {
        TipoProblema.BASURA -> IconoBasura
        TipoProblema.BACHE -> IconoAuto
        TipoProblema.ALUMBRADO -> IconoFoco
        TipoProblema.FUGA_DE_AGUA -> IconoGota
    }

private val IconoBasura = iconoDeLinea(
    "Basura", 2f,
    "M4 7h16",
    "M10 11v6M14 11v6",
    "M5 7l1 12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2l1-12",
    "M9 7V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v3"
)

private val IconoAuto = iconoDeLinea(
    "Auto", 2f,
    "M3 17a2 2 0 1 0 4 0a2 2 0 1 0-4 0",
    "M15 17a2 2 0 1 0 4 0a2 2 0 1 0-4 0",
    "M5 17H3v-6l2-5h9l4 5h1a2 2 0 0 1 2 2v4h-2m-4 0H9m-6-6h15m-6 0V6"
)

private val IconoFoco = iconoDeLinea(
    "Foco", 2f,
    "M9 16a5 5 0 1 1 6 0a3.5 3.5 0 0 0-1 3a2 2 0 0 1-4 0a3.5 3.5 0 0 0-1-3",
    "M9.7 17h4.6"
)

private val IconoGota = iconoDeLinea(
    "Gota", 2f,
    "M7.5 19.42c2.6 2.1 6.4 2.1 9 0c2.6-2.1 3.26-5.71 1.57-8.55l-4.9-7.26c-.42-.62-1.28-.8-1.93-.4a1.38 1.38 0 0 0-.41.4l-4.9 7.26c-1.69 2.84-1.03 6.45 1.57 8.55z"
)

