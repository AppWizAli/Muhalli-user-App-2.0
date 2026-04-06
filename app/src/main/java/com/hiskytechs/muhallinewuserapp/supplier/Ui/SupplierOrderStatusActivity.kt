package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierOrderStatusBinding
import com.hiskytechs.muhallinewuserapp.supplier.Data.SupplierData
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierOrder
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierOrderStatus
import com.hiskytechs.muhallinewuserapp.supplier.Utill.formatPkr
import com.hiskytechs.muhallinewuserapp.supplier.Utill.orderStatusBackground
import com.hiskytechs.muhallinewuserapp.supplier.Utill.orderStatusTextColor

class SupplierOrderStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierOrderStatusBinding
    private lateinit var order: SupplierOrder
    private lateinit var statusOptions: List<StatusOptionView>
    private var selectedStatus: SupplierOrderStatus = SupplierOrderStatus.PENDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierOrderStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderId = intent.getStringExtra(EXTRA_ORDER_ID).orEmpty()
        val supplierOrder = SupplierData.findOrder(orderId)
        if (supplierOrder == null) {
            finish()
            return
        }

        order = supplierOrder
        selectedStatus = order.status
        statusOptions = listOf(
            StatusOptionView(
                SupplierOrderStatus.PENDING,
                binding.layoutStatusPending,
                binding.tvStatusPendingTitle,
                binding.tvStatusPendingSubtitle
            ),
            StatusOptionView(
                SupplierOrderStatus.CONFIRMED,
                binding.layoutStatusConfirmed,
                binding.tvStatusConfirmedTitle,
                binding.tvStatusConfirmedSubtitle
            ),
            StatusOptionView(
                SupplierOrderStatus.SHIPPED,
                binding.layoutStatusShipped,
                binding.tvStatusShippedTitle,
                binding.tvStatusShippedSubtitle
            ),
            StatusOptionView(
                SupplierOrderStatus.DELIVERED,
                binding.layoutStatusDelivered,
                binding.tvStatusDeliveredTitle,
                binding.tvStatusDeliveredSubtitle
            )
        )

        binding.ivBack.setOnClickListener { finish() }
        binding.btnSaveStatus.setOnClickListener { saveStatus() }

        statusOptions.forEach { option ->
            option.container.setOnClickListener {
                selectedStatus = option.status
                renderStatusSelection()
            }
        }

        bindOrder()
        renderStatusSelection()
    }

    private fun bindOrder() {
        binding.tvOrderId.text = "#${order.id}"
        binding.tvRetailerName.text = order.retailerName
        binding.tvOrderDateValue.text = order.orderDate
        binding.tvExpectedDeliveryValue.text = order.expectedDeliveryDate
        binding.tvItemsCountValue.text = getString(R.string.supplier_items_count_format, order.itemsCount)
        binding.tvOrderAmountValue.text = formatPkr(order.amountPkr)
        binding.tvCurrentStatusValue.text = order.status.label
        binding.tvCurrentStatusValue.setBackgroundResource(orderStatusBackground(order.status))
        binding.tvCurrentStatusValue.setTextColor(
            ContextCompat.getColor(this, orderStatusTextColor(order.status))
        )
    }

    private fun renderStatusSelection() {
        statusOptions.forEach { option ->
            val isSelected = option.status == selectedStatus
            option.container.setBackgroundResource(
                if (isSelected) R.drawable.bg_supplier_card_selected else R.drawable.bg_supplier_card
            )
            option.title.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (isSelected) R.color.primary else R.color.text_dark
                )
            )
            option.subtitle.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (isSelected) R.color.primary else R.color.supplier_text_secondary
                )
            )
        }
    }

    private fun saveStatus() {
        SupplierData.updateOrderStatus(order.id, selectedStatus)
        Toast.makeText(this, getString(R.string.supplier_order_status_updated), Toast.LENGTH_SHORT).show()
        finish()
    }

    private data class StatusOptionView(
        val status: SupplierOrderStatus,
        val container: LinearLayout,
        val title: TextView,
        val subtitle: TextView
    )

    companion object {
        private const val EXTRA_ORDER_ID = "extra_order_id"

        fun open(context: Context, orderId: String) {
            context.startActivity(
                Intent(context, SupplierOrderStatusActivity::class.java)
                    .putExtra(EXTRA_ORDER_ID, orderId)
            )
        }
    }
}
