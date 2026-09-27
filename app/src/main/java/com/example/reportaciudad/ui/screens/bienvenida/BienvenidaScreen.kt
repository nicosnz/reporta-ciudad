package com.example.reportaciudad.ui.screens.bienvenida

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BienvenidaScreen(onEmpezar: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ReportaCiudad",
            style = MaterialTheme.typography.headlineLarge,

        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Santa Cruz, mirada por todos",
            style = MaterialTheme.typography.bodyLarge,
            
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = onEmpezar) {
            Text(text = "Empezar")
        }
    }
}