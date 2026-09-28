package com.example.reportaciudad.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reportaciudad.ui.screens.bienvenida.BienvenidaScreen
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
            InicioScreen(navController)
        }
    }
}