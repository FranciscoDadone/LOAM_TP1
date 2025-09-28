package com.loam.trabajopractico1loam.model

import com.google.firebase.Timestamp

/**
 * Modelo de datos para los precios de referencia en construcción
 */
data class PrecioReferencia(
    val id: String = "",
    val tipo: TipoPrecio = TipoPrecio.METRO_CUADRADO,
    val valor: Double = 0.0,
    val moneda: String = "USD",
    val unidad: String = "",
    val descripcion: String = "",
    val fechaActualizacion: Timestamp = Timestamp.now(),
    val activo: Boolean = true
)

/**
 * Enum para los diferentes tipos de precios de referencia
 */
enum class TipoPrecio(
    val displayName: String,
    val unidad: String,
    val descripcion: String,
    val icono: String
) {
    METRO_CUADRADO(
        displayName = "Metro Cuadrado de Construcción",
        unidad = "m²",
        descripcion = "Precio promedio por metro cuadrado de construcción",
        icono = "🏗️"
    ),
    HONORARIOS_PROFESIONALES(
        displayName = "Honorarios Profesionales",
        unidad = "hora",
        descripcion = "Tarifa por hora de servicios profesionales",
        icono = "👷"
    ),
    MATERIALES_BASICOS(
        displayName = "Materiales Básicos",
        unidad = "m³",
        descripcion = "Precio promedio de materiales básicos (cemento, arena, grava)",
        icono = "🧱"
    ),
    MANO_OBRA_ESPECIALIZADA(
        displayName = "Mano de Obra Especializada",
        unidad = "día",
        descripcion = "Costo diario de mano de obra especializada",
        icono = "⚡"
    )
}

/**
 * Estado de los precios para el UI
 */
data class PreciosState(
    val precios: List<PrecioReferencia> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)