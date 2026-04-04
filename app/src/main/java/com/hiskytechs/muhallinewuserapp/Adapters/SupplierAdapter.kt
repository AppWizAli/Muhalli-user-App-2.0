package com.hiskytechs.muhallinewuserapp.Adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hiskytechs.muhallinewuserapp.Models.Supplier
import com.hiskytechs.muhallinewuserapp.Ui.SupplierDetailsActivity
import com.hiskytechs.muhallinewuserapp.databinding.ItemSupplierBinding

class SupplierAdapter(
    private val suppliers: List<Supplier>,
    private val highlightedCategory: String? = null
) :
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
            tvLocation.text = supplier.location
            tvProductCount.text = supplier.productCount
            tvDeliveryTime.text = supplier.deliveryTime
            tvMinAmount.text = supplier.minAmount
            tvMinQty.text = supplier.minQty
            tvVerified.visibility = if (supplier.isVerified) View.VISIBLE else View.GONE

            val orderedCategories = supplier.categories.sortedByDescending { category ->
                category.equals(highlightedCategory, ignoreCase = true)
            }
            val primaryCategory = orderedCategories.getOrNull(0)
            val secondaryCategory = orderedCategories.getOrNull(1)

            bindCategoryChip(tvCategoryPrimary, primaryCategory)
            bindCategoryChip(tvCategorySecondary, secondaryCategory)
            layoutHeader.setBackgroundColor(Color.parseColor(supplier.headerColor))

            root.setOnClickListener {
                val intent = Intent(it.context, SupplierDetailsActivity::class.java).apply {
                    putExtra("supplier_name", supplier.name)
                    putExtra("location", supplier.location)
                    putExtra("delivery_time", supplier.deliveryTime)
                    putExtra("min_amount", supplier.minAmount)
                    putExtra("min_qty", supplier.minQty)
                }
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = suppliers.size

    private fun bindCategoryChip(view: android.widget.TextView, category: String?) {
        if (category.isNullOrBlank()) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            view.text = category
        }
    }
}
