package com.example.reportaciudad.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.reportaciudad.data.model.EstadoReporte
import com.example.reportaciudad.data.model.MotivoRechazo
import com.example.reportaciudad.data.model.ReporteServidor
import com.example.reportaciudad.ui.screens.componentes.AvisoConexion
import com.example.reportaciudad.ui.screens.componentes.BotonVolver
import com.example.reportaciudad.ui.screens.componentes.EncabezadoReporte
import com.example.reportaciudad.ui.screens.componentes.FotosAntesDespues
import com.example.reportaciudad.ui.screens.componentes.TarjetaMapa
import com.example.reportaciudad.ui.screens.reportar.componentes.iconoDeLinea
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.EmergenciaClaro
import com.example.reportaciudad.ui.theme.EmergenciaDeshabilitado
import com.example.reportaciudad.ui.theme.Fondo
import com.example.reportaciudad.ui.theme.Neutro200
import com.example.reportaciudad.ui.theme.Neutro300
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.Superficie
import com.example.reportaciudad.ui.theme.Urbano
import com.example.reportaciudad.ui.theme.UrbanoDeshabilitado
import com.example.reportaciudad.ui.theme.UrbanoEtiqueta
import com.example.reportaciudad.ui.theme.UrbanoEtiquetaTexto
import com.example.reportaciudad.viewmodel.AdminDetalleUiState
import kotlinx.coroutines.launch

private val PasosVisibles = listOf(
    EstadoReporte.RECIBIDO,
    EstadoReporte.EN_REVISION,
    EstadoReporte.ASIGNADO,
    EstadoReporte.RESUELTO
)

@Composable
fun AdminDetalleScreen(
    estado: AdminDetalleUiState,
    onVolver: () -> Unit = {},
    onReintentar: () -> Unit = {},
    onAvanzar: () -> Unit = {},
    onMarcarResuelto: () -> Unit = {},
    onAbrirRechazo: () -> Unit = {},
    onCerrarRechazo: () -> Unit = {},
    onElegirMotivo: (MotivoRechazo) -> Unit = {},
    onConfirmarRechazo: () -> Unit = {}
) {
    val reporte = estado.reporte

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        BotonVolver(
            onClick = onVolver,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp)
        )

        when {
            reporte != null -> ContenidoDetalle(
                estado = estado,
                reporte = reporte,
                onReintentar = onReintentar,
                onAvanzar = onAvanzar,
                onMarcarResuelto = onMarcarResuelto,
                onAbrirRechazo = onAbrirRechazo
            )
            estado.errorCarga -> AvisoConexion(
                texto = "No se pudo cargar el reporte. Revisa tu conexión.",
                onReintentar = onReintentar,
                modifier = Modifier.padding(start = 16.dp, top = 48.dp, end = 16.dp)
            )
            else -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp)
                    .semantics { contentDescription = "Cargando reporte" },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Urbano)
            }
        }
    }

    if (estado.hojaRechazoAbierta && reporte != null) {
        HojaRechazo(
            estado = estado,
            onCerrar = onCerrarRechazo,
            onElegirMotivo = onElegirMotivo,
            onConfirmar = onConfirmarRechazo
        )
    }
}

@Composable
private fun ColumnScope.ContenidoDetalle(
    estado: AdminDetalleUiState,
    reporte: ReporteServidor,
    onReintentar: () -> Unit,
    onAvanzar: () -> Unit,
    onMarcarResuelto: () -> Unit,
    onAbrirRechazo: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (estado.errorCarga) {
            AvisoConexion(
                texto = "No se pudo actualizar el reporte. Revisa tu conexión.",
                onReintentar = onReintentar
            )
        }
        EncabezadoReporte(
            categoria = reporte.categoria,
            familia = reporte.familia,
            titulo = reporte.titulo,
            fecha = reporte.recepcion,
            estado = reporte.estado
        )
        if (reporte.estado != EstadoReporte.RECHAZADO) PasosEstado(reporte.estado)

        val fotoArreglo = reporte.fotoArregloUrl
        if (reporte.estado == EstadoReporte.RESUELTO && fotoArreglo != null) {
            FotosAntesDespues(antes = reporte.fotoUrl, despues = fotoArreglo)
        } else {
            FotoProblema(reporte.fotoUrl)
        }

        TarjetaMapa(latitud = reporte.latitud, longitud = reporte.longitud)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (estado.errorGuardado && !estado.hojaRechazoAbierta) AvisoErrorGuardado()

        if (reporte.estado.esFinal) {
            TarjetaCierre(reporte)
        } else {
            BotonPrincipal(
                texto = reporte.estado.textoAccion,
                guardando = estado.guardando,
                onClick = if (reporte.estado == EstadoReporte.ASIGNADO) onMarcarResuelto else onAvanzar
            )
            TextButton(
                onClick = onAbrirRechazo,
                enabled = !estado.guardando,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(
                    text = "Rechazar reporte",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                    color = if (estado.guardando) EmergenciaDeshabilitado else Emergencia
                )
            }
        }
    }
}

private val EstadoReporte.textoAccion: String
    get() = when (this) {
        EstadoReporte.RECIBIDO -> "Empezar revisión"
        EstadoReporte.EN_REVISION -> "Asignar a cuadrilla"
        else -> "Marcar como resuelto"
    }

@Composable
private fun BotonPrincipal(texto: String, guardando: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !guardando,
        colors = ButtonDefaults.buttonColors(
            containerColor = Urbano,
            contentColor = Color.White,
            disabledContainerColor = UrbanoDeshabilitado,
            disabledContentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    ) {
        if (guardando) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.5.dp,
                modifier = Modifier
                    .size(22.dp)
                    .semantics { contentDescription = "Guardando" }
            )
        } else {
            Text(text = texto, style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp))
        }
    }
}

@Composable
private fun AvisoErrorGuardado(modifier: Modifier = Modifier) {
    Text(
        text = "No se pudo guardar el cambio. Revisa tu conexión.",
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
        color = Emergencia,
        modifier = modifier
            .fillMaxWidth()
            .background(EmergenciaClaro, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

@Composable
private fun PasosEstado(estado: EstadoReporte) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Superficie, RoundedCornerShape(24.dp))
            .padding(horizontal = 8.dp, vertical = 14.dp)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PasosVisibles.forEachIndexed { indice, paso ->
            val posicion = indice + 1
            val hecho = estado == EstadoReporte.RESUELTO || posicion < estado.paso
            val actual = !hecho && posicion == estado.paso
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MarcaPaso(hecho = hecho, actual = actual)
                Text(
                    text = paso.nombre,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = if (actual) FontWeight.ExtraBold else FontWeight.Medium
                    ),
                    color = if (actual) MaterialTheme.colorScheme.onBackground else Neutro700
                )
            }
        }
    }
}

@Composable
private fun MarcaPaso(hecho: Boolean, actual: Boolean) {
    when {
        hecho -> Box(
            modifier = Modifier
                .size(26.dp)
                .background(Urbano, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(IconoCheck, contentDescription = "Hecho", tint = Color.White, modifier = Modifier.size(14.dp))
        }
        actual -> Box(
            modifier = Modifier
                .size(26.dp)
                .background(Fondo, CircleShape)
                .border(2.dp, Urbano, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(10.dp)
                    .background(Urbano, CircleShape)
            )
        }
        else -> Box(
            modifier = Modifier
                .size(26.dp)
                .background(Neutro200, CircleShape)
                .border(1.5.dp, Neutro300, CircleShape)
        )
    }
}

@Composable
private fun FotoProblema(fotoUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Neutro200),
        contentAlignment = Alignment.Center
    ) {
        if (fotoUrl != null) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = "Foto del problema",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = "Sin foto del problema",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
                color = Neutro700
            )
        }
    }
}

@Composable
private fun TarjetaCierre(reporte: ReporteServidor) {
    val resuelto = reporte.estado == EstadoReporte.RESUELTO
    val acento = if (resuelto) Urbano else Emergencia
    val textoColor = if (resuelto) UrbanoEtiquetaTexto else Emergencia
    val titulo = if (resuelto) "Resuelto" else listOfNotNull("Rechazado", reporte.motivoRechazo?.nombre).joinToString(" · ")
    val detalle = if (resuelto) "La cuadrilla terminó el trabajo." else "El vecino ve este motivo en su reporte."

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (resuelto) UrbanoEtiqueta else EmergenciaClaro, RoundedCornerShape(24.dp))
            .padding(16.dp)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(acento, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (resuelto) IconoCheck else IconoCerrar,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                color = textoColor
            )
            Text(
                text = "$detalle Este estado es final y ya no se puede cambiar.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp),
                color = textoColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HojaRechazo(
    estado: AdminDetalleUiState,
    onCerrar: () -> Unit,
    onElegirMotivo: (MotivoRechazo) -> Unit,
    onConfirmar: () -> Unit
) {
    val hoja = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val alcance = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = hoja,
        containerColor = Fondo,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Rechazar reporte",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    lineHeight = 30.sp,
                    letterSpacing = (-0.025).em
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Elige el motivo. Quien lo envió lo verá en su reporte.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = Neutro700,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            MotivoRechazo.entries.forEach { motivo ->
                OpcionMotivo(
                    motivo = motivo,
                    elegido = estado.motivoElegido == motivo,
                    habilitado = !estado.guardando,
                    onClick = { onElegirMotivo(motivo) }
                )
            }
            if (estado.errorGuardado) AvisoErrorGuardado()
            Button(
                onClick = onConfirmar,
                enabled = estado.motivoElegido != null && !estado.guardando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Emergencia,
                    contentColor = Color.White,
                    disabledContainerColor = EmergenciaDeshabilitado,
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(top = 4.dp)
            ) {
                if (estado.guardando) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier
                            .size(22.dp)
                            .semantics { contentDescription = "Guardando" }
                    )
                } else {
                    Text(
                        text = "Rechazar reporte",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp)
                    )
                }
            }
            TextButton(
                onClick = { alcance.launch { hoja.hide() }.invokeOnCompletion { onCerrar() } },
                enabled = !estado.guardando,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                    color = Neutro700
                )
            }
        }
    }
}

@Composable
private fun OpcionMotivo(motivo: MotivoRechazo, elegido: Boolean, habilitado: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .background(Superficie, RoundedCornerShape(18.dp))
            .border(
                border = if (elegido) BorderStroke(2.dp, Emergencia) else BorderStroke(0.dp, Color.Transparent),
                shape = RoundedCornerShape(18.dp)
            )
            .selectable(
                selected = elegido,
                enabled = habilitado,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RadioButton(
            selected = elegido,
            onClick = null,
            enabled = habilitado,
            colors = RadioButtonDefaults.colors(selectedColor = Emergencia, unselectedColor = Neutro700)
        )
        Text(
            text = motivo.nombre,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private val IconoCheck = iconoDeLinea("Check", 3f, "M20 6L9 17l-5-5")

private val IconoCerrar = iconoDeLinea("Cerrar", 3f, "M18 6L6 18", "M6 6l12 12")
