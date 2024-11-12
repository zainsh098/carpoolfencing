package com.example.carpoolfencing.geofence

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.carpoolfencing.broadcast.GeofenceBroadcastReceiver
import com.example.carpoolfencing.constants.GeofenceConstant
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceUtil(private val context: Context) {
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    private fun buildGeofence(lat: Double, lng: Double): Geofence {
        return Geofence.Builder()
            .setRequestId(GeofenceConstant.GEOFENCE_ID)
            .setCircularRegion(lat, lng, GeofenceConstant.GEOFENCE_RADIUS_IN_METERS)
            .setExpirationDuration(GeofenceConstant.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    private fun buildGeoFencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()
    }

    fun createGeoFence(lat: Double, lng: Double, onGeofenceCreated: () -> Unit) {
        val geofence = buildGeofence(lat, lng)
        val geofencingRequest = buildGeoFencingRequest(geofence)
        val geofencePendingIntent = getGeofencePendingIntent()

        // Check permissions before proceeding
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("GeofenceError", "Permission not granted for location")
            return
        }

        // Add geofence
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
            .addOnSuccessListener {
                onGeofenceCreated() // Callback after geofence creation
            }
            .addOnFailureListener { e ->
                val apiException = e as ApiException
                Log.e("GeofenceError", "Geofence creation failed with status code: ${apiException.statusCode}")
                apiException.printStackTrace() // Log the stack trace for further debugging
            }
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java) // Geofence receiver
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
