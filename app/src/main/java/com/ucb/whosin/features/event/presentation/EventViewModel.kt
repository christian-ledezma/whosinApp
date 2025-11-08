package com.ucb.whosin.features.event.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.usecase.FindByNameUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel(
    private val usecase: FindByNameUseCase
) : ViewModel() {

    sealed class EventStateUI {
        object Init : EventStateUI()
        object Loading : EventStateUI()
        class Error(val message: String) : EventStateUI()
        class Success(val event: EventModel) : EventStateUI()

        object EmptyResult : EventStateUI()
    }

    private val _state = MutableStateFlow<EventStateUI>(EventStateUI.Init)
    val state: StateFlow<EventStateUI> = _state.asStateFlow()

    fun fetchEventByName(eventName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = EventStateUI.Loading

            val result = usecase.invoke(eventName)

            result.fold(
                onSuccess = { event ->
                    if (event.name.isBlank()) {
                        _state.value = EventStateUI.EmptyResult
                    } else {
                        _state.value = EventStateUI.Success(event)
                    }
                },
                onFailure = { error ->
                    _state.value = EventStateUI.Error(
                        message = error.message ?: "Error desconocido"
                    )
                }
            )
        }
    }
    fun setInitState() {
        _state.value = EventStateUI.Init
    }
    fun setEmptySearchMessage() {
        viewModelScope.launch {
            // Muestra el mensaje por 4 segundos
            _state.value = EventStateUI.Error("Ingrese un nombre de evento para buscar.")
            delay(3000L)
            // Luego muestra el recuadro (lista vacía)
            _state.value = EventStateUI.EmptyResult
        }
    }
}