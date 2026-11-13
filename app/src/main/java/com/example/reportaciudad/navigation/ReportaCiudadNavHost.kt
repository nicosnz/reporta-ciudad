package com.example.reportaciudad.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reportaciudad.data.local.AlmacenFotos
import com.example.reportaciudad.data.model.ReporteFormulario
import com.example.reportaciudad.data.reportes.enviarReporte
import com.example.reportaciudad.ui.screens.bienvenida.BienvenidaScreen
import com.example.reportaciudad.ui.screens.detalle.DetalleScreen
import com.example.reportaciudad.ui.screens.inicio.InicioScreen
import com.example.reportaciudad.ui.screens.reportar.emergencia.SeleccionEmergenciaScreen
import com.example.reportaciudad.ui.screens.reportar.fotografia.FotografiaScreen
import com.example.reportaciudad.ui.screens.reportar.localizacion.LocalizacionScreen
import com.example.reportaciudad.ui.screens.reportar.problema.SeleccionProblemaScreen
import com.example.reportaciudad.ui.screens.reportar.resumen.ResumenScreen
import com.example.reportaciudad.ui.screens.reportar.tipo.TipoReporteScreen

// Único lugar que conoce la navegación: las pantallas solo avisan con callbacks
@Composable
fun ReportaCiudadNavHost() {
    val navController = rememberNavController()
    var borrador by remember { mutableStateOf(ReporteFormulario()) }
    val almacenFotos = AlmacenFotos(LocalContext.current)

    NavHost(navController = navController, startDestination = Ruta.Bienvenida) {
        composable<Ruta.Bienvenida> {
            BienvenidaScreen(
                onInicio = {
                    // La bienvenida no queda en la pila: "Atrás" desde Mis reportes sale de la app
                    navController.navigate(Ruta.Inicio) {
                        popUpTo<Ruta.Bienvenida> { inclusive = true }
                    }
                }
            )
        }
        composable<Ruta.Inicio> {
            InicioScreen(
                onAbrirReporte = { id -> navController.navigate(Ruta.Detalle(id)) },
                onReportar = { navController.navigate(Ruta.FlujoReportar) }
            )
        }
        composable<Ruta.Detalle> { entrada ->
            DetalleScreen(
                onBack = { navController.popBackStack() },
                reporteId = entrada.toRoute<Ruta.Detalle>().id
            )
        }

        navigation<Ruta.FlujoReportar>(startDestination = Ruta.PasoTipo) {
            composable<Ruta.PasoTipo> {
                TipoReporteScreen(
                    onVolver = { navController.popBackStack() },
                    onEmergencia = {
                        almacenFotos.borrar(borrador.fotoRuta)
                        borrador = ReporteFormulario()
                        navController.navigate(Ruta.PasoEmergencia)
                    },
                    onRutina = {
                        almacenFotos.borrar(borrador.fotoRuta)
                        borrador = ReporteFormulario()
                        navController.navigate(Ruta.PasoProblema)
                    }
                )
            }
            composable<Ruta.PasoEmergencia> {
                SeleccionEmergenciaScreen(
                    onVolver = { navController.popBackStack() },
                    onSeleccionar = { emergencia ->
                        borrador = borrador.copy(emergencia = emergencia)
                        navController.navigate(Ruta.PasoFotografia)
                    }
                )
            }
            composable<Ruta.PasoProblema> {
                SeleccionProblemaScreen(
                    onVolver = { navController.popBackStack() },
                    onSeleccionar = { problema ->
                        borrador = borrador.copy(problema = problema)
                        navController.navigate(Ruta.PasoFotografia)
                    }
                )
            }
            composable<Ruta.PasoFotografia> {
                FotografiaScreen(
                    onVolver = { navController.popBackStack() },
                    onFotoTomada = { ruta ->
                        borrador = borrador.copy(fotoRuta = ruta)
                        navController.navigate(Ruta.PasoLocalizacion)
                    }
                )
            }
            composable<Ruta.PasoLocalizacion> {
                LocalizacionScreen(
                    onVolver = { navController.popBackStack() },
                    onConfirmar = { latitud, longitud ->
                        borrador = borrador.copy(latitud = latitud, longitud = longitud)
                        navController.navigate(Ruta.PasoResumen)
                    }
                )
            }
            composable<Ruta.PasoResumen> {
                ResumenScreen(
                    borrador = borrador,
                    onVolver = { navController.popBackStack() },
                    onEnviar = { enviarReporte(it) },
                    onVerMisReportes = {
                        navController.popBackStack(Ruta.Inicio, inclusive = false)
                        borrador = ReporteFormulario()
                    }
                )
            }
        }
    }
}
