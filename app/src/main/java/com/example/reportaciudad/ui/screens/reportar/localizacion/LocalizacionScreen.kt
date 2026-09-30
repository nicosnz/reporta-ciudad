package com.example.reportaciudad.ui.screens.reportar.localizacion

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.reportaciudad.data.UbicacionActual
import com.example.reportaciudad.ui.theme.Fondo
import com.example.reportaciudad.ui.theme.Neutro200
import com.example.reportaciudad.ui.theme.Neutro900
import com.example.reportaciudad.ui.theme.Texto
import com.example.reportaciudad.ui.theme.Urbano
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import java.io.File
import com.example.reportaciudad.ui.screens.reportar.componentes.PasoReporte
import com.example.reportaciudad.ui.screens.reportar.componentes.iconoDeLinea
import com.example.reportaciudad.ui.screens.reportar.fotografia.abrirAjustesDeLaApp

// Plaza 24 de Septiembre, Santa Cruz de la Sierra: el mapa se ve aquí mientras no hay ubicación del teléfono
private val CENTRO_SANTA_CRUZ = GeoPoint(-17.7833, -63.1821)
private const val ZOOM_CIUDAD = 15.0
internal const val ZOOM_CALLE = 18.0

private val PIN_ANCHO = 44.dp
private val PIN_ALTO = 56.dp

// El punto del reporte es la ubicación actual del teléfono: el mapa solo la muestra, no se puede mover.
@Composable
fun LocalizacionScreen(
    onVolver: () -> Unit = {},
    onConfirmar: (latitud: Double, longitud: Double) -> Unit = { _, _ -> }
) {
    PasoReporte(
        paso = 4,
        titulo = "Localización",
        subtitulo = "Marca dónde se encuentra.",
        onVolver = onVolver,
        compacto = true
    ) {
        if (LocalInspectionMode.current) {
            ZonaMapa(Modifier.weight(1f)) { PinCentral(Modifier.align(Alignment.Center)) }
            BotonConfirmar(habilitado = true, onClick = {})
        } else {
            ContenidoMapa(onConfirmar)
        }
    }
}

@Composable
private fun ColumnScope.ContenidoMapa(onConfirmar: (Double, Double) -> Unit) {
    val context = LocalContext.current
    val permiso = rememberPermisoUbicacion()
    // GeoPoint es Parcelable: se conserva al rotar o al volver desde el paso 5
    var ubicacion by rememberSaveable { mutableStateOf<GeoPoint?>(null) }
    val mapa = remember { crearMapa(context) }
    val scope = rememberCoroutineScope()
    var buscando by remember { mutableStateOf(false) }
    var sinUbicacion by remember { mutableStateOf(false) }
    var sinConexion by remember { mutableStateOf(!hayConexion(context)) }

    fun buscarUbicacion() {
        if (buscando) return
        sinConexion = !hayConexion(context)
        sinUbicacion = false
        buscando = true
        scope.launch {
            val actual = UbicacionActual.obtener(context)
            if (actual != null) {
                ubicacion = GeoPoint(actual.latitude, actual.longitude)
            } else {
                sinUbicacion = true
            }
            buscando = false
        }
    }

    // Al entrar (o apenas se da el permiso) toma la ubicación del teléfono
    LaunchedEffect(permiso.concedido) {
        if (permiso.concedido && ubicacion == null) buscarUbicacion()
    }

    LaunchedEffect(ubicacion) {
        ubicacion?.let {
            mapa.controller.setZoom(ZOOM_CALLE)
            mapa.controller.setCenter(it)
        }
    }

    LifecycleResumeEffect(mapa) {
        mapa.onResume()
        onPauseOrDispose { mapa.onPause() }
    }
    DisposableEffect(mapa) { onDispose { mapa.onDetach() } }

    ZonaMapa(Modifier.weight(1f)) {
        AndroidView(
            factory = { mapa },
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = "Mapa con tu ubicación actual" }
        )
        if (ubicacion != null) PinCentral(Modifier.align(Alignment.Center))

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!permiso.concedido) {
                AvisoSinPermiso(
                    bloqueado = permiso.bloqueado,
                    onPedir = permiso.pedir,
                    onAbrirAjustes = { abrirAjustesDeLaApp(context) }
                )
            }
            if (sinConexion) {
                Aviso("Sin señal: el mapa puede no mostrar las calles, pero se guarda la ubicación GPS del teléfono.")
            }
            if (sinUbicacion) {
                Aviso("No pudimos obtener tu ubicación. Revisa que el GPS esté activado e intenta de nuevo.")
            }
        }

        if (permiso.concedido) {
            BotonActualizar(
                buscando = buscando,
                onClick = ::buscarUbicacion,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }

    BotonConfirmar(
        habilitado = ubicacion != null,
        onClick = { ubicacion?.let { onConfirmar(it.latitude, it.longitude) } }
    )
}

// OpenStreetMap devuelve una imagen de "Access blocked" (con HTTP 200) si el User-Agent contiene "com.example"
// u "osmdroid". El MAPNIK de osmdroid manda siempre "paquete/versión" (com.example.reportaciudad/1), así que se
// usa el mismo servidor y la misma política de uso pero con el User-Agent propio de la app.
private const val USER_AGENT_OSM = "ReportaCiudad/1.0 (Android; +https://github.com/nicosnz/reporta-ciudad)"

private val TILES_OSM = XYTileSource(
    "OpenStreetMap", 0, 19, 256, ".png",
    arrayOf("https://tile.openstreetmap.org/"),
    "© OpenStreetMap contributors",
    TileSourcePolicy(
        2,
        TileSourcePolicy.FLAG_NO_BULK or TileSourcePolicy.FLAG_NO_PREVENTIVE or TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
    )
)

// Mapa de OpenStreetMap (osmdroid, tiles de tile.openstreetmap.org), solo para mostrar: ignora los toques.
@SuppressLint("ClickableViewAccessibility")
internal fun crearMapa(context: Context): MapView {
    Configuration.getInstance().apply {
        load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        // La caché de tiles va a la carpeta de caché, sin pedir permiso de almacenamiento
        userAgentValue = USER_AGENT_OSM
        osmdroidBasePath = File(context.cacheDir, "osmdroid")
        osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
    }
    return MapView(context).apply {
        setTileSource(TILES_OSM)
        setMultiTouchControls(false)
        setOnTouchListener { _, _ -> true }
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        isTilesScaledToDpi = true
        controller.setZoom(ZOOM_CIUDAD)
        controller.setCenter(CENTRO_SANTA_CRUZ)
        // "© OpenStreetMap contributors": obligatorio por la licencia de los datos y la política de tiles
        overlays.add(CopyrightOverlay(context))
    }
}

@Composable
private fun ZonaMapa(modifier: Modifier = Modifier, contenido: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Neutro200),
        content = contenido
    )
}

// La punta del pin queda exactamente en el centro del mapa (el centro de este Canvas).
@Composable
internal fun PinCentral(modifier: Modifier = Modifier, ancho: Dp = PIN_ANCHO, alto: Dp = PIN_ALTO) {
    Canvas(modifier.size(ancho, alto * 2)) {
        val ancho = size.width
        val radio = ancho / 2
        val punta = size.height / 2
        val cima = punta - alto.toPx()
        val alto = punta - cima

        drawOval(
            color = Color.Black.copy(alpha = 0.25f),
            topLeft = Offset(radio / 2, punta - 3.dp.toPx()),
            size = Size(radio, 6.dp.toPx())
        )

        val pin = Path().apply {
            moveTo(radio, punta)
            cubicTo(radio * 0.35f, punta - alto * 0.2f, 0f, cima + radio + (alto - radio) * 0.4f, 0f, cima + radio)
            arcTo(Rect(0f, cima, ancho, cima + ancho), 180f, 180f, false)
            cubicTo(ancho, cima + radio + (alto - radio) * 0.4f, ancho - radio * 0.35f, punta - alto * 0.2f, radio, punta)
            close()
        }
        drawPath(pin, Urbano)
        drawPath(pin, Color.White, style = Stroke(width = 2.dp.toPx()))
        drawCircle(Color.White, radius = radio * 0.4f, center = Offset(radio, cima + radio))
    }
}

@Composable
private fun BotonActualizar(buscando: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Fondo,
        contentColor = Texto,
        shadowElevation = 4.dp,
        modifier = modifier
            .size(52.dp)
            .semantics { contentDescription = "Actualizar mi ubicación" }
            .testTag("boton_actualizar_ubicacion")
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (buscando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(IconoUbicacion, contentDescription = null, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun BotonConfirmar(habilitado: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 16.dp)
            .defaultMinSize(minHeight = 56.dp)
            .testTag("boton_confirmar_ubicacion")
    ) {
        Text("Confirmar ubicación", style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp))
    }
}

@Composable
private fun Aviso(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
        color = Color.White,
        modifier = Modifier
            .background(Neutro900.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
private fun AvisoSinPermiso(bloqueado: Boolean, onPedir: () -> Unit, onAbrirAjustes: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Fondo, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (bloqueado) {
                "El permiso de ubicación está desactivado. Actívalo en los ajustes para guardar dónde está el problema."
            } else {
                "Usamos tu ubicación para guardar dónde está el problema y que la cuadrilla lo encuentre."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Texto
        )
        Button(
            onClick = if (bloqueado) onAbrirAjustes else onPedir,
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .testTag("boton_permiso_ubicacion")
        ) {
            Text(if (bloqueado) "Abrir ajustes" else "Permitir ubicación")
        }
    }
}


private class PermisoUbicacion(val concedido: Boolean, val bloqueado: Boolean, val pedir: () -> Unit)

@Composable
private fun rememberPermisoUbicacion(): PermisoUbicacion {
    val context = LocalContext.current
    val activity = LocalActivity.current
    var concedido by remember { mutableStateOf(UbicacionActual.tienePermiso(context)) }
    var bloqueado by remember { mutableStateOf(false) }
    val permisos = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
        concedido = resultado.values.any { it }
        bloqueado = !concedido && activity != null &&
            permisos.none { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
    }

    // Si lo activó desde los ajustes, al volver a la app se entera
    LifecycleResumeEffect(Unit) {
        concedido = UbicacionActual.tienePermiso(context)
        if (concedido) bloqueado = false
        onPauseOrDispose { }
    }

    var pedidoInicial by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!concedido && !pedidoInicial) {
            pedidoInicial = true
            lanzador.launch(permisos)
        }
    }

    return PermisoUbicacion(concedido, bloqueado) { lanzador.launch(permisos) }
}

private fun hayConexion(context: Context): Boolean {
    val manager = context.getSystemService(ConnectivityManager::class.java) ?: return false
    val red = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
    return red.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        red.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

private val IconoUbicacion = iconoDeLinea("Ubicacion", 2.25f, "M3 11L22 2L13 21L11 13Z")
