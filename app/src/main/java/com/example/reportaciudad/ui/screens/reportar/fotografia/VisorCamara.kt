package com.example.reportaciudad.ui.screens.reportar.fotografia

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@Composable
fun rememberControladorCamara(): LifecycleCameraController {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controlador = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            imageCaptureMode = ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
        }
    }
    DisposableEffect(lifecycleOwner) {
        controlador.bindToLifecycle(lifecycleOwner)
        onDispose { controlador.unbind() }
    }
    return controlador
}

@Composable
fun VisorCamara(controlador: LifecycleCameraController, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PreviewView(context).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                // COMPATIBLE usa TextureView: respeta las esquinas redondeadas (clip) de Compose
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                controller = controlador
            }
        }
    )
}


suspend fun LifecycleCameraController.tomarFoto(archivo: File, ejecutor: Executor): File =
    suspendCancellableCoroutine { continuacion ->
        val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()
        takePicture(
            opciones,
            ejecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(resultado: ImageCapture.OutputFileResults) {
                    continuacion.resume(archivo)
                }

                override fun onError(error: ImageCaptureException) {
                    continuacion.resumeWithException(error)
                }
            }
        )
    }
