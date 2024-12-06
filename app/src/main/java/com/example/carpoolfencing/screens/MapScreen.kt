package com.example.carpoolfencing.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.carpoolfencing.constants.GeofenceConstant
import com.example.carpoolfencing.viewmodel.RoutingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*

@Composable
fun MapScreen(viewModel: RoutingViewModel, navController: NavController) {
    val startCoordinates by viewModel.startCoordinates.collectAsState()
    val endCoordinates by viewModel.endCoordinates.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val geofences by viewModel.geofences.collectAsState()
    val radius by viewModel.radius.collectAsState()

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(startCoordinates, endCoordinates) {
        if (startCoordinates != null && endCoordinates != null) {
            val bounds = LatLngBounds.builder()
                .include(startCoordinates!!)
                .include(endCoordinates!!)
                .build()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),

        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(myLocationButtonEnabled = true, zoomControlsEnabled = false),
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        startCoordinates?.let {
            Marker(state = MarkerState(position = it), title = "Start Location")
        }
        endCoordinates?.let {
            Marker(state = MarkerState(position = it), title = "End Location")
        }

        geofences.forEach { geofence ->
            Circle(
                center = geofence,
                radius = radius.toDouble(),
                strokeColor = Color.Red,
                fillColor = Color.Red.copy(alpha = 0.2f)
            )

        }
        if (routePoints.isNotEmpty()) {
            Polyline(
                points = routePoints,
                color = Color.Red,
                width = 8f
            )
            Log.d("MapScreen", "Route points: $routePoints")
        }
    }
}
