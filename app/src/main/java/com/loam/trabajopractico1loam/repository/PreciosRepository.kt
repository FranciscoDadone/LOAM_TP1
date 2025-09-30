package com.loam.trabajopractico1loam.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.loam.trabajopractico1loam.models.PrecioReferencia
import com.loam.trabajopractico1loam.models.TipoPrecio
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
/**
 * Repositorio para manejar operaciones de precios de referencia en Firestore
 */
class PreciosRepository(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val COLLECTION_NAME = "precios_referencia"
        private const val TAG = "PreciosRepository"
    }

    /**
     * Obtiene todos los precios de referencia en tiempo real
     */
    fun getPreciosFlow(): Flow<List<PrecioReferencia>> = callbackFlow {
        val listener = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("activo", true)
            .orderBy("tipo")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error al escuchar precios", error)
                    close(error)
                    return@addSnapshotListener
                }

                val precios = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(PrecioReferencia::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al convertir documento: ${doc.id}", e)
                        null
                    }
                } ?: emptyList()

                trySend(precios)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Actualiza un precio específico
     */
    suspend fun actualizarPrecio(precio: PrecioReferencia): Result<Unit> {
        return try {
            val docRef = if (precio.id.isEmpty()) {
                firestore.collection(COLLECTION_NAME).document()
            } else {
                firestore.collection(COLLECTION_NAME).document(precio.id)
            }

            val precioData = precio.copy(
                id = docRef.id,
                fechaActualizacion = com.google.firebase.Timestamp.now()
            )

            docRef.set(precioData).await()
            Log.d(TAG, "Precio actualizado: ${precio.tipo}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar precio", e)
            Result.failure(e)
        }
    }

    /**
     * Inicializa los precios por defecto si no existen
     */
    suspend fun inicializarPreciosPorDefecto(): Result<Unit> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME).get().await()
            
            if (snapshot.isEmpty) {
                Log.d(TAG, "Inicializando precios por defecto")
                
                val preciosPorDefecto = listOf(
                    PrecioReferencia(
                        tipo = TipoPrecio.METRO_CUADRADO,
                        valor = 850.0,
                        moneda = "USD",
                        unidad = TipoPrecio.METRO_CUADRADO.unidad,
                        descripcion = TipoPrecio.METRO_CUADRADO.descripcion
                    ),
                    PrecioReferencia(
                        tipo = TipoPrecio.HONORARIOS_PROFESIONALES,
                        valor = 75.0,
                        moneda = "USD",
                        unidad = TipoPrecio.HONORARIOS_PROFESIONALES.unidad,
                        descripcion = TipoPrecio.HONORARIOS_PROFESIONALES.descripcion
                    ),
                    PrecioReferencia(
                        tipo = TipoPrecio.MATERIALES_BASICOS,
                        valor = 120.0,
                        moneda = "USD",
                        unidad = TipoPrecio.MATERIALES_BASICOS.unidad,
                        descripcion = TipoPrecio.MATERIALES_BASICOS.descripcion
                    ),
                    PrecioReferencia(
                        tipo = TipoPrecio.MANO_OBRA_ESPECIALIZADA,
                        valor = 180.0,
                        moneda = "USD",
                        unidad = TipoPrecio.MANO_OBRA_ESPECIALIZADA.unidad,
                        descripcion = TipoPrecio.MANO_OBRA_ESPECIALIZADA.descripcion
                    )
                )

                preciosPorDefecto.forEach { precio ->
                    actualizarPrecio(precio)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar precios por defecto", e)
            Result.failure(e)
        }
    }
}