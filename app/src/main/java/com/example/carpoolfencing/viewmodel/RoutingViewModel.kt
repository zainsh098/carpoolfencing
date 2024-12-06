package com.example.carpoolfencing.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolfencing.constants.GeofenceConstant
import com.example.carpoolfencing.geofence.GeofenceUtil
import com.example.carpoolfencing.network.RetrofitInstance
import com.example.carpoolfencing.repository.SharedRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoutingViewModel : ViewModel() {


    private val _radius = MutableStateFlow(GeofenceConstant.GEOFENCE_RADIUS_IN_METERS)
    val radius = _radius.asStateFlow()

    private val _geofences = MutableStateFlow<List<LatLng>>(emptyList())
    val geofences: StateFlow<List<LatLng>> = _geofences

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints

    private val _startCoordinates = MutableStateFlow<LatLng?>(null)
    val startCoordinates: StateFlow<LatLng?> = _startCoordinates

    private val _endCoordinates = MutableStateFlow<LatLng?>(null)
    val endCoordinates: StateFlow<LatLng?> = _endCoordinates

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun observeGeocodingUpdates(
        geocodingViewModel: GeocodingViewModel,
        geofenceUtil: GeofenceUtil
    ) {
        viewModelScope.launch {
            geocodingViewModel.coordinatesFetched.collect { isFetched ->
                if (isFetched) {
                    fetchRoute()
                    geoFence(geofenceUtil)
                }
            }
        }
    }


    fun changeRadius(newRadius: Float) {
        _radius.value = newRadius

    }


    private fun geoFence(geofenceUtil: GeofenceUtil) {
        viewModelScope.launch {
            _startCoordinates.value = SharedRepository.startCoordinates.value
            _endCoordinates.value = SharedRepository.endCoordinates.value

            // Add geofences for start and end locations
            _startCoordinates.value?.let {
                geofenceUtil.createGeoFence(it.latitude, it.longitude, _radius.value)
                _geofences.value = _geofences.value + it
            }

            _endCoordinates.value?.let {
                geofenceUtil.createGeoFence(it.latitude, it.longitude, _radius.value)
                _geofences.value = _geofences.value + it
            }

            // Notify that geofences have been added
            Log.d("RoutingViewModel", "Geofences added: $_geofences")
        }
    }


    fun fetchRoute() {
        viewModelScope.launch {
            _startCoordinates.value = SharedRepository.startCoordinates.value
            _endCoordinates.value = SharedRepository.endCoordinates.value

            val startCoordinates = _startCoordinates.value
            val endCoordinates = _endCoordinates.value

            if (startCoordinates != null && endCoordinates != null) {
                val locations =
                    "${startCoordinates.latitude},${startCoordinates.longitude}:${endCoordinates.latitude},${endCoordinates.longitude}"
                Log.d("Coodites :", locations)
                try {

                    val response = RetrofitInstance.api.getRoute(
                        locations = locations,
                        routeRepresentation = "polyline",
                        travelMode = "car",
                        apiKey = "231bwmiYI6NZNOod9nAxYHmfPRGP5ssn"
                    )

                    if (response.routes.isNotEmpty()) {
                        val points = response.routes[0].legs[0].points
                        _routePoints.value = points.map { LatLng(it.latitude, it.longitude) }
                    } else {
                        _errorMessage.value = "No routes found."
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error fetching route: ${e.message}"
                }
            }
        }
    }
}
