package com.hiskytechs.muhallinewuserapp.Models

data class CartItem(
    val id: String,
    val name: String,
    val supplier: String,
    val price: Double,
    var quantity: Int,
    val imageUrl: String? = null
) {
    val subtotal: Double
        get() = price * quantity
}
