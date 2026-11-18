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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
        navController.navigate(Ruta.Login) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Ruta.Bienvenida) {
        composable<Ruta.Bienvenida> {
            val haySesion by sesionVm.haySesion.collectAsStateWithLifecycle()
            BienvenidaScreen(
                haySesion = haySesion,
                onInicio = {
                    alcance.launch {
                        navController.navigate(destinoSegunSesion(sesionVm.sesionGuardada())) {
                            popUpTo<Ruta.Bienvenida> { inclusive = true }
                        }
                    }
                }
            )
        }
        composable<Ruta.Login> {
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
                        popUpTo<Ruta.Login> { inclusive = true }
                    }
                }
            )
        }
        composable<Ruta.Admin> {
            val vm: AdminPanelViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) { vm.cargar() }
            AdminScreen(
                estado = estado,
                onElegirBandeja = vm::elegirBandeja,
                onAbrirReporte = { remoteId -> navController.navigate(Ruta.AdminDetalle(remoteId)) },
                onReintentar = vm::cargar,
                onCerrarSesion = cerrarSesion
            )
        }
        composable<Ruta.AdminDetalle> { entrada ->
            val vm: AdminDetalleViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) { vm.cargar() }
            AdminDetalleScreen(
                estado = estado,
                onVolver = { navController.popBackStack() },
                onReintentar = vm::cargar,
                onAvanzar = vm::avanzar,
                onMarcarResuelto = {
                    navController.navigate(Ruta.AdminFotoArreglo(entrada.toRoute<Ruta.AdminDetalle>().remoteId))
                },
                onAbrirRechazo = vm::abrirHojaRechazo,
                onCerrarRechazo = vm::cerrarHojaRechazo,
                onElegirMotivo = vm::elegirMotivo,
                onConfirmarRechazo = vm::confirmarRechazo
            )
        }
        composable<Ruta.AdminFotoArreglo> {
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
        composable<Ruta.Inicio> {
            val vm: ReportesViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val estado by vm.estado.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) { vm.actualizarEstados() }
            InicioScreen(
                reportes = estado.reportes,
                cargando = estado.cargando,
                onAbrirReporte = { id -> navController.navigate(Ruta.Detalle(id)) },
                onReportar = { navController.navigate(Ruta.FlujoReportar) },
                onCerrarSesion = cerrarSesion
            )
        }
        composable<Ruta.Detalle> {
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
                    onVerMisReportes = { navController.popBackStack(Ruta.Inicio, inclusive = false) }
                )
            }
        }
    }
}

private fun destinoSegunSesion(sesion: Sesion?): Any = when (sesion?.rol) {
    null -> Ruta.Login
    Rol.VECINO -> Ruta.Inicio
    Rol.ADMIN -> Ruta.Admin
}

@Composable
private fun NavBackStackEntry.formularioViewModel(navController: NavController): ReporteFormularioViewModel {
    val entradaFlujo = remember(this) { navController.getBackStackEntry(Ruta.FlujoReportar) }
    return viewModel(viewModelStoreOwner = entradaFlujo, factory = AppViewModelProvider.Factory)
}
