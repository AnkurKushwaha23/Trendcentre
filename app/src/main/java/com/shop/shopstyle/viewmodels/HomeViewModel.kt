package com.shop.shopstyle.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.shopstyle.models.BrandModel
import com.shop.shopstyle.models.CartModel
import com.shop.shopstyle.models.ProductModel
import com.shop.shopstyle.models.UserDetailModel
import com.shop.shopstyle.repository.CartRepository
import com.shop.shopstyle.repository.FavoriteRepository
import com.shop.shopstyle.repository.HomeRepository
import com.shop.shopstyle.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val favRepository: FavoriteRepository,
    private val homeRepository: HomeRepository,
    private val cartRepository: CartRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val banner: LiveData<List<String>> = homeRepository.banner
    val brand: LiveData<MutableList<BrandModel>> = homeRepository.brand

    private val _product: LiveData<MutableList<ProductModel>> = homeRepository.product
    private val _selectedBrand = MutableLiveData<String?>()
    private val _searchQuery = MutableLiveData<String?>()
    val filteredProducts = MediatorLiveData<List<ProductModel>>()

    // For favorite
    val favProduct: LiveData<MutableList<ProductModel>> = favRepository.favProduct

    fun addFavorite(productModel: ProductModel) {
        favRepository.toggleFavorite(productModel)
    }

    fun loadFav() {
        favRepository.loadFavoriteItems()
    }

    fun isProductFavorite(productId: Int): Boolean {
        return favProduct.value?.any { it.id == productId } == true
    }

    // For cart
    fun addCart(cartModel: CartModel) {
        viewModelScope.launch {
            cartRepository.addCartProduct(cartModel)
        }
    }

    init {
        load()
        setupFilteredProducts()
    }

    fun getProductById(id: Int): ProductModel? {
        return homeRepository.getProductById(id)
    }

    fun load() {
        viewModelScope.launch {
            homeRepository.loadBanner()
            homeRepository.loadBrand()
            homeRepository.loadProduct()
            favRepository.loadFavoriteItems()
            loadUserDetail()
        }
    }

    fun setSelectedBrand(brand: String?) {
        _selectedBrand.value = brand
    }

    fun setSearchQuery(query: String?) {
        _searchQuery.value = query
    }

    private fun setupFilteredProducts() {
        filteredProducts.addSource(_product) { products ->
            filterProducts(products, _selectedBrand.value, _searchQuery.value)
        }
        filteredProducts.addSource(_selectedBrand) { brand ->
            filterProducts(_product.value, brand, _searchQuery.value)
        }
        filteredProducts.addSource(_searchQuery) { query ->
            filterProducts(_product.value, _selectedBrand.value, query)
        }
    }

    private fun filterProducts(
        products: List<ProductModel>?,
        selectedBrand: String?,
        query: String?
    ) {
        viewModelScope.launch {
            var filtered = products ?: emptyList()

            if (!selectedBrand.isNullOrEmpty()) {
                filtered = filtered.filter { it.brand == selectedBrand }
            }

            if (!query.isNullOrEmpty()) {
                filtered = filtered.filter { it.title.contains(query, ignoreCase = true) }
            }

            filteredProducts.postValue(filtered)
        }
    }

    // For user data
    val userDetail: LiveData<UserDetailModel?> = profileRepository.userDetail
    private fun loadUserDetail() {
        viewModelScope.launch {
            profileRepository.loadUserDetail()
        }
    }
}
