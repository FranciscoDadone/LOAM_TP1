package com.loam.trabajopractico1loam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.loam.trabajopractico1loam.model.PrecioReferencia
import com.loam.trabajopractico1loam.model.PreciosState
import com.loam.trabajopractico1loam.repository.PreciosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar el estado de los precios de referencia
 */
class PreciosViewModel : ViewModel() {
    
    private val repository = PreciosRepository(Firebase.firestore)
    
    private val _uiState = MutableStateFlow(PreciosState())
    val uiState: StateFlow<PreciosState> = _uiState.asStateFlow()
    
    init {
        inicializarPrecios()
        observarPrecios()
    }
    
    /**
     * Inicializa los precios por defecto si no existen
     */
    private fun inicializarPrecios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            repository.inicializarPreciosPorDefecto()
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al inicializar precios: ${error.message}"
                    )
                }
        }
    }
    
    /**
     * Observa los cambios en los precios en tiempo real
     */
    private fun observarPrecios() {
        viewModelScope.launch {
            repository.getPreciosFlow()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al cargar precios: ${error.message}"
                    )
                }
                .collect { precios ->
                    _uiState.value = _uiState.value.copy(
                        precios = precios,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }
    
    /**
     * Actualiza un precio específico
     */
    fun actualizarPrecio(precio: PrecioReferencia) {
        viewModelScope.launch {
            repository.actualizarPrecio(precio)
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Error al actualizar precio: ${error.message}"
                    )
                }
        }
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}