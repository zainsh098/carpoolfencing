package com.example.carpoolfencing.viewmodel

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolfencing.models.Point
import com.example.carpoolfencing.models.RoutingApiResponse
import com.example.carpoolfencing.network.RetrofitInstance
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class RoutingViewModel : ViewModel() {

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints

    private val _startCoordinates = MutableStateFlow<LatLng?>(null)
    val startCoordinates: StateFlow<LatLng?> = _startCoordinates

    private val _endCoordinates = MutableStateFlow<LatLng?>(null)
    val endCoordinates: StateFlow<LatLng?> = _endCoordinates

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun setStartLocation(location: String, context: Context) {
        val coordinates = getCoordinatesFromAddress(location, context)
        if (coordinates != null) {
            _startCoordinates.value = coordinates
        } else {
            _errorMessage.value = "Invalid start location."
        }
    }

    fun setEndLocation(location: String, context: Context) {
        val coordinates = getCoordinatesFromAddress(location, context)
        if (coordinates != null) {
            _endCoordinates.value = coordinates
        } else {
            _errorMessage.value = "Invalid end location."
        }
    }

    private fun getCoordinatesFromAddress(address: String, context: Context): LatLng? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addressList = geocoder.getFromLocationName(address, 1)
            if (addressList?.isNotEmpty() == true) {
                val location = addressList[0]
                LatLng(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: IOException) {
            Log.e("Geocoder", "Error fetching coordinates for address $address", e)
            null
        }
    }

    fun fetchRoute(apiKey: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val startCoordinates = _startCoordinates.value
            val endCoordinates = _endCoordinates.value

            if (startCoordinates != null && endCoordinates != null) {
                val locations = "${startCoordinates.latitude},${startCoordinates.longitude}:${endCoordinates.latitude},${endCoordinates.longitude}"
                Log.d("Route Request", "Request URL: $locations")

                try {
                    val response = RetrofitInstance.api.getRoute(
                        locations = locations,
                        routeRepresentation = "polyline",
                        apiKey = apiKey
                    )

                    if (response.routes.isNotEmpty()) {
                        val points = response.routes[0].legs[0].points
                        _routePoints.value = points.map { LatLng(it.latitude, it.longitude) }
                        Log.d("Route Points", "Fetched ${points.size} points for the route.")
                    } else {
                        _errorMessage.value = "No routes found."
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error fetching route: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}
