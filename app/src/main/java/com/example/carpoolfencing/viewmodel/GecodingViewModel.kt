package com.example.carpoolfencing.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolfencing.network.RetrofitInstance
import kotlinx.coroutines.launch

class GeocodingViewModel(

) : ViewModel() {

    fun fetchCoordinates(query: String) {
        viewModelScope.launch {

            val response = RetrofitInstance.geoCodingApi.getGeocode(
                query,
                apiKey = "231bwmiYI6NZNOod9nAxYHmfPRGP5ssn"
            )

            val lat = response.results[0].position.lat
            val long = response.results[0].position.lon





            Log.d("Lat & Long ", "${lat} ${long}}")

            Log.d("API REPOSNE ", response.toString())
        }


    }


}