package com.example.reportaciudad.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reportaciudad.ui.screens.bienvenida.BienvenidaScreen
import com.example.reportaciudad.ui.screens.detalle.DetalleScreen
import com.example.reportaciudad.ui.screens.inicio.InicioScreen
import com.example.reportaciudad.ui.screens.reportar.TipoReporteScreen


@Composable
fun ReportaCiudadScreen(){

    val navController = rememberNavController();
    NavHost(
        navController = navController,
        startDestination = "bienvenida"
    ){
        composable("bienvenida"){
            BienvenidaScreen(onReportar = { navController.navigate("reportar") })
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
            DetalleScreen(navController, reporteId = entrada.arguments?.getInt("id") ?: 0)
        }
        composable("reportar"){
            TipoReporteScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}