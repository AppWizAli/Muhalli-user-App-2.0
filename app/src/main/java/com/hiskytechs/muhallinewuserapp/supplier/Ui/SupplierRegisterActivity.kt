package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Ui.AppLoadingDialog
import com.hiskytechs.muhallinewuserapp.Ui.LocationSupport
import com.hiskytechs.muhallinewuserapp.Ui.MapActivity
import com.hiskytechs.muhallinewuserapp.Ui.OtpVerificationActivity
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierRegisterBinding
import com.hiskytechs.muhallinewuserapp.network.AuthRepository
import com.hiskytechs.muhallinewuserapp.network.BackgroundWork
import org.json.JSONObject

class SupplierRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierRegisterBinding
    private lateinit var loadingDialog: AppLoadingDialog
    private var selectedAddress: String = ""
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null
    private val mapPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            val selectedCity = result.data?.getStringExtra(MapActivity.EXTRA_SELECTED_CITY).orEmpty()
            selectedAddress = result.data?.getStringExtra(MapActivity.EXTRA_SELECTED_ADDRESS).orEmpty()
            selectedLatitude = if (result.data?.hasExtra(MapActivity.EXTRA_SELECTED_LATITUDE) == true) {
                result.data?.getDoubleExtra(MapActivity.EXTRA_SELECTED_LATITUDE, 0.0)
            } else {
                null
            }
            selectedLongitude = if (result.data?.hasExtra(MapActivity.EXTRA_SELECTED_LONGITUDE) == true) {
                result.data?.getDoubleExtra(MapActivity.EXTRA_SELECTED_LONGITUDE, 0.0)
            } else {
                null
            }
            if (selectedCity.isNotBlank()) {
                binding.etCity.setText(selectedCity)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = AppLoadingDialog(this)

        binding.ivBack.setOnClickListener { finish() }
        binding.tvSignIn.setOnClickListener { finish() }
        LocationSupport.bindSuggestions(this, binding.etCity)
        binding.btnSelectCityFromMap.setOnClickListener {
            mapPickerLauncher.launch(
                Intent(this, MapActivity::class.java).apply {
                    putExtra(MapActivity.EXTRA_MODE, MapActivity.MODE_PICK)
                    putExtra(MapActivity.EXTRA_CITY, binding.etCity.text?.toString().orEmpty())
                }
            )
        }
        binding.btnCreateAccount.setOnClickListener { requestOtp() }
    }

    private fun requestOtp() {
        val businessName = binding.etBusinessName.text?.toString()?.trim().orEmpty()
        val ownerName = binding.etOwnerName.text?.toString()?.trim().orEmpty()
        val phone = binding.etPhone.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val city = binding.etCity.text?.toString()?.trim().orEmpty()

        if (businessName.isBlank() || ownerName.isBlank() || phone.length < 8 || city.isBlank()) {
            Toast.makeText(this, getString(R.string.supplier_registration_required), Toast.LENGTH_SHORT).show()
            return
        }

        val payload = mapOf(
            "business_name" to businessName,
            "owner_name" to ownerName,
            "phone" to phone,
            "email" to email,
            "city" to city,
            "address" to selectedAddress.ifBlank { city },
            "latitude" to selectedLatitude,
            "longitude" to selectedLongitude
        )

        binding.btnCreateAccount.isEnabled = false
        loadingDialog.show()
        BackgroundWork.run(
            task = {
                AuthRepository.requestOtp(
                    role = "supplier",
                    purpose = "register",
                    payload = payload
                )
            },
            onSuccess = {
                binding.btnCreateAccount.isEnabled = true
                loadingDialog.dismiss()
                openVerification(phone, payload)
            },
            onError = { message ->
                binding.btnCreateAccount.isEnabled = true
                loadingDialog.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroy() {
        loadingDialog.dismiss()
        super.onDestroy()
    }

    private fun openVerification(
        phone: String,
        payload: Map<String, Any?>
    ) {
        OtpVerificationActivity.open(
            context = this,
            role = "supplier",
            purpose = "register",
            phone = phone,
            payloadJson = JSONObject(payload).toString()
        )
    }
}
