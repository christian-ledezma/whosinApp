package com.ucb.whosin.features.event.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.usecase.GetEventsWhereUserIsGuardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GuardEventsUiState(
    val events: List<EventModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class GuardEventsViewModel(
    private val getEventsWhereUserIsGuardUseCase: GetEventsWhereUserIsGuardUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow(GuardEventsUiState())
    val uiState: StateFlow<GuardEventsUiState> = _uiState.asStateFlow()

    init {
        Log.d("VIEWMODEL_TEST", "========================================")
        Log.d("VIEWMODEL_TEST", "GuardEventsViewModel INIT llamado")
        Log.d("VIEWMODEL_TEST", "========================================")
        loadGuardEvents()
    }

    fun loadGuardEvents() {
        try {
            Log.d("VIEWMODEL_TEST", "========================================")
            Log.d("VIEWMODEL_TEST", "loadGuardEvents() INICIO")

            val currentUser = firebaseAuth.currentUser
            Log.d("AUTH_TEST", "currentUser = ${currentUser?.uid}")
            Log.d("AUTH_TEST", "ViewModel currentUser = ${firebaseAuth.currentUser?.uid}")

            if (currentUser == null) {
                Log.e("VIEWMODEL_TEST", "❌ Usuario no autenticado")
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Usuario no autenticado",
                    isLoading = false
                )
                return
            }

            Log.d("VIEWMODEL_TEST", "✅ Usuario autenticado: ${currentUser.uid}")
            Log.d("VIEWMODEL_TEST", "Lanzando coroutine...")

            viewModelScope.launch {
                try {
                    Log.d("VIEWMODEL_TEST", "Coroutine iniciada")
                    _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    Log.d("VIEWMODEL_TEST", "Estado actualizado a loading")

                    Log.d("VIEWMODEL_TEST", "Llamando al UseCase...")
                    val events = getEventsWhereUserIsGuardUseCase(currentUser.uid)
                    Log.d("VIEWMODEL_TEST", "UseCase retornó: ${events.size} eventos")

                    _uiState.value = _uiState.value.copy(
                        events = events,
                        isLoading = false
                    )
                    Log.d("VIEWMODEL_TEST", "✅ Estado actualizado con eventos")
                } catch (e: Exception) {
                    Log.e("VIEWMODEL_TEST", "❌ EXCEPCIÓN en coroutine", e)
                    Log.e("VIEWMODEL_TEST", "Mensaje: ${e.message}")
                    Log.e("VIEWMODEL_TEST", "Tipo: ${e.javaClass.simpleName}")
                    Log.e("VIEWMODEL_TEST", "Stack: ${e.stackTraceToString()}")

                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al cargar eventos",
                        isLoading = false
                    )
                }
            }

            Log.d("VIEWMODEL_TEST", "Después de launch")
            Log.d("VIEWMODEL_TEST", "========================================")

        } catch (e: Exception) {
            Log.e("VIEWMODEL_TEST", "❌ EXCEPCIÓN ANTES de launch", e)
            Log.e("VIEWMODEL_TEST", "Mensaje: ${e.message}")
            Log.e("VIEWMODEL_TEST", "Tipo: ${e.javaClass.simpleName}")
            Log.e("VIEWMODEL_TEST", "Stack: ${e.stackTraceToString()}")

            _uiState.value = _uiState.value.copy(
                errorMessage = e.message ?: "Error al cargar eventos",
                isLoading = false
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}