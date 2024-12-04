package com.example.carpoolfencing.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolfencing.network.RetrofitInstance
import com.example.carpoolfencing.repository.SharedRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeocodingViewModel : ViewModel() {

    private val _coordinatesFetched = MutableStateFlow(false)
    val coordinatesFetched: StateFlow<Boolean> = _coordinatesFetched

    fun fetchCoordinates(queryStart: String, queryEnd: String) {
        viewModelScope.launch {
            try {
                val startResponse = RetrofitInstance.geoCodingApi.getGeocode(
                    queryStart,
                    apiKey = "231bwmiYI6NZNOod9nAxYHmfPRGP5ssn"
                )
                val startCoordinates = LatLng(
                    startResponse.results[0].position.lat,
                    startResponse.results[0].position.lon
                )
                SharedRepository.setStartCoordinates(startCoordinates)

                val endResponse = RetrofitInstance.geoCodingApi.getGeocode(
                    queryEnd,
                    apiKey = "231bwmiYI6NZNOod9nAxYHmfPRGP5ssn"
                )
                val endCoordinates = LatLng(
                    endResponse.results[0].position.lat,
                    endResponse.results[0].position.lon
                )
                SharedRepository.setEndCoordinates(endCoordinates)

                _coordinatesFetched.value = true
            } catch (e: Exception) {
                Log.e("Geocoding", "Error fetching coordinates: ${e.message}")
            }
        }
    }
}
