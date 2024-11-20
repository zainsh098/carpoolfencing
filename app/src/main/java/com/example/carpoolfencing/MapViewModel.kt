// File: com/example/carpoolfencing/viewmodel/MapViewModel.kt

package com.example.carpoolfencing.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    // Expose startLocation and endLocation as StateFlow
    private val _startLocation = MutableStateFlow<String>("")
    val startLocation: StateFlow<String> = _startLocation

    private val _endLocation = MutableStateFlow<String>("")
    val endLocation: StateFlow<String> = _endLocation

    // Expose startCoordinates and endCoordinates as StateFlow
    private val _startCoordinates = MutableStateFlow<LatLng?>(null)
    val startCoordinates: StateFlow<LatLng?> = _startCoordinates

    private val _endCoordinates = MutableStateFlow<LatLng?>(null)
    val endCoordinates: StateFlow<LatLng?> = _endCoordinates

    init {
        fetchCurrentLocation()
    }

    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    _currentLocation.value = LatLng(it.latitude, it.longitude)
                    Log.d("MapViewModel", "Current location set to: ${_currentLocation.value}")
                } ?: run {
                    _errorMessage.value = "Unable to determine current location."
                    Log.e("MapViewModel", "Unable to determine current location.")
                }
            }
            .addOnFailureListener {
                _errorMessage.value = "Failed to get current location."
                Log.e("MapViewModel", "Failed to get current location: ${it.message}")
            }
    }

    fun setStartLocation(address: String) {
        _startLocation.value = address
    }

    fun setEndLocation(address: String) {
        _endLocation.value = address
    }

    fun updateStartCoordinates(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MapViewModel", "Updating start location: ${_startLocation.value}")
            val coordinates = getCoordinatesFromAddress(context, _startLocation.value)
            if (coordinates == null) {
                _errorMessage.value = "Start location not found."
                Log.e("MapViewModel", "Start location not found.")
            } else {
                _startCoordinates.value = coordinates
                _errorMessage.value = null
                Log.d("MapViewModel", "Start location updated: $_startCoordinates")
            }
        }
    }

    fun updateEndCoordinates(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MapViewModel", "Updating end location: ${_endLocation.value}")
            val coordinates = getCoordinatesFromAddress(context, _endLocation.value)
            if (coordinates == null) {
                _errorMessage.value = "End location not found."
                Log.e("MapViewModel", "End location not found.")
            } else {
                _endCoordinates.value = coordinates
                _errorMessage.value = null
                Log.d("MapViewModel", "End location updated: $_endCoordinates")
            }
        }
    }

    private fun getCoordinatesFromAddress(context: Context, address: String): LatLng? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                LatLng(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: IOException) {
            Log.e("MapViewModel", "Geocoder IOException: ${e.message}")
            null
        } catch (e: IllegalArgumentException) {
            Log.e("MapViewModel", "Geocoder IllegalArgumentException: ${e.message}")
            null
        }
    }
}
