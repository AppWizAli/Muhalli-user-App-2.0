package com.hiskytechs.muhallinewuserapp.Ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Utill.AddressManager
import com.hiskytechs.muhallinewuserapp.Utill.CartManager
import com.hiskytechs.muhallinewuserapp.databinding.ActivityCheckoutReviewBinding
import java.util.Locale

class CheckoutReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutReviewBinding
    private var supplierName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val address = AddressManager.getAddress()
        if (address == null) {
            finish()
            return
        }

        supplierName = intent.getStringExtra(CheckoutAddressActivity.EXTRA_SUPPLIER_NAME)
            ?.takeIf { CartManager.getSupplierCart(it) != null }
            ?: CartManager.getSupplierCarts().firstOrNull()?.supplierName.orEmpty()
        val supplierCart = CartManager.getSupplierCart(supplierName)
        if (supplierCart == null) {
            finish()
            return
        }

        binding.ivBack.setOnClickListener { finish() }
        binding.tvEditAddress.setOnClickListener {
            startActivity(
                Intent(this, CheckoutAddressActivity::class.java).apply {
                    putExtra(CheckoutAddressActivity.EXTRA_SUPPLIER_NAME, supplierName)
                }
            )
        }
        binding.btnPlaceOrder.setOnClickListener {
            val createdOrder = AppData.addOrderFromCart(
                items = supplierCart.items,
                totalAmount = supplierCart.total
            )
            CartManager.clearSupplierCart(supplierName)
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
        binding.tvAddressNote.text = address.note.ifBlank { getString(R.string.no_delivery_note_added) }
        binding.tvSupplierValue.text = supplierCart.supplierName

        binding.tvItemsValue.text = getString(R.string.items_count_format, supplierCart.totalQuantity)
        binding.tvSubtotalValue.text = String.format(
            Locale.getDefault(),
            getString(R.string.currency_amount_format),
            supplierCart.subtotal
        )
        binding.tvShippingValue.text = String.format(
            Locale.getDefault(),
            getString(R.string.currency_amount_format),
            supplierCart.shipping
        )
        binding.tvTotalValue.text = String.format(
            Locale.getDefault(),
            getString(R.string.currency_amount_format),
            supplierCart.total
        )
    }
}
