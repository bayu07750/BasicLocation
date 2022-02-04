package com.bayu.basiclocation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bayu.basiclocation.databinding.ActivityDialogPermissionLocationBinding

class DialogPermissionLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDialogPermissionLocationBinding

    private val perkiraanLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            if (isGranted) {
                Toast.makeText(
                    this,
                    "Akses perkiraan lokasi pengguna di izinkan",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(this, "Akses perkiraan lokasi pengguna di tolak", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val akuratLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Akses lokasi akurat pengguna di berikan", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Akses lokasi akurat pengguna di tolak", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Toast.makeText(this, "Perkiraan lokasi di berikan", Toast.LENGTH_SHORT).show()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Toast.makeText(this, "Akurat lokasi di berika", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "permission di tolak", Toast.LENGTH_SHORT).show()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogPermissionLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPerkiraanLokasi.setOnClickListener {
            if (checkSudahDiBerikan(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(
                    this,
                    "Akses perkiraan lokasi pengguna di izinkan",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                requestPermissionPerkiraanLokasi()
            }
        }

        binding.btnAkuratLokasi.setOnClickListener {
            if (checkSudahDiBerikan(Manifest.permission.ACCESS_COARSE_LOCATION) && checkSudahDiBerikan(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(this, "Akses lokasi akurat pengguna di berikan", Toast.LENGTH_SHORT)
                    .show()
            } else {
                requestPermissionPerkiraanLokasi()
                requestPermissionAkuratLokasi()
            }
        }
        binding.btnMultipleLocation.setOnClickListener {
            if (checkSudahDiBerikan(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                checkSudahDiBerikan(Manifest.permission.ACCESS_FINE_LOCATION)
            ) {
                Toast.makeText(this, "Aksses lokasi di berika", Toast.LENGTH_SHORT).show()
            } else {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }
    }

    private fun requestPermissionPerkiraanLokasi() {
        if (!checkSudahDiBerikan(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            perkiraanLocationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun requestPermissionAkuratLokasi() {
        akuratLocationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkSudahDiBerikan(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}