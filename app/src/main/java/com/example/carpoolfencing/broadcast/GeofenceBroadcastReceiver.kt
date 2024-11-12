package com.example.carpoolfencing.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.Geofence

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        // Check if geofencingEvent is null
        if (geofencingEvent == null) {
            Log.e("GeofenceReceiver", "Geofencing event is null!")
            return
        }

        // Handle errors from geofencing event
        if (geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Geofence error: ${geofencingEvent.errorCode}")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d("GeofenceReceiver", "User entered geofence area.")
                // Handle user entering geofence (e.g., show notification)
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.d("GeofenceReceiver", "User exited geofence area.")
                // Handle user exiting geofence (e.g., show notification)
            }
            else -> {
                Log.e("GeofenceReceiver", "Invalid geofence transition")
            }
        }
    }
}
