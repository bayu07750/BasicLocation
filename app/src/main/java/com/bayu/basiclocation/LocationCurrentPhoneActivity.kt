package com.bayu.basiclocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bayu.basiclocation.databinding.ActivityLocationCurrentPhoneBinding
import com.google.android.gms.location.FusedLocationProviderClient

class LocationCurrentPhoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationCurrentPhoneBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val listPermissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )

    private val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission Danied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationCurrentPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = FusedLocationProviderClient(this)

        binding.btnGetCurrentLocation.setOnClickListener {
            if (
                hasPermission(listPermissions[0]) &&
                hasPermission(listPermissions[1]) &&
                hasPermission(listPermissions[2]) &&
                hasPermission(listPermissions[3])
            ) {
                getLastLocation()
            } else {
                locationPermission.launch(listPermissions.toTypedArray())
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            location.let {
                val latitude = it.latitude
                val longitude = it.longitude
                val geoCoder = Geocoder(this)
                val currentAddress: Address =
                    geoCoder.getFromLocation(latitude, longitude, 1).first()
                val message =
                    "Negara: ${currentAddress.countryName}\n" +
                            "Provinsi: ${currentAddress.adminArea}\n" +
                            "Kota: ${currentAddress.subAdminArea}\n" +
                            "Kelurahan: ${currentAddress.subLocality}\n" +
                            "Jalan: ${currentAddress.thoroughfare}\n" +
                            "No Jalan: ${currentAddress.subThoroughfare}\n" +
                            "Kecamatan: ${currentAddress.locality}\n" +
                            "Url: ${currentAddress.url}\n" +
                            "premises: ${currentAddress.premises}\n" +
                            "PostalCode : ${currentAddress.postalCode}\n" +
                            "Phone : ${currentAddress.phone}\n" +
                            "maxAddressLineIndex : ${currentAddress.maxAddressLineIndex}"
                binding.tvResult.text = message
            }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}