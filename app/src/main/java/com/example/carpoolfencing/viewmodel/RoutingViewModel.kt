package com.example.carpoolfencing.viewmodel

import RoutingApiResponse
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolfencing.network.RetrofitInstance
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class RoutingViewModel : ViewModel() {

    // StateFlow to hold the response from the routing API
    private val _routeResponse = MutableStateFlow<RoutingApiResponse?>(null)
    val routeResponse: StateFlow<RoutingApiResponse?> = _routeResponse

    // StateFlows to store start and end locations (as LatLng coordinates)
    private val _startCoordinates = MutableStateFlow<LatLng?>(null)
    val startCoordinates: StateFlow<LatLng?> = _startCoordinates

    private val _endCoordinates = MutableStateFlow<LatLng?>(null)
    val endCoordinates: StateFlow<LatLng?> = _endCoordinates

    // Set the start location and fetch coordinates
    fun setStartLocation(location: String, context: Context) {
        val coordinates = getCoordinatesFromAddress(location, context)
        _startCoordinates.value = coordinates
    }

    // Set the end location and fetch coordinates
    fun setEndLocation(location: String, context: Context) {
        val coordinates = getCoordinatesFromAddress(location, context)
        _endCoordinates.value = coordinates
    }

    // Helper function to convert an address to LatLng coordinates using Geocoder
    private fun getCoordinatesFromAddress(address: String, context: Context): LatLng? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addressList = geocoder.getFromLocationName(address, 1)
            if (addressList?.isNotEmpty() == true) {
                val location = addressList[0]
                Log.d("Geocoder", "Coordinates for $address: ${location.latitude}, ${location.longitude}")
                LatLng(location.latitude, location.longitude)
            } else {
                Log.e("Geocoder", "No address found for $address")
                null
            }
        } catch (e: IOException) {
            Log.e("RoutingViewModel", "Geocoder error: ${e.message}")
            null
        }
    }


    // Fetch routing data from API when both start and end coordinates are set
    fun fetchRoute() {
        val start = _startCoordinates.value
        val end = _endCoordinates.value

        if (start != null && end != null) {
            Log.d("RoutingViewModel", "Start Coordinates: ${start.latitude}, ${start.longitude}")
            Log.d("RoutingViewModel", "End Coordinates: ${end.latitude}, ${end.longitude}")

            val locations = "${start.latitude},${start.longitude}|${end.latitude},${end.longitude}"
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.getRoute(locations, apiKey = "231bwmiYI6NZNOod9nAxYHmfPRGP5ssn")
                    _routeResponse.value = response
                } catch (e: Exception) {
                    Log.e("RoutingViewModel", "API call error: ${e.message}")
                }
            }
        } else {
            Log.e("RoutingViewModel", "Start or end location is missing.")
        }

    }
}
