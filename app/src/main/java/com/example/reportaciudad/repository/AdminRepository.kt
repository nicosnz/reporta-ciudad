package com.example.reportaciudad.repository

import com.example.reportaciudad.data.model.ReporteServidor

// Acceso a los datos del administrador. A diferencia del vecino, no usa Room:
// los reportes son de otros teléfonos y siempre se leen del servidor.
interface AdminRepository {

    // Todos los reportes, del más nuevo al más viejo. Falla si no hay conexión.
    suspend fun reportes(): Result<List<ReporteServidor>>
}
