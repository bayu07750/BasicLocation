package com.bayu.basiclocation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bayu.basiclocation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTestPermissionLocation.setOnClickListener {
            Intent(this, DialogPermissionLocationActivity::class.java).also { startActivity(it) }
        }

        binding.btnLocaationMyPhone.setOnClickListener {
            Intent(this, LocationLastPhoneActivity::class.java).also { startActivity(it) }
        }
    }
}