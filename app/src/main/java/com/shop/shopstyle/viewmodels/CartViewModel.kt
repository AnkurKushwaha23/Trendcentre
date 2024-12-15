package com.shop.shopstyle.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.shopstyle.models.CartModel
import com.shop.shopstyle.models.ProductOrderModel
import com.shop.shopstyle.repository.CartRepository
import com.shop.shopstyle.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {
    val cartProduct: LiveData<MutableList<CartModel>> = cartRepository.cartProduct

    val totalItemCount = MutableLiveData<Int>()
    val totalPrice = MutableLiveData<Double>()

    companion object {
        const val TAG = "CartViewModel"
    }

    fun checkout(cartItems: List<CartModel>, address: String) {
        viewModelScope.launch {

            try {
                val orderItems = cartItems.map { cartItem ->
                    ProductOrderModel(
                        pid = cartItem.pid,
                        brand = cartItem.brand,
                        name = cartItem.name,
                        price = cartItem.price * cartItem.quantity + 1000,
                        size = cartItem.size,
                        quantity = cartItem.quantity,
                        imageUrl = cartItem.imageUrl,
                        date = getCurrentDate(),
                        address = address
                    )
                }
                orderRepository.addOrderProducts(orderItems)
                cartRepository.removeAllCartProducts()
                Log.d(TAG, "Checkout successful")
            } catch (e: Exception) {
                Log.w(TAG, "Checkout failed", e)
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // Cart
    init {
        load()
    }

    fun removeCartItem(id: String) {
        viewModelScope.launch {
            cartRepository.removeCartProduct(id)
            getAllCartItemCountAndPrice() // Update total values after removing an item
        }
    }

    private fun getAllCartItemCountAndPrice() {
        cartProduct.observeForever { cartList ->
            updateTotalValues(cartList)
        }
    }

    private fun updateTotalValues(cartList: List<CartModel>) {
        val itemCount = cartList.sumOf { it.quantity }
        val price = cartList.sumOf { it.price * it.quantity }

        totalItemCount.value = itemCount
        totalPrice.value = price
    }

    fun incrementQuantity(cartItem: CartModel) {
        cartItem.quantity++
        updateTotalValues(cartProduct.value ?: emptyList())
    }

    fun decrementQuantity(cartItem: CartModel) {
        if (cartItem.quantity > 1) {
            cartItem.quantity--
            updateTotalValues(cartProduct.value ?: emptyList())
        }
    }


    fun load() {
        viewModelScope.launch {
            cartRepository.loadCartItems()
            getAllCartItemCountAndPrice()
        }
    }

}