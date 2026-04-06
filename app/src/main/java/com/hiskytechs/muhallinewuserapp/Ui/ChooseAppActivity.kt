package com.hiskytechs.muhallinewuserapp.Ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.databinding.ActivityChooseAppBinding
import com.hiskytechs.muhallinewuserapp.supplier.Ui.ActivitySupplierOnboarding

class ChooseAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardBuyer.setOnClickListener { openBuyerApp() }
        binding.btnOpenBuyer.setOnClickListener { openBuyerApp() }
        binding.cardSupplier.setOnClickListener { openSupplierApp() }
        binding.btnOpenSupplier.setOnClickListener { openSupplierApp() }
    }

    private fun openBuyerApp() {
        startActivity(Intent(this, ActivityOnboarding::class.java))
    }

    private fun openSupplierApp() {
        startActivity(Intent(this, ActivitySupplierOnboarding::class.java))
    }
}
