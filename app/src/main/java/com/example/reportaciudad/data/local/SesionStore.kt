package com.example.reportaciudad.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.reportaciudad.data.model.Rol
import com.example.reportaciudad.data.model.Sesion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStoreSesion: DataStore<Preferences> by preferencesDataStore(name = "sesion")

class SesionStore(context: Context) {

    private val dataStore = context.applicationContext.dataStoreSesion

    val sesion: Flow<Sesion?> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            val usuarioId = prefs[USUARIO_ID] ?: return@map null
            val usuario = prefs[USUARIO] ?: return@map null
            val rol = prefs[ROL]?.let { nombre -> Rol.entries.find { it.name == nombre } } ?: return@map null
            Sesion(usuarioId = usuarioId, usuario = usuario, nombre = prefs[NOMBRE] ?: usuario, rol = rol)
        }

    suspend fun guardar(sesion: Sesion) {
        dataStore.edit { prefs ->
            prefs[USUARIO_ID] = sesion.usuarioId
            prefs[USUARIO] = sesion.usuario
            prefs[NOMBRE] = sesion.nombre
            prefs[ROL] = sesion.rol.name
        }
    }

    suspend fun borrar() {
        dataStore.edit { it.clear() }
    }

    private companion object {
        val USUARIO_ID = stringPreferencesKey("usuario_id")
        val USUARIO = stringPreferencesKey("usuario")
        val NOMBRE = stringPreferencesKey("nombre")
        val ROL = stringPreferencesKey("rol")
    }
}
