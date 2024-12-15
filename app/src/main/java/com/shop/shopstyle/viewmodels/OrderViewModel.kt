package com.shop.shopstyle.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.shopstyle.models.ProductOrderModel
import com.shop.shopstyle.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    val orderList: LiveData<MutableList<ProductOrderModel>> = orderRepository.orderProduct

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            orderRepository.loadOrderItems()
        }
    }


}