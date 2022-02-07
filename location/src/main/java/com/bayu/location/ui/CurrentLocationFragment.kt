package com.bayu.location.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bayu.location.BuildConfig
import com.bayu.location.databinding.FragmentCurrentLocationBinding
import com.bayu.location.extension.hasPermission
import com.bayu.location.extension.shouldShowRationale
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar

class CurrentLocationFragment : Fragment() {

    private var _binding: FragmentCurrentLocationBinding? = null
    private val binding get() = _binding!!

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private var cancellationTokenSource = CancellationTokenSource()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Snackbar.make(
                binding.root,
                "Permission Granted",
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            Snackbar.make(
                binding.root,
                "Permission Danied",
                Snackbar.LENGTH_SHORT
            )
                .setAction("Go Setting") {
                    val uri = Uri.fromParts(
                        "package",
                        BuildConfig.APPLICATION_ID,
                        null
                    )
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = uri
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                }
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentLocationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLocation.setOnClickListener {
            if (!requireContext().hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                getCurrentLocation()
            }
        }
    }

    private fun requestPermission(permission: String) {
        val launchPermission = {
            permissionLauncher.launch(permission)
        }
        if (requireActivity().shouldShowRationale(permission)) {
            Snackbar.make(
                binding.root,
                "Kami butuh lokasi anda untuk menggunakan layanan ini",
                Snackbar.LENGTH_SHORT
            )
                .setAction("Iya") {
                    launchPermission()
                }
                .show()
        } else {
            launchPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val currentLocationTask = fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )

        currentLocationTask.addOnCompleteListener {
            val result = if (it.isSuccessful && it.result != null) {
                val location = it.result
                val geocoder = Geocoder(requireContext()).getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                val address = geocoder.first()
                "Berhasil mendapatkan Lokasi: ${address.countryName}/${address.adminArea}/${address.subAdminArea}/${address.locality}/${address.subLocality}/${address.thoroughfare}/${address.subThoroughfare}"
            } else {
                "Gagal mendapatkan Lokasi: ${it.exception?.message}"
            }

            binding.tvTitlePage.text = result
        }
    }

    override fun onStop() {
        super.onStop()
        cancellationTokenSource.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}