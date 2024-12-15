package com.shop.shopstyle.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shop.shopstyle.models.ProductOrderModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val _orderProduct = MutableLiveData<MutableList<ProductOrderModel>>()
    val orderProduct: LiveData<MutableList<ProductOrderModel>> = _orderProduct

    companion object {
        const val TAG = "OrderRepository"
    }

    suspend fun loadOrderItems() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userOrderRef = firestore.collection("UserOrder").document(userId)

            try {
                val querySnapshot = userOrderRef.collection("OrderItems").get().await()
                val orderList = mutableListOf<ProductOrderModel>()
                for (document in querySnapshot.documents) {
                    val order = document.toObject(ProductOrderModel::class.java)
                    order?.let { orderList.add(it) }
                }
                _orderProduct.value = orderList
            } catch (e: Exception) {
                Log.w(TAG, "Error getting order items", e)
                _orderProduct.value = mutableListOf()
            }
        } else {
            Log.w(TAG, "User is not authenticated")
            _orderProduct.value = mutableListOf()
        }
    }

    suspend fun addOrderProducts(orderItems: List<ProductOrderModel>) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userOrderRef = firestore.collection("UserOrder").document(userId)

            try {
                for (orderItem in orderItems) {
                    val orderItemRef = userOrderRef.collection("OrderItems").document()
                    orderItemRef.set(orderItem).await()
                }
                Log.d(TAG, "All products added to Order")
            } catch (e: Exception) {
                Log.w(TAG, "Error adding products to Order", e)
            }
        } else {
            Log.w(TAG, "User is not authenticated")
        }
    }
}
