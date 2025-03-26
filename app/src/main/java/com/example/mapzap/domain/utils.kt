package com.example.mapzap.domain

//Validate the latitude and the longitude
 fun isValidLatLng(latitude: Double, longitude: Double): Boolean {
    return latitude.isFinite() && longitude.isFinite() && latitude in -90.0..90.0 && longitude in -180.0..180.0
}