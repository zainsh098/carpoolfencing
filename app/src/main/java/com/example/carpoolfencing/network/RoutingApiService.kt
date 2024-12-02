

package com.example.carpoolfencing.network

import com.example.carpoolfencing.models.RoutingApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoutingApi {
    @GET("calculateRoute/{locations}/json")
    suspend fun getRoute(
        @Path("locations") locations: String, // Full coordinates as string
        @Query("routeRepresentation") routeRepresentation: String = "polyline", // Default to polyline
        @Query("key") apiKey: String // API key
    ): RoutingApiResponse
}

