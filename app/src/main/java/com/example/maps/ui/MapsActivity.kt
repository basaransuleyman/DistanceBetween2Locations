package com.example.maps.ui

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.maps.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.maps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import org.koin.android.ext.android.inject
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val _viewModel by inject<ViewModel>()

    companion object {
        const val REQUEST_CODE_MAP = 1
        const val HOUR_TO_KM = 0.7
        const val FIRST_LONG_AND_LAT = 0.0
        const val EMPTY_STRING = ""
        const val ZERO = 0
        const val MAX_ADDRESS_RESULT = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        calculateDescription()
    }

    private fun calculateDescription() {
        binding.btnCalculate.setOnClickListener {

            binding.tvKm.visibility = View.VISIBLE
            binding.textView2.visibility = View.VISIBLE
            binding.tvTime.visibility = View.VISIBLE
            binding.tvDistance.visibility = View.VISIBLE

            val target = binding.edTarget
            var addressList: List<Address>? = null
            val location = target.text.toString()

            if (location != EMPTY_STRING) {
                val geocoder = Geocoder(applicationContext)
                try {
                    addressList = geocoder.getFromLocationName(location, MAX_ADDRESS_RESULT)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                _viewModel.calculateDescription()

                binding.tvKm.text = _viewModel.distance.toString()
                binding.tvTime.text = _viewModel.hourToDistance.toString()

            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        _viewModel.setupMap(mMap, this)
    }

    override fun onMarkerClick(p0: Marker) = false

}