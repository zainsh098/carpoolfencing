package com.example.carpoolfencing.location

import android.Manifest
import android.content.Context
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable

fun RequestLocationPermission(context: Context, onPermissionGranted: () -> Unit) {

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    if (locationPermissionState.status.isGranted) {
        onPermissionGranted()
    } else {
        locationPermissionState.run { launchPermissionRequest() }
    }
}