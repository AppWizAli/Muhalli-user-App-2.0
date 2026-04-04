package com.hiskytechs.muhallinewuserapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hiskytechs.muhallinewuserapp.Models.CartItem
import com.hiskytechs.muhallinewuserapp.Models.Product
import com.hiskytechs.muhallinewuserapp.databinding.ItemSupplierProductBinding
import java.util.Locale

class SupplierProductAdapter(
    private val products: List<Product>,
    private val supplierName: String,
    private val onAddToCart: (CartItem) -> Unit
) : RecyclerView.Adapter<SupplierProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemSupplierProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemSupplierProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.apply {
            tvProductName.text = product.name
            tvPrice.text = String.format(Locale.getDefault(), "$%.2f", product.price)
            
            btnAddToCart.setOnClickListener {
                val cartItem = CartItem(
                    id = product.id,
                    name = product.name,
                    supplier = supplierName,
                    price = product.price,
                    quantity = 1
                )
                onAddToCart(cartItem)
            }
        }
    }

    override fun getItemCount() = products.size
}
