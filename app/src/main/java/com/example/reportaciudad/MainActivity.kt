package com.example.reportaciudad

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.reportaciudad.ui.screens.bienvenida.BienvenidaScreen
import com.example.reportaciudad.ui.screens.inicio.InicioScreen
import com.example.reportaciudad.ui.theme.ReportaCiudadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            ReportaCiudadTheme {
                // Navegación simple entre pantallas; rememberSaveable la conserva al rotar el teléfono
                var pantalla by rememberSaveable { mutableStateOf(Pantalla.BIENVENIDA) }

                when (pantalla) {
                    Pantalla.BIENVENIDA -> BienvenidaScreen(
                        onReportar = { pantalla = Pantalla.INICIO }
                    )
                    Pantalla.INICIO -> InicioScreen()
                }
            }
        }
    }
}

private enum class Pantalla { BIENVENIDA, INICIO }