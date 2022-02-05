package com.bayu.basiclocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bayu.basiclocation.databinding.ActivityLocationPhoneBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationPhoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationPhoneBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val loationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission di berikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission di tolak", Toast.LENGTH_SHORT).show()
            }
        }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "My Location"

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnGetLastLocationMyPhone.setOnClickListener {
            if (hasPermissionLocation()) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val geoCoder = Geocoder(this)
                        val currentLocation = geoCoder.getFromLocation(
                            latitude,
                            longitude,
                            1
                        )
                        with(currentLocation.first()) {
                            binding.tvResult.text = "$countryName $subLocality"
                        }
                    }
                }
            } else {
                shouldShowDialogRationale()
            }
        }
    }

    private fun hasPermissionLocation(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowDialogRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            showDialog()
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        loationPermission.launch(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Perhatian!")
            .setMessage("Aplikasi ini memerlukan akes lokasi anda.\nLokasi ini kami gunakan untuk keperluan layanana seperti GoFood\nTerimakasih!")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                requestLocationPermission()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}
