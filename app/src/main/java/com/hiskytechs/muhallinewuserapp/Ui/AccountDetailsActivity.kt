package com.hiskytechs.muhallinewuserapp.Ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.databinding.ActivityAccountDetailsBinding

class AccountDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profile = AppData.buyerProfile
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.etStoreName.setText(profile.storeName)
        binding.etBuyerName.setText(profile.buyerName)
        binding.etEmail.setText(profile.email)
        binding.etPhone.setText(profile.phoneNumber)
        binding.etCity.setText(profile.city)

        binding.btnSaveProfile.setOnClickListener {
            profile.storeName = binding.etStoreName.text?.toString()?.trim().orEmpty()
            profile.buyerName = binding.etBuyerName.text?.toString()?.trim().orEmpty()
            profile.email = binding.etEmail.text?.toString()?.trim().orEmpty()
            profile.phoneNumber = binding.etPhone.text?.toString()?.trim().orEmpty()
            profile.city = binding.etCity.text?.toString()?.trim().orEmpty()
            Toast.makeText(this, "Account details updated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
