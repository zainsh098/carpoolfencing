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
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceUtil(private val context: Context) {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    fun createGeoFence(latitude: Double, longitude: Double) {
        val geofence = Geofence.Builder()
            .setRequestId("geo_${latitude}_$longitude")
            .setCircularRegion(latitude, longitude, GeofenceConstant.GEOFENCE_RADIUS_IN_METERS) // 900 meters radius
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Check for permission before adding the geofence
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("GeofenceUtil", "Location permission not granted.")
            return
        }

        // Add geofence
        geofencingClient.addGeofences(geofenceRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d("GeofenceUtil", "Geofence added successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("GeofenceUtil", "Failed to add geofence: ${e.message}")
            }
    }

    fun removeGeoFence(requestId: String) {
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
            .addGeofence(
                Geofence.Builder()
                    .setRequestId(requestId)
                    .build()
            )
            .build()

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener {
                Log.d("GeofenceUtil", "Geofence removed successfully")
            }
            .addOnFailureListener { e ->
                Log.e("GeofenceUtil", "Failed to remove geofence: ${e.message}")
            }
    }
}
