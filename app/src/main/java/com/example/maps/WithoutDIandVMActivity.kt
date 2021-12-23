package com.example.mapsappwithoutdi.withoutDI

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.mapsappwithoutdi.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mapsappwithoutdi.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import java.io.IOException
import kotlin.math.floor

class WithoutDIandVMActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: WithoutDIandVMActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val REQUEST_CODE_MAP = 1
        private const val HOUR_TO_KM = 0.7
        private const val FIRST_LONG_AND_LAT = 0.0
        private const val A_THOUSAND = 1000
        private const val ZERO = 0
        private const val CAMERA_ZOOM = 12f
        private const val MAX_ADDRESS_RESULT = 5
        private const val ARRAY_SIZE = 10
        private const val EMPTY_STRING = " "
    }

    var endLatitude = FIRST_LONG_AND_LAT
    var endLongitude = FIRST_LONG_AND_LAT
    var startLatitude = FIRST_LONG_AND_LAT
    var startLongitude = FIRST_LONG_AND_LAT
    var origin: MarkerOptions? = null
    var destination: MarkerOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        calculateDescription()
    }

    private fun calculateDescription() {
        binding.btnCalculate.setOnClickListener {

            binding.tvKm.visibility = View.VISIBLE
            binding.tvWithCar.visibility = View.VISIBLE
            binding.tvTime.visibility = View.VISIBLE
            binding.tvDistance.visibility = View.VISIBLE

            val target = binding.edTarget
            var addressList: List<Address>? = null
            val markerOptions = MarkerOptions()
            val location = target.text.toString()

            if (location != EMPTY_STRING) {
                val geocoder = Geocoder(applicationContext)
                try {
                    addressList = geocoder.getFromLocationName(location!!, MAX_ADDRESS_RESULT)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (addressList != null) {
                    for (i in addressList.indices) {
                        val myAddress = addressList[i]
                        val latLng = LatLng(myAddress.latitude, myAddress.longitude)
                        markerOptions.position(latLng)
                        endLatitude = myAddress.latitude
                        endLongitude = myAddress.longitude

                        val result = FloatArray(ARRAY_SIZE)
                        Location.distanceBetween(
                            startLatitude,
                            startLongitude,
                            endLatitude,
                            endLongitude,
                            result
                        )
                        val resultFirst = (result[ZERO] / A_THOUSAND)
                        val roundResult = floor((resultFirst * ARRAY_SIZE))
                        val distance = "$roundResult km "
                        val hourToDistance =
                            round(result[ZERO] / A_THOUSAND * HOUR_TO_KM).toString() + "min"

                        origin = MarkerOptions().position(LatLng(startLatitude, startLongitude))
                        destination = MarkerOptions().position(LatLng(endLatitude, endLongitude))
                        mMap.addMarker(markerOptions)
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                        mMap.addMarker(destination!!)
                        mMap.addMarker(origin!!)

                        binding.tvKm.text = distance
                        binding.tvTime.text = hourToDistance

                    }
                }
            }
        }
    }

    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_MAP
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            location?.let {
                lastLocation = location!!
                val currentLatLong = LatLng(location!!.latitude, location!!.longitude)
                markerOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, CAMERA_ZOOM))

                startLatitude = location!!.latitude
                startLongitude = location!!.longitude
            }
        }
    }

    private fun markerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap.addMarker(markerOptions)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setupMap()
    }

    override fun onMarkerClick(p0: Marker) = false

}
