package com.loam.trabajopractico1loam.model

import com.google.firebase.Timestamp

data class Mensaje(
    var mensaje: String = "",
    var usuario: String = "",
    var timestamp: Timestamp = Timestamp.now()
)
