package com.loam.trabajopractico1loam.services

import com.google.gson.Gson
import com.loam.trabajopractico1loam.model.Dolar
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class DolarService {
    private val gson = Gson()
    
    suspend fun getDolarOficial(): Dolar {
        val client = HttpClient(CIO)
        val response = client.get("https://dolarapi.com/v1/dolares/oficial")
        val jsonString = response.bodyAsText()
        client.close()
        return gson.fromJson(jsonString, Dolar::class.java)
    }
}