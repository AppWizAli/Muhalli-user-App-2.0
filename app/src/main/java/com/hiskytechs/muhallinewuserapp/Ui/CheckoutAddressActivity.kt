package com.hiskytechs.muhallinewuserapp.Ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.Models.Address
import com.hiskytechs.muhallinewuserapp.Utill.AddressManager
import com.hiskytechs.muhallinewuserapp.databinding.ActivityCheckoutAddressBinding

class CheckoutAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutAddressBinding
    private var continueToReview: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        continueToReview = intent.getBooleanExtra(EXTRA_CONTINUE_TO_REVIEW, true)

        binding.ivBack.setOnClickListener { finish() }
        binding.btnUseSavedAddress.setOnClickListener {
            if (continueToReview) openReviewScreen() else finish()
        }
        binding.btnEditSavedAddress.setOnClickListener {
            showForm(AddressManager.getAddress())
        }
        binding.btnSaveAddress.setOnClickListener {
            saveAddressAndContinue()
        }

        if (!continueToReview) {
            binding.btnUseSavedAddress.text = "Done"
            binding.btnSaveAddress.text = "Save Address"
        }

        renderSavedAddress()
    }

    private fun renderSavedAddress() {
        val savedAddress = AddressManager.getAddress()
        if (savedAddress == null) {
            binding.cardSavedAddress.visibility = View.GONE
            showForm()
            return
        }

        binding.cardSavedAddress.visibility = View.VISIBLE
        binding.cardAddressForm.visibility = View.GONE
        binding.tvSavedName.text = savedAddress.fullName
        binding.tvSavedPhone.text = savedAddress.phoneNumber
        binding.tvSavedAddress.text = savedAddress.formattedAddress
        binding.tvSavedNote.visibility = if (savedAddress.note.isBlank()) View.GONE else View.VISIBLE
        binding.tvSavedNote.text = savedAddress.note
    }

    private fun showForm(address: Address? = null) {
        binding.cardAddressForm.visibility = View.VISIBLE
        if (address != null) {
            binding.etFullName.setText(address.fullName)
            binding.etPhoneNumber.setText(address.phoneNumber)
            binding.etStreetAddress.setText(address.streetAddress)
            binding.etCity.setText(address.city)
            binding.etAddressNote.setText(address.note)
        }
    }

    private fun saveAddressAndContinue() {
        val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
        val phoneNumber = binding.etPhoneNumber.text?.toString()?.trim().orEmpty()
        val streetAddress = binding.etStreetAddress.text?.toString()?.trim().orEmpty()
        val city = binding.etCity.text?.toString()?.trim().orEmpty()
        val note = binding.etAddressNote.text?.toString()?.trim().orEmpty()

        binding.etFullName.error = if (fullName.isBlank()) "Required" else null
        binding.etPhoneNumber.error = if (phoneNumber.isBlank()) "Required" else null
        binding.etStreetAddress.error = if (streetAddress.isBlank()) "Required" else null
        binding.etCity.error = if (city.isBlank()) "Required" else null

        if (fullName.isBlank() || phoneNumber.isBlank() || streetAddress.isBlank() || city.isBlank()) {
            return
        }

        AddressManager.saveAddress(
            Address(
                fullName = fullName,
                phoneNumber = phoneNumber,
                streetAddress = streetAddress,
                city = city,
                note = note
            )
        )
        if (continueToReview) openReviewScreen() else finish()
    }

    private fun openReviewScreen() {
        startActivity(Intent(this, CheckoutReviewActivity::class.java))
    }

    companion object {
        const val EXTRA_CONTINUE_TO_REVIEW = "continue_to_review"
    }
}
