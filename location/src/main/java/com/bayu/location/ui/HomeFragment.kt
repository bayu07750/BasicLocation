package com.bayu.location.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bayu.location.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val locationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLastLocation()
            Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Permission Danied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        binding.btnLastLocation.setOnClickListener {
            if (!hasPermission()) {
                requestPermission()
            } else {
                getLastLocation()
            }
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            showDialog(
                title = "Konfirmasi!",
                message = "Untuk menggunakan layanan location kami, anda harus mengijinkan permission location\nTerima Kasih",
                onPositiveButtonClicked = {
                    locationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            )
        } else {
            locationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun showDialog(
        title: String,
        message: String,
        onPositiveButtonClicked: () -> Unit,
    ) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Lanjutkan") { dialog, _ ->
                dialog.dismiss()
                onPositiveButtonClicked()
            }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }

        val dialog = dialogBuilder.create()

        dialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val geoCoder =
                        Geocoder(requireContext()).getFromLocation(latitude, longitude, 1)
                    val address = geoCoder.first()
                    val countryName = address.countryName
                    val countryCode = address.countryCode
                    val provinsi = address.adminArea
                    val kota = address.subAdminArea
                    val kecamatan = address.locality
                    val kelurahan = address.subLocality
                    val no = address.featureName
                    val jalan = address.thoroughfare
                    val noJalan = address.subThoroughfare

                    val text = StringBuilder().apply {
                        append(countryName)
                        append(" ")
                        append("($countryCode)")
                        append("\n")
                        append(provinsi)
                        append("\n")
                        append(kota)
                        append("\n")
                        append(kecamatan)
                        append("\n")
                        append(kelurahan)
                        append("\n")
                        append(no)
                        append("\n")
                        append(jalan)
                        append("\n")
                        append(noJalan)
                    }

                    showMessage(text.toString())
                } else {
                    showDialog(
                        title = "Peringatan!",
                        message = "Tidak di temukan lokasi saat ini.\nCoba lagi nanti atau buka aplikasi Google Maps\ndan kembali lagi\nTerima Kasih!",
                        onPositiveButtonClicked = {

                        }
                    )
                }
            }
    }

    private fun showMessage(msg: String) {
        binding.tvTitlePage.text = msg
    }

    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}