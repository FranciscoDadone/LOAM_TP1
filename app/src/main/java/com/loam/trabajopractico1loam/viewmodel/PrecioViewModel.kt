package com.loam.trabajopractico1loam.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loam.trabajopractico1loam.model.PrecioReferencia
import com.loam.trabajopractico1loam.services.PreciosService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class PreciosUiState(
    val precios: List<PrecioReferencia> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PreciosViewModel : ViewModel() {
    private val preciosService = PreciosService()
    
    private val _uiState = MutableLiveData(PreciosUiState(isLoading = true))
    val uiState: LiveData<PreciosUiState> = _uiState

    init {
        inicializarPrecios()
        observarPrecios()
    }

    private fun inicializarPrecios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true, errorMessage = null)
            
            preciosService.inicializarPreciosPorDefecto()
                .onFailure { error ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = "Error al inicializar precios: ${error.message}"
                    )
                }
        }
    }

    private fun observarPrecios() {
        viewModelScope.launch {
            preciosService.getPreciosFlow()
                .catch { error ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar precios: ${error.message}"
                    )
                }
                .collect { precios ->
                    _uiState.value = _uiState.value?.copy(
                        precios = precios,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }

    fun retry() {
        inicializarPrecios()
        observarPrecios()
    }
}