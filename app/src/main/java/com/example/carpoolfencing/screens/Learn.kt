package com.example.carpoolfencing.screens

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import com.example.carpoolfencing.constants.GeofenceConstant
import com.example.carpoolfencing.geofence.GeofenceUtil
import com.example.carpoolfencing.location.LocationUtil
import com.example.carpoolfencing.location.RequestLocationPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

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
//        modifier = Modifier.padding(contentPadding),
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
    val geofenceCreated by remember { mutableStateOf(false) }
    val googleMap: GoogleMap? by remember { mutableStateOf(null) }

    RequestLocationPermission(context = context) {
        permissionGranted = true
        locationUtil.getLocation(context) { newLocation ->
            location = newLocation
            newLocation?.let {
                googleMap?.let { map ->
                    geofenceUtil.createGeoFence(it.latitude, it.longitude, map)
                }
            }
        }
    }

    if (permissionGranted) {
        if (location != null) {
            ShowMap(location!!, geofenceCreated)
        } else {
            Text("Fetching location...")
        }
    } else {
        Text("Location permission is required to display the map.")
    }
}

@Composable
fun ShowMap(location: Location, geofenceCreated: Boolean) {
    val context = LocalContext.current
    val geofenceUtil = remember { GeofenceUtil(context) }
    val position = LatLng(location.latitude, location.longitude)
    val markerState = remember { MarkerState(position = position) }

    val pos=CameraPosition(
        LatLng(location.latitude,location.longitude),
        15f,
        0f,
        0f,)


    // Initialize camera position state with the current location and a zoom level
    val cameraPositionState = rememberCameraPositionState {
        CameraPositionState(pos)
    }

    // Update camera to user's location when location is available
//    LaunchedEffect(location) {
//        cameraPositionState.move(
//            CameraUpdateFactory.newLatLngZoom(position, 15f)
//        )
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
    ) {
        GoogleMap(

            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = true, // Enable zoom controls
                zoomGesturesEnabled = true, // Allow pinch-to-zoom
                scrollGesturesEnabled = true, // Allow scrolling/panning
                tiltGesturesEnabled = true,
            ),
            properties = MapProperties(isMyLocationEnabled = true,),
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(state = markerState)

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ShowPreview() {
    val mockNavController = TestNavHostController(LocalContext.current)
    LearnScreen(navController = mockNavController)
}
