package com.example.carpoolfencing.repository

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SharedRepository {

    private val _startCoordinates = MutableStateFlow<LatLng?>(null)
    val startCoordinates: StateFlow<LatLng?> = _startCoordinates

    private val _endCoordinates = MutableStateFlow<LatLng?>(null)
    val endCoordinates: StateFlow<LatLng?> = _endCoordinates


    fun setStartCoordinates(coordinates: LatLng) {
        _startCoordinates.value = coordinates
    }

    fun setEndCoordinates(coordinates: LatLng) {
        _endCoordinates.value = coordinates

    }


}