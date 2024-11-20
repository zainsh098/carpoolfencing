// File: com/example/carpoolfencing/screens/MapScreen.kt

package com.example.carpoolfencing.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.carpoolfencing.util.GeofenceUtil
import com.example.carpoolfencing.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapScreen(viewModel: MapViewModel, navController: NavController) {
    val context = LocalContext.current

    // Collect StateFlows from ViewModel
    val currentLocation by viewModel.currentLocation.collectAsState()
    val startCoordinates by viewModel.startCoordinates.collectAsState()
    val endCoordinates by viewModel.endCoordinates.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Log coordinates for debugging
    Log.d("MapScreen", "Current Location: $currentLocation")
    Log.d("MapScreen", "Start Coordinates: $startCoordinates")
    Log.d("MapScreen", "End Coordinates: $endCoordinates")

    // Initialize camera position
    val cameraPositionState = rememberCameraPositionState {
        currentLocation?.let {
            position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    // Add LaunchedEffect to update camera position when start or end coordinates change
    LaunchedEffect(startCoordinates, endCoordinates) {
        Log.d("MapScreen", "Coordinates updated: start=$startCoordinates, end=$endCoordinates")

        if (startCoordinates != null && endCoordinates != null) {
            Log.d("MapScreen", "Moving camera to show both locations")
            val bounds = LatLngBounds.builder()
                .include(startCoordinates!!)
                .include(endCoordinates!!)
                .build()

            // Adjust the camera to include both locations with padding
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(bounds, 100)
            )
        } else if (currentLocation != null) {
            Log.d("MapScreen", "Moving camera to current location")
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15f)
            )
        }
    }

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
        // Add a marker for current location if available
        currentLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "You are here",
                snippet = "Current Location"
            )
        }

        // Start Location Marker
        startCoordinates?.let {
            Log.d("MapScreen", "Placing start location marker: $it")
            Marker(
                state = MarkerState(position = it),
                title = "Start Location",
                snippet = "Your starting point"
            )
        }

        // End Location Marker
        endCoordinates?.let {
            Log.d("MapScreen", "Placing end location marker: $it")
            Marker(
                state = MarkerState(position = it),
                title = "End Location",
                snippet = "Your destination"
            )
        }

        // Draw Polyline between the two locations if both are available
        if (startCoordinates != null && endCoordinates != null) {
            Polyline(
                points = listOfNotNull(startCoordinates, endCoordinates),
                color = Color.Green,
                width = 5f
            )
        }

    }

    // Handle geofencing
    LaunchedEffect(startCoordinates, endCoordinates) {
        if (startCoordinates != null && endCoordinates != null) {
            Log.d("MapScreen", "Setting up geofencing for start and end locations")
            val geofenceUtil = GeofenceUtil(context)
            geofenceUtil.createGeoFence(
                latitude = startCoordinates!!.latitude,
                longitude = startCoordinates!!.longitude
            )
            geofenceUtil.createGeoFence(
                latitude = endCoordinates!!.latitude,
                longitude = endCoordinates!!.longitude
            )
        }
    }

    // Optionally, display error messages
    errorMessage?.let { error ->
        // Implement your UI for displaying error messages, e.g., a Snackbar or Toast
        Log.e("MapScreen", "Error: $error")
    }
}
