package com.example.mapzap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var currentLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val FINE_PERMISSION_CODE = 1
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var clientMap: GoogleMap
    private var isLocationReceived = false
    private var pinToUser: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000L)
            .setMinUpdateIntervalMillis(60000L).build()
        setMap()
    }

    private fun initMap() {
        Log.e("Mapsss", "inside the map init")
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    private fun setMap() {
        if (permissionCheck()) {
            startLocationUpdates()
            initMap()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        val permission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        ActivityCompat.requestPermissions(this, permission, FINE_PERMISSION_CODE)
    }

    private fun permissionCheck(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onMapReady(p0: GoogleMap) {
        clientMap = p0

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Mapsss", "got the permission")
                initMap()
            } else {
                Toast.makeText(this, "Needed the location permission", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getCallBackObject(): LocationCallback {
        return object : LocationCallback() {
            @SuppressLint("MissingPermission")
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.e("Mapsss", "from the object $location")
                    currentLocation = location
                    isLocationReceived = true
                    //  clientMap.isMyLocationEnabled = true
                    goToLocation(currentLocation)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        Log.e("Mapsss", "called the start")
        locationCallback = getCallBackObject()
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    private fun goToLocation(location: Location?) {
        if (location != null && clientMap != null) {
            val localLocation = LatLng(location.latitude, location.longitude)
            val preiousZoom = clientMap.cameraPosition.zoom
            pinToUser?.remove()
            pinToUser = clientMap?.addMarker(
                MarkerOptions().position(localLocation).title("Your Location").snippet(
                        "Updated: ${
                            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(
                                Date()
                            )
                        }"
                    )
            )
            clientMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(localLocation, preiousZoom))
        } else {
            Log.e("Mapsss", "Received the null in  $clientMap , $location")
        }
    }
}
