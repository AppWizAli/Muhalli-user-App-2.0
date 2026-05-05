package com.hiskytechs.muhallinewuserapp.Ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivityAccountDetailsBinding

class AccountDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountDetailsBinding
    private var currentProfile = AppData.buyerProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        loadProfile()

        binding.btnSaveProfile.setOnClickListener {
            val buyerName = binding.etBuyerName.text?.toString()?.trim().orEmpty()
            val updatedProfile = currentProfile.copy(
                storeName = buyerName,
                buyerName = buyerName,
                email = binding.etEmail.text?.toString()?.trim().orEmpty(),
                phoneNumber = binding.etPhone.text?.toString()?.trim().orEmpty(),
                city = binding.etCity.text?.toString()?.trim().orEmpty()
            )
            AppData.updateBuyerProfile(
                updatedProfile = updatedProfile,
                onSuccess = {
                    Toast.makeText(this, getString(R.string.account_details_updated), Toast.LENGTH_SHORT)
                        .show()
                    finish()
                },
                onError = { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun loadProfile() {
        AppData.loadBuyerProfile(
            onSuccess = { profile ->
                currentProfile = profile
                binding.etBuyerName.setText(profile.buyerName)
                binding.etEmail.setText(profile.email)
                binding.etPhone.setText(profile.phoneNumber)
                binding.etCity.setText(profile.city)
            },
            onError = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
