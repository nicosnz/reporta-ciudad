package com.example.reportaciudad.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reportaciudad.data.model.Rol
import com.example.reportaciudad.data.model.Sesion
import com.example.reportaciudad.ui.screens.admin.AdminDetalleScreen
import com.example.reportaciudad.ui.screens.admin.AdminFotoArregloScreen
import com.example.reportaciudad.ui.screens.admin.AdminScreen
import com.example.reportaciudad.ui.screens.bienvenida.BienvenidaScreen
import com.example.reportaciudad.ui.screens.detalle.DetalleScreen
import com.example.reportaciudad.ui.screens.inicio.InicioScreen
import com.example.reportaciudad.ui.screens.login.LoginScreen
import com.example.reportaciudad.ui.screens.reportar.emergencia.SeleccionEmergenciaScreen
import com.example.reportaciudad.ui.screens.reportar.fotografia.FotografiaScreen
import com.example.reportaciudad.ui.screens.reportar.localizacion.LocalizacionScreen
import com.example.reportaciudad.ui.screens.reportar.problema.SeleccionProblemaScreen
import com.example.reportaciudad.ui.screens.reportar.resumen.ResumenScreen
import com.example.reportaciudad.ui.screens.reportar.tipo.TipoReporteScreen
import com.example.reportaciudad.viewmodel.AdminDetalleViewModel
import com.example.reportaciudad.viewmodel.AdminFotoArregloViewModel
import com.example.reportaciudad.viewmodel.AdminPanelViewModel
import com.example.reportaciudad.viewmodel.AppViewModelProvider
import com.example.reportaciudad.viewmodel.DetalleViewModel
import com.example.reportaciudad.viewmodel.LoginViewModel
import com.example.reportaciudad.viewmodel.ReporteFormularioViewModel
import com.example.reportaciudad.viewmodel.ReportesViewModel
import com.example.reportaciudad.viewmodel.SesionViewModel
import kotlinx.coroutines.launch

@Composable
fun ReportaCiudadNavHost() {
    val navController = rememberNavController()
    val alcance = rememberCoroutineScope()
    val sesionVm: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val cerrarSesion: () -> Unit = {
        sesionVm.cerrarSesion()
        navController.navigate(Ruta.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Ruta.BIENVENIDA) {
        composable(Ruta.BIENVENIDA) {
            val haySesion by sesionVm.haySesion.collectAsStateWithLifecycle()
            BienvenidaScreen(
                haySesion = haySesion,
                onInicio = {
                    alcance.launch {
                        navController.navigate(destinoSegunSesion(sesionVm.sesionGuardada())) {
                            popUpTo(Ruta.BIENVENIDA) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Ruta.LOGIN) {
            val vm: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            LoginScreen(
                estado = estado,
                onUsuarioCambiado = vm::cambiarUsuario,
                onContrasenaCambiada = vm::cambiarContrasena,
                onAlternarContrasena = vm::alternarMostrarContrasena,
                onIngresar = vm::ingresar,
                onIngresado = { sesion ->
                    navController.navigate(destinoSegunSesion(sesion)) {
                        popUpTo(Ruta.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Ruta.ADMIN) {
            val vm: AdminPanelViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) { vm.cargar() }
            AdminScreen(
                estado = estado,
                onElegirBandeja = vm::elegirBandeja,
                onAbrirReporte = { remoteId -> navController.navigate(Ruta.adminDetalle(remoteId)) },
                onReintentar = vm::cargar,
                onCerrarSesion = cerrarSesion
            )
        }
        composable(
            Ruta.ADMIN_DETALLE,
            arguments = listOf(navArgument("remoteId") { type = NavType.StringType })
        ) { entrada ->
            val vm: AdminDetalleViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) { vm.cargar() }
            AdminDetalleScreen(
                estado = estado,
                onVolver = { navController.popBackStack() },
                onReintentar = vm::cargar,
                onAvanzar = vm::avanzar,
                onMarcarResuelto = {
                    navController.navigate(Ruta.adminFotoArreglo(entrada.arguments?.getString("remoteId").orEmpty()))
                },
                onAbrirRechazo = vm::abrirHojaRechazo,
                onCerrarRechazo = vm::cerrarHojaRechazo,
                onElegirMotivo = vm::elegirMotivo,
                onConfirmarRechazo = vm::confirmarRechazo
            )
        }
        composable(
            Ruta.ADMIN_FOTO_ARREGLO,
            arguments = listOf(navArgument("remoteId") { type = NavType.StringType })
        ) {
            val vm: AdminFotoArregloViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            AdminFotoArregloScreen(
                estado = estado,
                crearArchivoFoto = vm::nuevoArchivoFoto,
                onFotoSacada = vm::fotoSacada,
                onFotoElegida = vm::fotoElegida,
                onRepetir = vm::repetir,
                onConfirmar = vm::confirmar,
                onVolver = { navController.popBackStack() },
                onResuelto = { navController.popBackStack() }
            )
        }
        composable(Ruta.INICIO) {
            val vm: ReportesViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) { vm.actualizarEstados() }
            InicioScreen(
                reportes = estado.reportes,
                cargando = estado.cargando,
                onAbrirReporte = { id -> navController.navigate(Ruta.detalle(id)) },
                onReportar = { navController.navigate(Ruta.FLUJO_REPORTAR) },
                onCerrarSesion = cerrarSesion
            )
        }
        composable(
            Ruta.DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) {
            val vm: DetalleViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            val cancelacion by vm.cancelacion.collectAsStateWithLifecycle()
            DetalleScreen(
                estado = estado,
                cancelacion = cancelacion,
                onBack = { navController.popBackStack() },
                onAbrirCancelacion = vm::abrirCancelacion,
                onCerrarCancelacion = vm::cerrarCancelacion,
                onConfirmarCancelacion = vm::confirmarCancelacion,
                onEliminado = { navController.popBackStack() }
            )
        }

        navigation(startDestination = Ruta.PASO_TIPO, route = Ruta.FLUJO_REPORTAR) {
            composable(Ruta.PASO_TIPO) {
                TipoReporteScreen(
                    onVolver = { navController.popBackStack() },
                    onEmergencia = { navController.navigate(Ruta.PASO_EMERGENCIA) },
                    onRutina = { navController.navigate(Ruta.PASO_PROBLEMA) }
                )
            }
            composable(Ruta.PASO_EMERGENCIA) { entrada ->
                val vm = entrada.formularioViewModel(navController)
                SeleccionEmergenciaScreen(
                    onVolver = { navController.popBackStack() },
                    onSeleccionar = { emergencia ->
                        vm.elegirEmergencia(emergencia)
                        navController.navigate(Ruta.PASO_FOTOGRAFIA)
                    }
                )
            }
            composable(Ruta.PASO_PROBLEMA) { entrada ->
                val vm = entrada.formularioViewModel(navController)
                SeleccionProblemaScreen(
                    onVolver = { navController.popBackStack() },
                    onSeleccionar = { problema ->
                        vm.elegirProblema(problema)
                        navController.navigate(Ruta.PASO_FOTOGRAFIA)
                    }
                )
            }
            composable(Ruta.PASO_FOTOGRAFIA) { entrada ->
                val vm = entrada.formularioViewModel(navController)
                val estado by vm.estado.collectAsStateWithLifecycle()
                FotografiaScreen(
                    fotoRuta = estado.borrador.fotoRuta,
                    crearArchivoFoto = vm::nuevoArchivoFoto,
                    onFotoSacada = vm::fotoSacada,
                    onRepetir = vm::descartarFoto,
                    onUsarFoto = { navController.navigate(Ruta.PASO_LOCALIZACION) },
                    onVolver = {
                        vm.descartarFoto()
                        navController.popBackStack()
                    }
                )
            }
            composable(Ruta.PASO_LOCALIZACION) { entrada ->
                val vm = entrada.formularioViewModel(navController)
                val estado by vm.estado.collectAsStateWithLifecycle()
                LocalizacionScreen(
                    ubicacion = estado.ubicacion,
                    buscando = estado.buscandoUbicacion,
                    sinUbicacion = estado.sinUbicacion,
                    onBuscarUbicacion = vm::buscarUbicacion,
                    onConfirmar = {
                        vm.confirmarUbicacion()
                        navController.navigate(Ruta.PASO_RESUMEN)
                    },
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Ruta.PASO_RESUMEN) { entrada ->
                val vm = entrada.formularioViewModel(navController)
                val estado by vm.estado.collectAsStateWithLifecycle()
                ResumenScreen(
                    borrador = estado.borrador,
                    envio = estado.envio,
                    onVolver = { navController.popBackStack() },
                    onEnviar = vm::enviar,
                    onVerMisReportes = { navController.popBackStack(Ruta.INICIO, inclusive = false) }
                )
            }
        }
    }
}

private fun destinoSegunSesion(sesion: Sesion?): String = when (sesion?.rol) {
    null -> Ruta.LOGIN
    Rol.VECINO -> Ruta.INICIO
    Rol.ADMIN -> Ruta.ADMIN
}

@Composable
private fun NavBackStackEntry.formularioViewModel(navController: NavController): ReporteFormularioViewModel {
    val entradaFlujo = remember(this) { navController.getBackStackEntry(Ruta.FLUJO_REPORTAR) }
    return viewModel(viewModelStoreOwner = entradaFlujo, factory = AppViewModelProvider.Factory)
}
