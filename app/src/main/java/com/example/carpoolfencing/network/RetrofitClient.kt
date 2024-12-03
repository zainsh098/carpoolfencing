package com.example.carpoolfencing.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.tomtom.com/routing/1/"
    private const val GEOCODE_BASE_URL = "https://api.tomtom.com/search/2/"
    val api: RoutingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RoutingApi::class.java)
    }

    val geoCodingApi: GeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl(GEOCODE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApi::class.java)
    }
}