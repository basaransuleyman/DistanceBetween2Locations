package com.example.maps.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.roundToInt

class ViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var endLatitude = MapsActivity.FIRST_LONG_AND_LAT
    var endLongitude = MapsActivity.FIRST_LONG_AND_LAT
    var startLatitude = MapsActivity.FIRST_LONG_AND_LAT
    var startLongitude = MapsActivity.FIRST_LONG_AND_LAT
    var origin: MarkerOptions? = null
    var destination: MarkerOptions? = null
    var distance = 0
    var hourToDistance = 0

    fun setupMap(googleMap: GoogleMap, context: Context) {
        mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                getApplication(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MapsActivity.REQUEST_CODE_MAP
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                markerOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))

                startLatitude = location.latitude
                startLongitude = location.longitude
            }
        }
    }

    fun markerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap.addMarker(markerOptions)
    }

    fun calculateDescription() {
        val addressList: List<Address>? = null
        val markerOptions = MarkerOptions()


        if (addressList != null) {
            for (i in addressList.indices) {
                val myAddress = addressList[i]
                val latLng = LatLng(myAddress.latitude, myAddress.longitude)
                markerOptions.position(latLng)
                endLatitude = myAddress.latitude
                endLongitude = myAddress.longitude

                val result = FloatArray(10)
                Location.distanceBetween(
                    startLatitude,
                    startLongitude,
                    endLatitude,
                    endLongitude,
                    result
                )

                val roundResult = (result[0] / 1000).roundToInt()
                distance = roundResult
                hourToDistance = (roundResult * MapsActivity.HOUR_TO_KM).toInt()

                origin = MarkerOptions().position(LatLng(startLatitude, startLongitude))
                destination = MarkerOptions().position(LatLng(endLatitude, endLongitude))
                mMap!!.addMarker(markerOptions)
                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap!!.addMarker(destination!!)
                mMap!!.addMarker(origin!!)
            }
        }
    }
}