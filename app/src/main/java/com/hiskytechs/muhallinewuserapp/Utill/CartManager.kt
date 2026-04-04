package com.hiskytechs.muhallinewuserapp.Utill

import com.hiskytechs.muhallinewuserapp.Models.CartItem

object CartManager {
    private val cartItems = mutableListOf<CartItem>()
    private const val SHIPPING_COST = 25.0

    fun addItem(item: CartItem) {
        val existingItem = cartItems.find { it.id == item.id }
        if (existingItem != null) {
            existingItem.quantity += item.quantity
        } else {
            cartItems.add(item)
        }
    }

    fun getItems(): List<CartItem> = cartItems

    fun removeItem(item: CartItem) {
        cartItems.remove(item)
    }

    fun clearCart() {
        cartItems.clear()
    }
    
    fun getSubtotal(): Double = cartItems.sumOf { it.subtotal }

    fun getShipping(): Double = if (cartItems.isEmpty()) 0.0 else SHIPPING_COST

    fun getTotal(): Double = getSubtotal() + getShipping()

    fun getCartCount(): Int = cartItems.size
}
