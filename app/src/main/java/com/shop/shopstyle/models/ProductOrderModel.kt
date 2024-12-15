package com.shop.shopstyle.models

data class ProductOrderModel(
    val pid : Int = 0 ,
    val brand: String = "",
    val name: String = "" ,
    val price: Double = 0.0,
    val size: String = "" ,
    val quantity: Int = 1 ,
    val imageUrl: String = "" ,
    val date: String = "",
    val address: String = ""
)
