package com.shop.shopstyle.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val picUrl: String = "",
    val size: List<String> = emptyList(),
    val rating: Double = 0.0,
    val price: Double = 0.0,
    val brand: String = "",
    var isFavorite: Boolean = false
) : Parcelable
