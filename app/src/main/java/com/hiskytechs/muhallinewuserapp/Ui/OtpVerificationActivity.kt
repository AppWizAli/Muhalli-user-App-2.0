package com.hiskytechs.muhallinewuserapp.Ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.MainActivity
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivityOtpVerificationBinding
import com.hiskytechs.muhallinewuserapp.network.AuthRepository
import com.hiskytechs.muhallinewuserapp.network.BackgroundWork
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierMainActivity
import org.json.JSONObject

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding
    private lateinit var loadingDialog: AppLoadingDialog
    private lateinit var role: String
    private lateinit var purpose: String
    private lateinit var phone: String
    private lateinit var payloadJson: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = AppLoadingDialog(this)

        role = intent.getStringExtra(EXTRA_ROLE).orEmpty()
        purpose = intent.getStringExtra(EXTRA_PURPOSE).orEmpty()
        phone = intent.getStringExtra(EXTRA_PHONE).orEmpty()
        payloadJson = intent.getStringExtra(EXTRA_PAYLOAD_JSON).orEmpty()

        binding.tvSubtitle.text = getString(R.string.otp_verification_subtitle, phone)

        binding.ivBack.setOnClickListener { finish() }
        binding.btnVerify.setOnClickListener { verifyOtp() }
        binding.tvResendCode.setOnClickListener { resendOtp() }
    }

    private fun verifyOtp() {
        val code = binding.etOtp.text?.toString()?.trim().orEmpty()
        if (code.length < 4) {
            Toast.makeText(this, getString(R.string.otp_code_required), Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnVerify.isEnabled = false
        loadingDialog.show()
        BackgroundWork.run(
            task = { AuthRepository.verifyOtp(role = role, purpose = purpose, phone = phone, code = code) },
            onSuccess = {
                binding.btnVerify.isEnabled = true
                loadingDialog.dismiss()
                val destination = if (role == "supplier") {
                    Intent(this, SupplierMainActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }
                startActivity(destination)
                finishAffinity()
            },
            onError = { message ->
                binding.btnVerify.isEnabled = true
                loadingDialog.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun resendOtp() {
        val payload = mutableMapOf<String, Any?>()
        if (payloadJson.isNotBlank()) {
            val jsonObject = JSONObject(payloadJson)
            jsonObject.keys().forEach { key ->
                payload[key] = jsonObject.opt(key)
            }
        }

        binding.tvResendCode.isEnabled = false
        loadingDialog.show(R.string.loading_resending_code)
        BackgroundWork.run(
            task = {
                AuthRepository.requestOtp(
                    role = role,
                    purpose = purpose,
                    payload = payload
                )
            },
            onSuccess = {
                binding.tvResendCode.isEnabled = true
                loadingDialog.dismiss()
                Toast.makeText(this, getString(R.string.otp_sent_success), Toast.LENGTH_SHORT).show()
            },
            onError = { message ->
                binding.tvResendCode.isEnabled = true
                loadingDialog.dismiss()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroy() {
        loadingDialog.dismiss()
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_ROLE = "extra_role"
        private const val EXTRA_PURPOSE = "extra_purpose"
        private const val EXTRA_PHONE = "extra_phone"
        private const val EXTRA_PAYLOAD_JSON = "extra_payload_json"

        fun open(
            context: Context,
            role: String,
            purpose: String,
            phone: String,
            payloadJson: String
        ) {
            context.startActivity(
                Intent(context, OtpVerificationActivity::class.java)
                    .putExtra(EXTRA_ROLE, role)
                    .putExtra(EXTRA_PURPOSE, purpose)
                    .putExtra(EXTRA_PHONE, phone)
                    .putExtra(EXTRA_PAYLOAD_JSON, payloadJson)
            )
        }
    }
}
