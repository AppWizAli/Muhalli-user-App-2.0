package com.hiskytechs.muhallinewuserapp.Ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.MainActivity
import com.hiskytechs.muhallinewuserapp.databinding.ActivityOrderSuccessBinding

class OrderSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderId = intent.getStringExtra(EXTRA_ORDER_ID).orEmpty()
        if (orderId.isNotBlank()) {
            binding.tvOrderId.text = orderId
        }

        binding.ivBack.setOnClickListener { goHome() }
        binding.btnGoHome.setOnClickListener { goHome() }
    }

    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "home")
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_ORDER_ID = "order_id"
    }
}
