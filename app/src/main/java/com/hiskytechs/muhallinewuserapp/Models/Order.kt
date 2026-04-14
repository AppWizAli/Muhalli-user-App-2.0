package com.hiskytechs.muhallinewuserapp.Models

data class Order(
    val internalId: Int = 0,
    val orderId: String,
    val date: String,
    val status: String,
    val supplier: String,
    val itemsCount: Int,
    val totalAmount: Double,
    val deliveryDate: String? = null
)
