package com.example.carpoolfencing.constants

import com.google.android.gms.location.Geofence

object GeofenceConstant {

    const val GEOFENCE_RADIUS_IN_METERS = 100f  // 1 km radius
    const val GEOFENCE_EXPIRATION_IN_MILLISECONDS = Geofence.NEVER_EXPIRE
    const val GEOFENCE_ID = "MyGeofenceID"

}