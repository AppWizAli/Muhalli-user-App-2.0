package com.hiskytechs.muhallinewuserapp.Models

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val unit: String,
    val imageResId: Int,
    val supplierName: String
)
