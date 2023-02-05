package com.bracketcove.android.dashboards.passenger

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import com.bracketcove.android.R
import com.bracketcove.android.databinding.FragmentPassengerDashboardBinding
import com.bracketcove.android.uicommon.LOCATION_PERMISSION
import com.bracketcove.android.uicommon.LOCATION_REQUEST_INTERVAL
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class PassengerDashboardFragment : Fragment(R.layout.fragment_passenger_dashboard),
    OnMapReadyCallback {

    private val viewModel by lazy { lookup<PassengerDashboardViewModel>() }


    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private var locationRequest: LocationRequest? = null
    private lateinit var locationClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPassengerDashboardBinding.bind(view)

        mapView = binding.mapLayout.mapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        locationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.isMyLocationEnabled = true
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        //This function is a great introduction to programming with the Android SDK ;)
        val locationManager = (requireActivity()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager)

        if (LocationManagerCompat.isLocationEnabled(locationManager)) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission()
            } else {
                // Create the location request to start receiving updates
                locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    LOCATION_REQUEST_INTERVAL
                ).apply {
                    //only update if user moved more than 10 meters
                    setMinUpdateDistanceMeters(10f)
                }.build()

                //determine if device settings are configured properly
                val locationSettingsRequest =  LocationSettingsRequest.Builder().apply {
                    addLocationRequest(locationRequest!!)
                }.build()

                LocationServices.getSettingsClient(requireContext())
                    .checkLocationSettings(locationSettingsRequest).addOnCompleteListener { task ->
                        if (task.isSuccessful) startRequestingLocationUpdates()
                        else {
                            Toast.makeText(
                                requireContext(),
                                R.string.system_settings_are_preventing_location_updates,
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.handleError()
                        }
                    }
            }
        } else {
            Toast.makeText(
                requireContext(),
                R.string.location_must_be_enabled,
                Toast.LENGTH_LONG
            ).show()
            viewModel.handleError()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRequestingLocationUpdates() {
        locationClient
            .lastLocation
            .addOnCompleteListener { locationRequest ->
                if (locationRequest.isSuccessful && locationRequest.result != null) {
                    val location = locationRequest.result

                    val lat = location.latitude.toFloat()
                    val lon = location.longitude.toFloat()

                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.permissions_required_to_use_this_app,
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.handleError()
                }
            }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //permission not granted
            Toast.makeText(
                requireContext(),
                R.string.permissions_required_to_use_this_app,
                Toast.LENGTH_LONG
            ).show()
            viewModel.handleError()
        } else {
            //permission granted
            requestLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                requestLocation()
        }
    }
}