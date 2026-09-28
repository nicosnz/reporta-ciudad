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


@Composable
fun ReportaCiudadScreen(){

    val navController = rememberNavController();
    NavHost(
        navController = navController,
        startDestination = "bienvenida"
    ){
        composable("bienvenida"){
            BienvenidaScreen(navController)
        }
        composable("inicio"){
            InicioScreen(
                navController,

            )
        }
        composable(
            route = "detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ){ entrada ->
            DetalleScreen(navController, reporteId = entrada.arguments?.getInt("id") ?: 0)
        }
    }
}