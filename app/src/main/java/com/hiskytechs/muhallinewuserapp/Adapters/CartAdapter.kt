package com.hiskytechs.muhallinewuserapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hiskytechs.muhallinewuserapp.Models.CartItem
import com.hiskytechs.muhallinewuserapp.databinding.ItemCartBinding
import java.util.Locale

class CartAdapter(
    private var items: List<CartItem>,
    private val onQuantityChanged: (CartItem) -> Unit,
    private val onDeleteItem: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvProductName.text = item.name
            tvSupplier.text = item.supplier
            tvPrice.text = String.format(Locale.getDefault(), "$%.2f", item.price)
            tvQuantity.text = item.quantity.toString()
            tvItemSubtotal.text = String.format(Locale.getDefault(), "$%.2f", item.subtotal)

            ivPlus.setOnClickListener {
                item.quantity++
                onQuantityChanged(item)
                notifyItemChanged(position)
            }

            ivMinus.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    onQuantityChanged(item)
                    notifyItemChanged(position)
                }
            }

            ivDelete.setOnClickListener {
                onDeleteItem(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
