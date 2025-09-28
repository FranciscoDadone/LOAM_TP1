package com.loam.trabajopractico1loam.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loam.trabajopractico1loam.model.Dolar
import com.loam.trabajopractico1loam.services.DolarService
import kotlinx.coroutines.launch

data class DolarUiState(
    val dolar: Dolar? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DolarViewModel : ViewModel() {
    private val dolarService = DolarService()
    
    private val _uiState = MutableLiveData(DolarUiState(isLoading = true))
    val uiState: LiveData<DolarUiState> = _uiState

    init {
        loadDolarOficial()
    }

    fun loadDolarOficial() {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true, errorMessage = null)
            try {
                val dolar = dolarService.getDolarOficial()
                System.out.println("Dolar: " + dolar.compra);
                _uiState.value = _uiState.value?.copy(
                    dolar = dolar,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun retry() {
        loadDolarOficial()
    }
}