package com.example.reportaciudad.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.reportaciudad.ui.screens.bienvenida.BienvenidaScreen
import com.example.reportaciudad.ui.screens.detalle.DetalleScreen
import com.example.reportaciudad.ui.screens.inicio.InicioScreen
import com.example.reportaciudad.ui.screens.reportar.emergencia.SeleccionEmergenciaScreen
import com.example.reportaciudad.ui.screens.reportar.fotografia.FotografiaScreen
import com.example.reportaciudad.ui.screens.reportar.localizacion.LocalizacionScreen
import com.example.reportaciudad.ui.screens.reportar.problema.SeleccionProblemaScreen
import com.example.reportaciudad.ui.screens.reportar.resumen.ResumenScreen
import com.example.reportaciudad.ui.screens.reportar.tipo.TipoReporteScreen
import com.example.reportaciudad.viewmodel.AppViewModelProvider
import com.example.reportaciudad.viewmodel.DetalleViewModel
import com.example.reportaciudad.viewmodel.ReporteFormularioViewModel
import com.example.reportaciudad.viewmodel.ReportesViewModel

// Único lugar que conoce la navegación: las pantallas solo avisan con callbacks
@Composable
fun ReportaCiudadNavHost() {
    val navController = rememberNavController()

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
            val vm: ReportesViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            InicioScreen(
                reportes = estado.reportes,
                cargando = estado.cargando,
                onAbrirReporte = { id -> navController.navigate(Ruta.Detalle(id)) },
                onReportar = { navController.navigate(Ruta.FlujoReportar) }
            )
        }
        composable<Ruta.Detalle> {
            // DetalleViewModel lee el id de la ruta desde su SavedStateHandle
            val vm: DetalleViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            DetalleScreen(estado = estado, onBack = { navController.popBackStack() })
        }

        navigation<Ruta.FlujoReportar>(startDestination = Ruta.PasoTipo) {
            composable<Ruta.PasoTipo> {
                TipoReporteScreen(
                    onVolver = { navController.popBackStack() },
                    onEmergencia = { navController.navigate(Ruta.PasoEmergencia) },
                    onRutina = { navController.navigate(Ruta.PasoProblema) }
                )
            }
            composable<Ruta.PasoEmergencia> { entrada ->
                val vm = entrada.formularioViewModel(navController)
                SeleccionEmergenciaScreen(
                    onVolver = { navController.popBackStack() },
                    onSeleccionar = { emergencia ->
                        vm.elegirEmergencia(emergencia)
                        navController.navigate(Ruta.PasoFotografia)
                    }
                )
            }
            composable<Ruta.PasoProblema> { entrada ->
                val vm = entrada.formularioViewModel(navController)
                SeleccionProblemaScreen(
                    onVolver = { navController.popBackStack() },
                    onSeleccionar = { problema ->
                        vm.elegirProblema(problema)
                        navController.navigate(Ruta.PasoFotografia)
                    }
                )
            }
            composable<Ruta.PasoFotografia> { entrada ->
                val vm = entrada.formularioViewModel(navController)
                val estado by vm.estado.collectAsStateWithLifecycle()
                FotografiaScreen(
                    fotoRuta = estado.borrador.fotoRuta,
                    crearArchivoFoto = vm::nuevoArchivoFoto,
                    onFotoSacada = vm::fotoSacada,
                    onRepetir = vm::descartarFoto,
                    onUsarFoto = { navController.navigate(Ruta.PasoLocalizacion) },
                    onVolver = {
                        vm.descartarFoto()
                        navController.popBackStack()
                    }
                )
            }
            composable<Ruta.PasoLocalizacion> { entrada ->
                val vm = entrada.formularioViewModel(navController)
                val estado by vm.estado.collectAsStateWithLifecycle()
                LocalizacionScreen(
                    ubicacion = estado.ubicacion,
                    buscando = estado.buscandoUbicacion,
                    sinUbicacion = estado.sinUbicacion,
                    onBuscarUbicacion = vm::buscarUbicacion,
                    onConfirmar = {
                        vm.confirmarUbicacion()
                        navController.navigate(Ruta.PasoResumen)
                    },
                    onVolver = { navController.popBackStack() }
                )
            }
            composable<Ruta.PasoResumen> { entrada ->
                val vm = entrada.formularioViewModel(navController)
                val estado by vm.estado.collectAsStateWithLifecycle()
                ResumenScreen(
                    borrador = estado.borrador,
                    envio = estado.envio,
                    onVolver = { navController.popBackStack() },
                    onEnviar = vm::enviar,
                    // Al salir del subgrafo se descarta el ViewModel del formulario
                    onVerMisReportes = { navController.popBackStack(Ruta.Inicio, inclusive = false) }
                )
            }
        }
    }
}

// Las pantallas del flujo "Reportar" comparten el ViewModel que pertenece al subgrafo:
// vive mientras el usuario está en el flujo y se descarta al salir.
@Composable
private fun NavBackStackEntry.formularioViewModel(navController: NavController): ReporteFormularioViewModel {
    val entradaFlujo = remember(this) { navController.getBackStackEntry(Ruta.FlujoReportar) }
    return viewModel(viewModelStoreOwner = entradaFlujo, factory = AppViewModelProvider.Factory)
}
