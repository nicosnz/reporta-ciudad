package com.example.reportaciudad.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reportaciudad.data.AlmacenFotos
import com.example.reportaciudad.data.reportes.enviarReporte
import com.example.reportaciudad.data.model.ReporteFormulario
import com.example.reportaciudad.ui.screens.bienvenida.BienvenidaScreen
import com.example.reportaciudad.ui.screens.detalle.DetalleScreen
import com.example.reportaciudad.ui.screens.inicio.InicioScreen
import com.example.reportaciudad.ui.screens.reportar.fotografia.FotografiaScreen
import com.example.reportaciudad.ui.screens.reportar.localizacion.LocalizacionScreen
import com.example.reportaciudad.ui.screens.reportar.resumen.ResumenScreen
import com.example.reportaciudad.ui.screens.reportar.emergencia.SeleccionEmergenciaScreen
import com.example.reportaciudad.ui.screens.reportar.problema.SeleccionProblemaScreen
import com.example.reportaciudad.ui.screens.reportar.tipo.TipoReporteScreen


@Composable
fun ReportaCiudadScreen(){

    val navController = rememberNavController();
    var borrador by remember { mutableStateOf(ReporteFormulario()) }
    NavHost(
        navController = navController,
        startDestination = "bienvenida"
    ){
        composable("bienvenida"){
            BienvenidaScreen(onInicio = { navController.navigate("inicio") })
        }
        composable("inicio"){
            InicioScreen(
                navController,
                onReportar = { navController.navigate("reportar") }
            )
        }
        composable(
            route = "detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ){ entrada ->
            DetalleScreen({ navController.popBackStack() }, reporteId = entrada.arguments?.getInt("id") ?: 0)
        }
        composable("reportar"){
            TipoReporteScreen(
                onVolver = { navController.popBackStack() },
                onEmergencia = {
                    AlmacenFotos.borrar(borrador.fotoRuta)
                    borrador = ReporteFormulario()
                    navController.navigate("reportar/emergencia")
                },
                onRutina = {
                    AlmacenFotos.borrar(borrador.fotoRuta)
                    borrador = ReporteFormulario()
                    navController.navigate("reportar/problema")
                }
            )
        }
        composable("reportar/emergencia"){
            SeleccionEmergenciaScreen(
                onVolver = { navController.popBackStack() },
                onSeleccionar = { emergencia ->
                    borrador = borrador.copy(emergencia = emergencia)
                    navController.navigate("reportar/fotografia")
                }
            )
        }
        composable("reportar/problema"){
            SeleccionProblemaScreen(
                onVolver = { navController.popBackStack() },
                onSeleccionar = { problema ->
                    borrador = borrador.copy(problema = problema)
                    navController.navigate("reportar/fotografia")
                }
            )
        }
        composable("reportar/fotografia"){
            FotografiaScreen(
                onVolver = { navController.popBackStack() },
                onFotoTomada = { ruta ->
                    borrador = borrador.copy(fotoRuta = ruta)
                    navController.navigate("reportar/localizacion")
                }
            )
        }
        composable("reportar/localizacion"){
            LocalizacionScreen(
                onVolver = { navController.popBackStack() },
                onConfirmar = { latitud, longitud ->
                    borrador = borrador.copy(latitud = latitud, longitud = longitud)
                    navController.navigate("reportar/resumen")
                }
            )
        }
        composable("reportar/resumen"){
            ResumenScreen(
                borrador = borrador,
                onVolver = { navController.popBackStack() },
                onEnviar = { enviarReporte(it) },
                onVerMisReportes = {

                    navController.popBackStack("inicio", inclusive = false)

                    borrador = ReporteFormulario()
                }
            )
        }
    }
}