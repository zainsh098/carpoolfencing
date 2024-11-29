package com.example.carpoolfencing.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.carpoolfencing.viewmodel.RoutingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.compose.*

@Composable
fun MapScreen(viewModel: RoutingViewModel, navController: NavController) {
    val startCoordinates by viewModel.startCoordinates.collectAsState()
    val endCoordinates by viewModel.endCoordinates.collectAsState()
    val routeResponse by viewModel.routeResponse.collectAsState()

    val cameraPositionState = rememberCameraPositionState()

    // Adjust the camera to show both start and end coordinates
    LaunchedEffect(startCoordinates, endCoordinates) {
        if (startCoordinates != null && endCoordinates != null) {
            val bounds = LatLngBounds.builder()
                .include(startCoordinates!!)
                .include(endCoordinates!!)
                .build()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    // Initialize the GoogleMap composable
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true,
            zoomControlsEnabled = false
        ),
        properties = MapProperties(
            isMyLocationEnabled = true
        )
    ) {
        // Add markers for start and end locations
        startCoordinates?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Start Location",
                snippet = "This is your starting point."
            )
        }
        endCoordinates?.let {
            Marker(
                state = MarkerState(position = it),
                title = "End Location",
                snippet = "This is your destination."
            )
        }

        // Draw the polygon for the route, if available
        routeResponse?.routes?.firstOrNull()?.legs?.firstOrNull()?.points?.let { points ->
            // Log the points for debugging
            Log.d("MapScreen", "Route Points: $points")

            // Convert the points to LatLng
            val polygonPoints = points.map { LatLng(it.latitude, it.longitude) }

            // Log polygon points to verify they're correct
            Log.d("MapScreen", "Polygon Points: $polygonPoints")

            if (polygonPoints.isNotEmpty()) {
                // Create PolygonOptions instance and add the points to the polygon
                val polygonOptions = PolygonOptions()
                    .addAll(polygonPoints)
                    .strokeColor(0xFFFF0000.toInt()) // Border color
                    .fillColor(0x7FFF0000.toInt())  // Fill color with transparency (50% opacity)

                // Add the polygon to the map
                Polygon(polygonOptions.points)
            } else {
                Log.e("MapScreen", "No points available to draw a polygon.")
            }
        } ?: run {
            Log.e("MapScreen", "No route points available.")
        }
    }
}
