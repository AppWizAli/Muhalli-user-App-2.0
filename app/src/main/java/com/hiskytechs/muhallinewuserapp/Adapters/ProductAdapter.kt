package com.hiskytechs.muhallinewuserapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hiskytechs.muhallinewuserapp.Models.Product
import com.hiskytechs.muhallinewuserapp.databinding.ItemProductBinding

class ProductAdapter(private val products: List<Product>, private val onAddClick: (Product) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
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
            tvProductUnit.text = product.unit
            tvProductPrice.text = product.price
            ivProduct.setImageResource(product.imageResId)
            
            btnAddToCart.setOnClickListener {
                onAddClick(product)
            }
        }
    }

    override fun getItemCount() = products.size
}
