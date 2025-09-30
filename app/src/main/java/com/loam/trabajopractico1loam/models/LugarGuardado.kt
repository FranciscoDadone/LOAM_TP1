package com.loam.trabajopractico1loam.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.Timestamp

data class LugarGuardado(
    @DocumentId
    val id: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val direccion: String = "",
    val referencia: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null
) {
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "lat" to lat,
            "lng" to lng,
            "direccion" to direccion,
            "referencia" to referencia,
            "timestamp" to (timestamp ?: Timestamp.now())
        )
    }
}