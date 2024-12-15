package com.shop.shopstyle.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shop.shopstyle.models.ProductModel
import com.shop.shopstyle.repository.FavoriteRepository
import com.shop.shopstyle.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val favRepository: FavoriteRepository
) : ViewModel() {

    val favProduct: LiveData<MutableList<ProductModel>> = favRepository.favProduct

    fun getProductById(id: Int): ProductModel? {
        return homeRepository.getProductById(id)
    }

    fun toggleFavorite(productModel: ProductModel) {
        favRepository.toggleFavorite(productModel)
    }

    fun isProductFavorite(productId: Int): Boolean {
        return favProduct.value?.any { it.id == productId } == true
    }

    fun retrieveFav() {
        favRepository.loadFavoriteItems()
    }

}