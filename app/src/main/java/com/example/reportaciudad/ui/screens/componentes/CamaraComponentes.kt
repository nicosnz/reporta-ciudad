package com.example.reportaciudad.ui.screens.componentes

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.example.reportaciudad.ui.theme.Neutro900

@Composable
fun ZonaCamara(modifier: Modifier = Modifier, contenido: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(Neutro900),
        content = contenido
    )
}

@Composable
fun ZonaAcciones(contenido: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
        content = contenido
    )
}

@Composable
fun GuiasEncuadre() {
    Canvas(
        Modifier
            .fillMaxSize()
            .padding(22.dp)
    ) {
        val largo = 36.dp.toPx()
        val radio = 18.dp.toPx()
        val trazo = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        val esquina = Path().apply {
            moveTo(0f, largo)
            lineTo(0f, radio)
            arcTo(Rect(0f, 0f, radio * 2, radio * 2), 180f, 90f, false)
            lineTo(largo, 0f)
        }

        listOf(1f to 1f, -1f to 1f, 1f to -1f, -1f to -1f).forEach { (sx, sy) ->
            scale(sx, sy, pivot = Offset(size.width / 2, size.height / 2)) {
                drawPath(esquina, Color.White, style = trazo)
            }
        }
    }
}

@Composable
fun AvisoSobreCamara(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
        color = Color.White,
        modifier = modifier
            .background(Neutro900.copy(alpha = 0.6f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
fun SinPermiso(bloqueado: Boolean, onPedir: () -> Unit, onAbrirAjustes: () -> Unit, textoPedir: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(IconoCamara, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        Text(
            text = if (bloqueado) {
                "El permiso de cámara está desactivado. Actívalo en los ajustes para sacar la foto."
            } else {
                textoPedir
            },
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = if (bloqueado) onAbrirAjustes else onPedir,
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .testTag("boton_permiso_camara")
        ) {
            Text(if (bloqueado) "Abrir ajustes" else "Permitir cámara")
        }
    }
}

@Composable
fun BotonDisparo(habilitado: Boolean, onClick: () -> Unit) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(if (presionado) 0.94f else 1f, label = "escalaDisparo")

    Box(
        modifier = Modifier
            .size(96.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
                alpha = if (habilitado) 1f else 0.5f
            }
            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .padding(8.dp)
            .shadow(6.dp, CircleShape)
            .background(Color.White, CircleShape)
            .clickable(
                interactionSource = interaccion,
                indication = null,
                enabled = habilitado,
                role = Role.Button,
                onClick = onClick
            )
            .semantics { contentDescription = "Tomar foto" }
            .testTag("boton_tomar_foto")
    )
}

class PermisoCamara(val concedido: Boolean, val bloqueado: Boolean, val pedir: () -> Unit)

@Composable
fun rememberPermisoCamara(): PermisoCamara {
    val context = LocalContext.current
    val activity = LocalActivity.current
    var concedido by remember { mutableStateOf(tienePermisoCamara(context)) }
    var bloqueado by remember { mutableStateOf(false) }

    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        concedido = ok
        bloqueado = !ok && activity != null &&
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
    }

    LifecycleResumeEffect(Unit) {
        concedido = tienePermisoCamara(context)
        if (concedido) bloqueado = false
        onPauseOrDispose { }
    }

    var pedidoInicial by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!concedido && !pedidoInicial) {
            pedidoInicial = true
            lanzador.launch(Manifest.permission.CAMERA)
        }
    }

    return PermisoCamara(concedido, bloqueado) { lanzador.launch(Manifest.permission.CAMERA) }
}

private fun tienePermisoCamara(context: Context) =
    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED

fun abrirAjustesDeLaApp(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
