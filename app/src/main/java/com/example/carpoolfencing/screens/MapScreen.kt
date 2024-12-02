package com.example.carpoolfencing.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.carpoolfencing.models.RoutingApiResponse
import com.example.carpoolfencing.viewmodel.RoutingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(viewModel: RoutingViewModel, navController: NavController) {
    val startCoordinates by viewModel.startCoordinates.collectAsState()
    val endCoordinates by viewModel.endCoordinates.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()

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
        // Add markers for start and end locations
        startCoordinates?.let {
            Marker(state = MarkerState(position = it), title = "Start Location")
        }
        endCoordinates?.let {
            Marker(state = MarkerState(position = it), title = "End Location")
        }

        // Draw the polyline for the route
        if (routePoints.isNotEmpty()) {
            Polyline(
                points = routePoints,
                color = Color.Red,
                width = 8f
            )
        }
    }
}

@Composable
fun DrawRouteOnMap(routeResponse: RoutingApiResponse) {
    val points = routeResponse.routes.firstOrNull()?.legs?.firstOrNull()?.points

    val latLngList = points?.map { point ->
        LatLng(point.latitude, point.longitude)
    } ?: emptyList()

    if (latLngList.isNotEmpty()) {
        Polyline(
            points = latLngList,
            color = Color.Red, // Set polyline color
            width = 8f // Set the polyline width
        )
        Log.d("MapScreen", "Adding polyline with ${latLngList.size} points.")
    } else {
        Log.e("MapScreen", "No route points available")
    }
}
