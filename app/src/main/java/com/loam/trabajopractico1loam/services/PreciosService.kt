package com.loam.trabajopractico1loam.services

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.loam.trabajopractico1loam.models.PrecioReferencia
import com.loam.trabajopractico1loam.repository.PreciosRepository
import kotlinx.coroutines.flow.Flow

class PreciosService {
    private val repository = PreciosRepository(Firebase.firestore)

    fun getPreciosFlow(): Flow<List<PrecioReferencia>> {
        return repository.getPreciosFlow()
    }

    suspend fun actualizarPrecio(precio: PrecioReferencia): Result<Unit> {
        return repository.actualizarPrecio(precio)
    }

    suspend fun inicializarPreciosPorDefecto(): Result<Unit> {
        return repository.inicializarPreciosPorDefecto()
    }
}