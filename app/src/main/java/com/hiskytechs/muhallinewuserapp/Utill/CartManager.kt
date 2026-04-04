package com.hiskytechs.muhallinewuserapp.Utill

import com.hiskytechs.muhallinewuserapp.Models.CartItem

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

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
    
    fun getCartCount(): Int = cartItems.size
}
