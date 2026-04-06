package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierRegisterBinding

class SupplierRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }
        binding.tvSignIn.setOnClickListener { finish() }
        binding.btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, SupplierMainActivity::class.java))
            finishAffinity()
        }
    }
}
