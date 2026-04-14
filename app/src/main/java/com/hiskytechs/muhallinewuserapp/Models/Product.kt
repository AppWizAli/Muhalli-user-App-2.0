package com.hiskytechs.muhallinewuserapp.Models

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val unit: String,
    val imageResId: Int,
    val supplierName: String,
    val supplierId: Int = 0,
    val packaging: String = "",
    val stockQuantity: Int = 0,
    val deliveryTime: String = "",
    val categoryName: String = ""
)
