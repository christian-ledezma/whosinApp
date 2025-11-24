package com.ucb.whosin.features.event.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationViewModel : ViewModel() {
    private val _selectedLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val selectedLocation: StateFlow<Pair<Double, Double>?> = _selectedLocation.asStateFlow()

    fun setLocation(lat: Double, lng: Double) {
        _selectedLocation.value = Pair(lat, lng)
    }

    fun clearLocation() {
        _selectedLocation.value = null
    }
}