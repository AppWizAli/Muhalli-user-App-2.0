package com.hiskytechs.muhallinewuserapp.Adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hiskytechs.muhallinewuserapp.Models.Order
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ItemOrderBinding
import java.util.Locale

class OrderAdapter(
    private var orders: List<Order>,
    private val onViewDetails: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.apply {
            tvOrderId.text = order.orderId
            tvDate.text = order.date
            tvStatus.text = order.status
            tvSupplierName.text = order.supplier
            tvItemsCount.text = "${order.itemsCount} products"
            tvTotalAmount.text = String.format(Locale.getDefault(), "$%.2f", order.totalAmount)

            // Status styling
            val context = root.context
            when (order.status.lowercase()) {
                "delivered" -> {
                    tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_delivered_bg))
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text))
                    ivStatusIcon.setImageResource(R.drawable.ic_check_circle_24)
                    ivStatusIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_delivered_text))
                }
                "in transit" -> {
                    tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_transit_bg))
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_transit_text))
                    ivStatusIcon.setImageResource(R.drawable.ic_local_shipping_24)
                    ivStatusIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_transit_text))
                }
                "processing" -> {
                    tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_processing_bg))
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_processing_text))
                    ivStatusIcon.setImageResource(R.drawable.ic_schedule_24)
                    ivStatusIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_processing_text))
                }
                "cancelled" -> {
                    tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_cancelled_bg))
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_cancelled_text))
                    ivStatusIcon.setImageResource(R.drawable.ic_delete_24)
                    ivStatusIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_cancelled_text))
                }
            }

            btnViewDetails.setOnClickListener { onViewDetails(order) }
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
