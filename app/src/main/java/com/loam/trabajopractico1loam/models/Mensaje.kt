package com.loam.trabajopractico1loam.models

import com.google.firebase.Timestamp

data class Mensaje(
    var mensaje: String = "",
    var usuario: String = "",
    var timestamp: Timestamp = Timestamp.now()
)
