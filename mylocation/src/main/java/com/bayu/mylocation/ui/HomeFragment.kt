package com.bayu.mylocation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bayu.mylocation.BuildConfig
import com.bayu.mylocation.databinding.FragmentHomeBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var currentLocation: Location? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all { it.value }
        if (isGranted) {
            showSnackbar("Permission Granted", "") { }
            getLocation()
        } else {
            showSnackbar("Permission Danied", "Go Settings") {
                val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    data = uri
                }.also { startActivity(it) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        createLocationCallback()

        actions()
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                currentLocation = locationResult.lastLocation
                Log.d(
                    "uyab",
                    "onLocationResult: ${currentLocation?.latitude} <--> ${currentLocation?.longitude}"
                )
                updateUI()
            }
        }
    }

    private fun updateUI() {
        binding.tvResult.text =
            "Latitude: ${currentLocation?.latitude}\nLongitude: ${currentLocation?.longitude}"
    }

    private fun actions() {
        binding.btnUpdateLocation.setOnClickListener {
            if (!hasPermission()) {
                requestPermission()
            } else {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(requireContext())

        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    // show dialog to the user, and
                    it.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore error
                }
            }
        }
    }

    private fun requestPermission() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun hasPermission(): Boolean {
        return checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && checkPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showSnackbar(
        message: String,
        actionTitle: String = "",
        onActionListener: View.OnClickListener,
    ) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(actionTitle, onActionListener)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                Log.d("uyab", "User agreed to make required location settings changes.")
            }
            else -> {
                Log.d("uyab", "User chose not to make required location settings changes.")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 101
    }
}