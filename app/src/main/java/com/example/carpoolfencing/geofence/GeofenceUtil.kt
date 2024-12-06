package com.example.carpoolfencing.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.carpoolfencing.broadcast.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceUtil(private val context: Context) {
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private var geofencePendingIntent: PendingIntent? = null

    @SuppressLint("MissingPermission")
    fun createGeoFence(latitude: Double, longitude: Double, radius: Float) {
        val geofence = Geofence.Builder()
            .setRequestId("geo_${latitude}_${longitude}")
            .setCircularRegion(latitude, longitude, radius)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencePendingIntent = geofencePendingIntent ?: PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Remove existing geofences before adding the updated one
        geofencePendingIntent?.let {
            geofencingClient.removeGeofences(it).addOnCompleteListener {
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent!!)
                    .addOnSuccessListener {
                        Log.d("GeofenceUtil", "Geofence created/updated with radius: $radius meters")
                    }
                    .addOnFailureListener { e ->
                        Log.e("GeofenceUtil", "Failed to create/update geofence: ${e.message}")
                    }
            }
        }
    }
}
