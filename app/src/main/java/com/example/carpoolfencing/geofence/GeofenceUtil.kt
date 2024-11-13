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
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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

    fun createGeoFence(lat: Double, lng: Double, googleMap: GoogleMap) {
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
                drawCircleOnMap(LatLng(lat, lng), googleMap) // Draw the circle on the map
            }
            .addOnFailureListener { e ->
                val apiException = e as ApiException
                Log.e("GeofenceError", "Geofence creation failed with status code: ${apiException.statusCode}")
                apiException.printStackTrace()
            }
    }

    private fun drawCircleOnMap(latLng: LatLng, googleMap: GoogleMap) {
        val circleOptions = CircleOptions()
            .center(latLng)
            .radius(GeofenceConstant.GEOFENCE_RADIUS_IN_METERS.toDouble())
            .strokeColor(0x5500FF00) // Green color with transparency
            .fillColor(0x2200FF00) // Light green fill color
            .strokeWidth(5f)

        googleMap.addCircle(circleOptions)
        googleMap.addMarker(MarkerOptions().position(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
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
