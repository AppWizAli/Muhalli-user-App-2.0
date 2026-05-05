package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Ui.MapActivity
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierEditProfileBinding
import com.hiskytechs.muhallinewuserapp.supplier.Data.SupplierData
import com.hiskytechs.muhallinewuserapp.supplier.Utill.initials

class SupplierEditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierEditProfileBinding
    private var currentProfile = SupplierData.getProfile()
    private val mapPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != RESULT_OK) return@registerForActivityResult
        val data = result.data ?: return@registerForActivityResult
        val city = data.getStringExtra(MapActivity.EXTRA_SELECTED_CITY).orEmpty()
        val address = data.getStringExtra(MapActivity.EXTRA_SELECTED_ADDRESS).orEmpty()
        val latitude = data.takeIf { it.hasExtra(MapActivity.EXTRA_SELECTED_LATITUDE) }
            ?.getDoubleExtra(MapActivity.EXTRA_SELECTED_LATITUDE, 0.0)
        val longitude = data.takeIf { it.hasExtra(MapActivity.EXTRA_SELECTED_LONGITUDE) }
            ?.getDoubleExtra(MapActivity.EXTRA_SELECTED_LONGITUDE, 0.0)
        if (city.isNotBlank()) {
            binding.etCity.setText(city)
        }
        if (address.isNotBlank()) {
            binding.etAddress.setText(address)
        }
        currentProfile = currentProfile.copy(
            city = city.ifBlank { currentProfile.city },
            businessAddress = address.ifBlank { currentProfile.businessAddress },
            latitude = latitude ?: currentProfile.latitude,
            longitude = longitude ?: currentProfile.longitude
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }
        loadProfile()
        binding.btnChooseLocation.setOnClickListener {
            mapPicker.launch(
                Intent(this, MapActivity::class.java).apply {
                    putExtra(MapActivity.EXTRA_MODE, MapActivity.MODE_PICK)
                    putExtra(MapActivity.EXTRA_CITY, binding.etCity.text?.toString().orEmpty())
                    putExtra(MapActivity.EXTRA_TITLE, getString(R.string.supplier_business_address))
                }
            )
        }
        binding.btnSave.setOnClickListener {
            SupplierData.updateProfile(
                updatedProfile = currentProfile.copy(
                    businessName = binding.etBusinessName.text?.toString().orEmpty(),
                    ownerName = binding.etOwnerName.text?.toString().orEmpty(),
                    phoneNumber = binding.etPhone.text?.toString().orEmpty(),
                    emailAddress = binding.etEmail.text?.toString().orEmpty(),
                    city = binding.etCity.text?.toString().orEmpty(),
                    businessAddress = binding.etAddress.text?.toString().orEmpty(),
                    minimumOrderQuantity = binding.etMinimumOrderQuantity.text?.toString()?.toIntOrNull() ?: currentProfile.minimumOrderQuantity,
                    minimumOrderAmountPkr = binding.etMinimumOrderAmount.text?.toString()?.toIntOrNull() ?: currentProfile.minimumOrderAmountPkr
                ),
                onSuccess = {
                    Toast.makeText(this, getString(R.string.supplier_profile_updated), Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun loadProfile() {
        SupplierData.refreshProfile(
            onSuccess = {
                currentProfile = SupplierData.getProfile()
                binding.tvAvatar.text = initials(currentProfile.businessName)
                binding.etBusinessName.setText(currentProfile.businessName)
                binding.etOwnerName.setText(currentProfile.ownerName)
                binding.etPhone.setText(currentProfile.phoneNumber)
                binding.etEmail.setText(currentProfile.emailAddress)
                binding.etCity.setText(currentProfile.city)
                binding.etAddress.setText(currentProfile.businessAddress)
                binding.etMinimumOrderQuantity.setText(currentProfile.minimumOrderQuantity.toString())
                binding.etMinimumOrderAmount.setText(currentProfile.minimumOrderAmountPkr.toString())
            },
            onError = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
