package com.shop.shopstyle.models


data class CartModel(
    val pid: Int = 0,
    val brand: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val size: String = "",
    var quantity: Int = 0,
    val imageUrl: String = "",
    val documentId: String = ""
)