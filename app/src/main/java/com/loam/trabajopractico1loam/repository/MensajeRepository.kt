package com.loam.trabajopractico1loam.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.loam.trabajopractico1loam.model.Mensaje

class MensajeRepository {
    private val db = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "MensajeRepository"
    }

    fun enviar(mensaje: Mensaje) {
        try {
            db.collection("mensajes")
                .add(mensaje)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Mensaje enviado con ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error al enviar mensaje", e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado al enviar mensaje", e)
        }
    }
    
    fun recibir(onChange: (List<Mensaje>) -> Unit) {
        try {
            db.collection("mensajes")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, e ->
                    Log.d(TAG, "Snapshot recibido: " + e.toString())
                    if (e != null) {
                        Log.w(TAG, "Error al escuchar mensajes", e)
                        return@addSnapshotListener
                    }

                    val lista = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(Mensaje::class.java)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error al convertir documento: ${doc.id}", e)
                            null
                        }
                    } ?: emptyList()

                    onChange(lista)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado al configurar listener", e)
            onChange(emptyList())
        }
    }
}