package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierEditProfileBinding
import com.hiskytechs.muhallinewuserapp.supplier.Data.SupplierData
import com.hiskytechs.muhallinewuserapp.supplier.Utill.initials

class SupplierEditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profile = SupplierData.getProfile()
        binding.tvAvatar.text = initials(profile.businessName)
        binding.etBusinessName.setText(profile.businessName)
        binding.etOwnerName.setText(profile.ownerName)
        binding.etPhone.setText(profile.phoneNumber)
        binding.etEmail.setText(profile.emailAddress)
        binding.etCity.setText(profile.city)
        binding.etAddress.setText(profile.businessAddress)
        binding.etMinimumOrderQuantity.setText(profile.minimumOrderQuantity.toString())
        binding.etMinimumOrderAmount.setText(profile.minimumOrderAmountPkr.toString())

        binding.ivBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener {
            SupplierData.updateProfile(
                profile.copy(
                    businessName = binding.etBusinessName.text?.toString().orEmpty(),
                    ownerName = binding.etOwnerName.text?.toString().orEmpty(),
                    phoneNumber = binding.etPhone.text?.toString().orEmpty(),
                    emailAddress = binding.etEmail.text?.toString().orEmpty(),
                    city = binding.etCity.text?.toString().orEmpty(),
                    businessAddress = binding.etAddress.text?.toString().orEmpty(),
                    minimumOrderQuantity = binding.etMinimumOrderQuantity.text?.toString()?.toIntOrNull() ?: profile.minimumOrderQuantity,
                    minimumOrderAmountPkr = binding.etMinimumOrderAmount.text?.toString()?.toIntOrNull() ?: profile.minimumOrderAmountPkr
                )
            )
            Toast.makeText(this, getString(R.string.supplier_profile_updated), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
