package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierLoginBinding

class SupplierLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, SupplierMainActivity::class.java))
            finish()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, SupplierRegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, getString(R.string.supplier_coming_soon), Toast.LENGTH_SHORT).show()
        }
    }
}
