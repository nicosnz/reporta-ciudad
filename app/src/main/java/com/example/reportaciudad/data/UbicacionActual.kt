package com.example.reportaciudad.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume


object UbicacionActual {

    private const val ESPERA_MAXIMA_MS = 10_000L

    fun tienePermiso(context: Context) =
        concedido(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
            concedido(context, Manifest.permission.ACCESS_COARSE_LOCATION)

    // GPS si el permiso es preciso (funciona sin internet); si no responde, la red. Null si no hay ubicación.
    @SuppressLint("MissingPermission")
    suspend fun obtener(context: Context): Location? {
        if (!tienePermiso(context)) return null
        val manager = context.getSystemService(LocationManager::class.java) ?: return null

        val proveedores = buildList {
            if (concedido(context, Manifest.permission.ACCESS_FINE_LOCATION)) add(LocationManager.GPS_PROVIDER)
            add(LocationManager.NETWORK_PROVIDER)
        }.filter { manager.isProviderEnabled(it) }

        for (proveedor in proveedores) {
            val ubicacion = withTimeoutOrNull(ESPERA_MAXIMA_MS) { pedir(context, manager, proveedor) }
                ?: manager.getLastKnownLocation(proveedor)
            if (ubicacion != null) return ubicacion
        }
        return null
    }

    @SuppressLint("MissingPermission")
    private suspend fun pedir(context: Context, manager: LocationManager, proveedor: String): Location? =
        suspendCancellableCoroutine { continuacion ->
            val senal = CancellationSignal()
            continuacion.invokeOnCancellation { senal.cancel() }
            LocationManagerCompat.getCurrentLocation(
                manager, proveedor, senal, ContextCompat.getMainExecutor(context)
            ) { continuacion.resume(it) }
        }

    private fun concedido(context: Context, permiso: String) =
        ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED
}
