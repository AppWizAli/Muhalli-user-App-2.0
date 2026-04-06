package com.hiskytechs.muhallinewuserapp.Models

data class Supplier(
    val name: String,
    val location: String,
    val productCount: String,
    val deliveryTime: String,
    val minimumAmount: Double,
    val minimumQuantity: Int,
    val categories: List<String>,
    val isVerified: Boolean = true,
    val headerColor: String = "#EAF2FF"
)
