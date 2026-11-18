package com.example.reportaciudad.ui.screens.bienvenida

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.example.reportaciudad.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.ui.screens.componentes.iconoDeLinea
import com.example.reportaciudad.ui.theme.UrbanoClaro

@Composable
fun BienvenidaScreen(
    // null mientras se lee la sesión guardada: el botón no se muestra para no cambiar de texto
    haySesion: Boolean? = false,
    onInicio: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 120.dp)
                .requiredSize(520.dp)
                .clip(CircleShape)
                .background(UrbanoClaro)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ReportaCiudad",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 40.sp,
                    lineHeight = 40.sp,
                    letterSpacing = (-0.025).em
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            Image(
                painter = painterResource(R.drawable.logo_santa_cruz),
                contentDescription = "Escudo Santa Cruz de la Sierra",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(200.dp)
            )

            Spacer(Modifier.height(64.dp))

            Text(
                text = "Santa Cruz, mirada por todos",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 28.sp,
                    lineHeight = 35.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.02).em
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onInicio,
                enabled = haySesion != null,
                modifier = Modifier
                    .defaultMinSize(minHeight = 56.dp)
                    .alpha(if (haySesion == null) 0f else 1f),
                contentPadding = PaddingValues(horizontal = 32.dp)
            ) {
                Text(
                    text = if (haySesion == true) "Ir al Inicio" else "Iniciar sesión",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp)
                )
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = FlechaDerecha,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

private val FlechaDerecha = iconoDeLinea("FlechaDerecha", 2.75f, "M5 12h14", "M12 5l7 7-7 7")
