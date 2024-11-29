package com.example.carpoolfencing.network

import RoutingApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoutingApiService {

    @GET("calculateRoute/{locations}/{contentType}")
    suspend fun getRoute(
        @Path("locations") locations: String,
        @Path("contentType") contentType: String = "json",
//        @Query("routeType") routeType: String = "fastest",
//        @Query("travelMode") travelMode: String = "car",
        @Query("key") apiKey: String =  "231bwmiYI6NZNOod9nAxYHmfPRGP5ssn"
    ): RoutingApiResponse
}