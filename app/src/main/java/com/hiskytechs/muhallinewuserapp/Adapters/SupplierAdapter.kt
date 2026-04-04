package com.hiskytechs.muhallinewuserapp.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hiskytechs.muhallinewuserapp.Models.Supplier
import com.hiskytechs.muhallinewuserapp.Ui.SupplierDetailsActivity
import com.hiskytechs.muhallinewuserapp.databinding.ItemSupplierBinding

class SupplierAdapter(private val suppliers: List<Supplier>) :
    RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder>() {

    class SupplierViewHolder(val binding: ItemSupplierBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierViewHolder {
        val binding = ItemSupplierBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SupplierViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SupplierViewHolder, position: Int) {
        val supplier = suppliers[position]
        holder.binding.apply {
            tvSupplierName.text = supplier.name
            tvProductCount.text = supplier.productCount
            tvDeliveryTime.text = supplier.deliveryTime
            tvMinAmount.text = supplier.minAmount
            tvMinQty.text = supplier.minQty
            
            // Set random background color for demo
            val colors = listOf("#E3F2FD", "#E8F5E9", "#FFF3E0", "#FCE4EC", "#F3E5F5")
            layoutHeader.setBackgroundColor(android.graphics.Color.parseColor(colors[position % colors.size]))

            root.setOnClickListener {
                val intent = Intent(it.context, SupplierDetailsActivity::class.java).apply {
                    putExtra("supplier_name", supplier.name)
                    putExtra("delivery_time", supplier.deliveryTime)
                    putExtra("min_amount", supplier.minAmount)
                    putExtra("min_qty", supplier.minQty)
                }
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = suppliers.size
}
