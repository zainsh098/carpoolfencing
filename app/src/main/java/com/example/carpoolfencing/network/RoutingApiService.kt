package com.example.carpoolfencing.network

import com.example.carpoolfencing.models.GeocodingModelResponse
import com.example.carpoolfencing.models.Position
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

interface GeocodingApi {
    @GET("geocode/{query}.{ext}")
    suspend fun getGeocode(
        @Path("query") query: String,
        @Path("ext") ext: String = "json",
        @Query("limit") limit: Int = 1,
        @Query("lat") lat: Float = 37.337f,
        @Query("lon") lon: Float = -121.89f,
        @Query("key") apiKey: String,
    ): GeocodingModelResponse
}


//class GeoRepository(private val api: GeocodingApi) {
//    suspend fun fetchGeoCode(
//        query: String,
////        ext: String = "json",
//        lat: Float = 37.337f,
//        lon: Float = -121.89f,
//        apiKey: String): GeocodingModelResponse {
//        return api.getGeocode(query, lat = lat, lon = lon, apiKey = apiKey)
//    }



