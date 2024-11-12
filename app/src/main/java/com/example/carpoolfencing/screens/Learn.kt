package com.example.carpoolfencing.screens

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import com.example.carpoolfencing.geofence.GeofenceUtil
import com.example.carpoolfencing.location.LocationUtil
import com.example.carpoolfencing.location.RequestLocationPermission
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LearnScreen(navController: NavController) {
    Scaffold(
        floatingActionButtonPosition = FabPosition.Start,
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.LocationOn, contentDescription = "icon")
            }
        }
    ) { contentPadding ->
        LearnUI(contentPadding)
    }
}

@Composable
fun LearnUI(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier.padding(contentPadding),
        verticalArrangement = Arrangement.Center
    ) {
        MapScreen()
    }
}

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val geofenceUtil = remember { GeofenceUtil(context) }
    val locationUtil = remember { LocationUtil(context) }
    var location by remember { mutableStateOf<Location?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }
    var geofenceCreated by remember { mutableStateOf(false) }

    // Request location permission and update state when granted
    RequestLocationPermission(context = context) {
        permissionGranted = true
        locationUtil.getLocation(context) { newLocation ->
            location = newLocation
            // Create geofence once location is retrieved
            newLocation?.let {
                geofenceUtil.createGeoFence(it.latitude, it.longitude)
                {
                    geofenceCreated = true


                }// Create geofence at user's location
            }
        }
    }

    // Show map if location is available; otherwise, show message based on permission status
    if (permissionGranted) {
        if (location != null) {
            ShowMap(location!!)
        } else {
            Text("Fetching location...")
        }
    } else {
        Text("Location permission is required to display the map.")
    }
}

@Composable
fun ShowMap(location: Location) {
    val position = LatLng(location.latitude, location.longitude)
    val markerState = remember { MarkerState(position = position) }

    val coordinatesCameraPosition = CameraPosition(
        LatLng(location.latitude, location.longitude),
        10f,
        0f,
        0f,
    )

    val cameraPositionState = rememberCameraPositionState {
        CameraPositionState(
            coordinatesCameraPosition

        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
    ) {
        GoogleMap(
            uiSettings = MapUiSettings(myLocationButtonEnabled = true),
            properties = MapProperties(isMyLocationEnabled = true),
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(state = markerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowP() {
    val mockNavController = TestNavHostController(LocalContext.current)
    LearnScreen(navController = mockNavController)
}
