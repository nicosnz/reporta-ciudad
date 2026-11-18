package com.example.reportaciudad.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.reportaciudad.R
import com.example.reportaciudad.data.model.Sesion
import com.example.reportaciudad.ui.screens.componentes.IconoCheck
import com.example.reportaciudad.ui.screens.componentes.iconoDeLinea
import com.example.reportaciudad.ui.theme.Emergencia
import com.example.reportaciudad.ui.theme.EmergenciaClaro
import com.example.reportaciudad.ui.theme.Neutro700
import com.example.reportaciudad.ui.theme.Superficie
import com.example.reportaciudad.ui.theme.Urbano
import com.example.reportaciudad.ui.theme.UrbanoClaro
import com.example.reportaciudad.ui.theme.UrbanoDeshabilitado
import com.example.reportaciudad.ui.theme.UrbanoEtiqueta
import com.example.reportaciudad.ui.theme.UrbanoEtiquetaTexto
import com.example.reportaciudad.viewmodel.ErrorLogin
import com.example.reportaciudad.viewmodel.LoginUiState
import kotlinx.coroutines.delay

private const val ESPERA_EXITO_MS = 900L

@Composable
fun LoginScreen(
    estado: LoginUiState,
    onUsuarioCambiado: (String) -> Unit = {},
    onContrasenaCambiada: (String) -> Unit = {},
    onAlternarContrasena: () -> Unit = {},
    onIngresar: () -> Unit = {},
    onIngresado: (Sesion) -> Unit = {}
) {
    LaunchedEffect(estado.sesion) {
        val sesion = estado.sesion ?: return@LaunchedEffect
        delay(ESPERA_EXITO_MS)
        onIngresado(sesion)
    }

    val exito = estado.sesion != null
    val bordeCampo = when {
        estado.error == ErrorLogin.CREDENCIALES -> Emergencia
        exito -> Urbano
        else -> Color.Transparent
    }
    val camposHabilitados = !estado.ingresando && !exito

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(112.dp)
                    .background(UrbanoClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_santa_cruz),
                    contentDescription = "Escudo de Santa Cruz de la Sierra",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(width = 62.dp, height = 80.dp)
                )
            }

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    letterSpacing = (-0.025).em
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 4.dp, top = 32.dp, end = 4.dp)
            )
            Text(
                text = "Ingresa con tu usuario y contraseña.",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
                color = Neutro700,
                modifier = Modifier.padding(start = 4.dp, top = 6.dp, end = 4.dp)
            )

            Column(
                modifier = Modifier.padding(top = 32.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                CampoLogin(
                    etiqueta = "Usuario",
                    ejemplo = "Ej.: vecino1",
                    valor = estado.usuario,
                    onValorCambiado = onUsuarioCambiado,
                    colorBorde = bordeCampo,
                    habilitado = camposHabilitados,
                    teclado = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
                )
                CampoLogin(
                    etiqueta = "Contraseña",
                    ejemplo = "Tu contraseña",
                    valor = estado.contrasena,
                    onValorCambiado = onContrasenaCambiada,
                    colorBorde = bordeCampo,
                    habilitado = camposHabilitados,
                    teclado = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    acciones = KeyboardActions(onDone = { onIngresar() }),
                    transformacion = if (estado.mostrarContrasena) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    iconoFinal = {
                        IconButton(onClick = onAlternarContrasena) {
                            Icon(
                                imageVector = if (estado.mostrarContrasena) IconoOjoTachado else IconoOjo,
                                contentDescription = if (estado.mostrarContrasena) "Ocultar contraseña"
                                else "Mostrar contraseña",
                                tint = Neutro700,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                )

                estado.error?.let { AvisoError(it) }
                estado.sesion?.let { AvisoExito(it) }
            }
        }

        Spacer(Modifier.size(16.dp))

        Button(
            onClick = onIngresar,
            enabled = estado.puedeIngresar,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Urbano,
                contentColor = Color.White,
                disabledContainerColor = UrbanoDeshabilitado,
                disabledContentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) {
            Text(
                text = when {
                    exito -> "Entrando…"
                    estado.ingresando -> "Ingresando…"
                    else -> "Ingresar"
                },
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp)
            )
        }
    }
}

@Composable
private fun CampoLogin(
    etiqueta: String,
    ejemplo: String,
    valor: String,
    onValorCambiado: (String) -> Unit,
    colorBorde: Color,
    habilitado: Boolean,
    teclado: KeyboardOptions,
    acciones: KeyboardActions = KeyboardActions.Default,
    transformacion: VisualTransformation = VisualTransformation.None,
    iconoFinal: (@Composable () -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambiado,
            enabled = habilitado,
            singleLine = true,
            keyboardOptions = teclado,
            keyboardActions = acciones,
            visualTransformation = transformacion,
            trailingIcon = iconoFinal,
            placeholder = {
                Text(
                    text = ejemplo,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = Neutro700
                )
            },
            textStyle = TextStyle(fontFamily = MaterialTheme.typography.bodyLarge.fontFamily, fontSize = 16.sp),
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Superficie,
                unfocusedContainerColor = Superficie,
                disabledContainerColor = Superficie,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                focusedBorderColor = if (colorBorde == Color.Transparent) Urbano else colorBorde,
                unfocusedBorderColor = colorBorde,
                disabledBorderColor = colorBorde,
                cursorColor = Urbano
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .semantics { contentDescription = etiqueta }
        )
    }
}

@Composable
private fun AvisoError(error: ErrorLogin) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(EmergenciaClaro, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .semantics { liveRegion = LiveRegionMode.Assertive },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = IconoAlerta,
            contentDescription = null,
            tint = Emergencia,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = when (error) {
                ErrorLogin.CREDENCIALES -> "Usuario o contraseña incorrectos."
                ErrorLogin.SIN_CONEXION -> "No hay conexión. Revisa tu internet e intenta de nuevo."
                ErrorLogin.SERVIDOR -> "No pudimos iniciar sesión. Intenta de nuevo en un momento."
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Emergencia
        )
    }
}

@Composable
private fun AvisoExito(sesion: Sesion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(UrbanoEtiqueta, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Urbano, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = IconoCheck,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = "¡Inicio de sesión exitoso!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = UrbanoEtiquetaTexto
            )
            Text(
                text = "Bienvenido, ${sesion.nombre}.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
                color = UrbanoEtiquetaTexto
            )
        }
    }
}

private val IconoOjo = iconoDeLinea(
    "Ojo", 2f,
    "M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z",
    "M9 12a3 3 0 1 0 6 0a3 3 0 1 0-6 0"
)

private val IconoOjoTachado = iconoDeLinea(
    "OjoTachado", 2f,
    "M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z",
    "M9 12a3 3 0 1 0 6 0a3 3 0 1 0-6 0",
    "M3 3l18 18"
)

private val IconoAlerta = iconoDeLinea(
    "Alerta", 2.25f,
    "M12 3a9 9 0 1 0 0 18a9 9 0 1 0 0-18z",
    "M12 8v5",
    "M12 16.5v.01"
)
