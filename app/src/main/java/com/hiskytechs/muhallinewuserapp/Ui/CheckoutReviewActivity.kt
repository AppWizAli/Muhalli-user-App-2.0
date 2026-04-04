package com.hiskytechs.muhallinewuserapp.Ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.Utill.AddressManager
import com.hiskytechs.muhallinewuserapp.Utill.CartManager
import com.hiskytechs.muhallinewuserapp.databinding.ActivityCheckoutReviewBinding
import java.util.Locale

class CheckoutReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val address = AddressManager.getAddress()
        if (address == null) {
            finish()
            return
        }

        binding.ivBack.setOnClickListener { finish() }
        binding.tvEditAddress.setOnClickListener {
            startActivity(Intent(this, CheckoutAddressActivity::class.java))
        }
        binding.btnPlaceOrder.setOnClickListener {
            val createdOrder = AppData.addOrderFromCart(
                items = CartManager.getItems(),
                totalAmount = CartManager.getTotal()
            )
            CartManager.clearCart()
            val intent = Intent(this, OrderSuccessActivity::class.java).apply {
                putExtra(OrderSuccessActivity.EXTRA_ORDER_ID, createdOrder.orderId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }

        binding.tvAddressName.text = address.fullName
        binding.tvAddressPhone.text = address.phoneNumber
        binding.tvAddressLine.text = address.formattedAddress
        binding.tvAddressNote.text = address.note.ifBlank { "No delivery note added" }

        binding.tvItemsValue.text = "${CartManager.getCartCount()} items"
        binding.tvSubtotalValue.text = String.format(Locale.getDefault(), "$%.2f", CartManager.getSubtotal())
        binding.tvShippingValue.text = String.format(Locale.getDefault(), "$%.2f", CartManager.getShipping())
        binding.tvTotalValue.text = String.format(Locale.getDefault(), "$%.2f", CartManager.getTotal())
    }
}
