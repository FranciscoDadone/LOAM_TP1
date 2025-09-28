package com.loam.trabajopractico1loam.services

import com.loam.trabajopractico1loam.model.Dolar
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

class DolarService {
    suspend fun getDolarOficial(): Dolar {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
        return client.get("https://dolarapi.com/v1/dolares/oficial").body<Dolar>()
    }
}