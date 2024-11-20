package com.example.carpoolfencing.location

import android.Manifest
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(context: Context, onPermissionGranted: () -> Unit) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Request permission if not granted
    if (!locationPermissionState.status.isGranted) {
        LaunchedEffect(locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()  // Request permission
        }
    } else {
        // Call the provided callback if permission is already granted
        onPermissionGranted()
    }
}
