package com.shop.shopstyle.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shop.shopstyle.models.CartModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val _cartProduct = MutableLiveData<MutableList<CartModel>>()
    val cartProduct: LiveData<MutableList<CartModel>> = _cartProduct

    companion object {
        const val TAG = "CartRepository"
    }

    suspend fun loadCartItems() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userCartRef = firestore.collection("UserCart").document(userId)

            try {
                val querySnapshot = userCartRef.collection("CartItems").get().await()
                val cartList = mutableListOf<CartModel>()
                for (document in querySnapshot.documents) {
                    val cart = document.toObject(CartModel::class.java)
                    cart?.let { cartList.add(it) }
                }
                _cartProduct.value = cartList
            } catch (e: Exception) {
                Log.w(TAG, "Error getting cart items", e)
                _cartProduct.value = mutableListOf()
            }
        } else {
            Log.w(TAG, "User is not authenticated")
            _cartProduct.value = mutableListOf()
        }
    }

    suspend fun addCartProduct(cartItem: CartModel) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userCartRef = firestore.collection("UserCart").document(userId)
            val cartItemRef = userCartRef.collection("CartItems").document()

            try {
                val cart = CartModel(
                    cartItem.pid, cartItem.brand,
                    cartItem.name, cartItem.price,
                    cartItem.size, cartItem.quantity,
                    cartItem.imageUrl,
                    documentId = cartItemRef.id  // Assigning document ID to CartModel
                )
                cartItemRef.set(cart).await()
                Log.d(TAG, "Product added to cart")
            } catch (e: Exception) {
                Log.w(TAG, "Error adding product to cart", e)
            }
        } else {
            Log.w(TAG, "User is not authenticated")
        }
    }

//    suspend fun removeCartProduct(productId: Int) {
//        val currentUser = firebaseAuth.currentUser
//        if (currentUser != null) {
//            val userId = currentUser.uid
//            val userCartRef = firestore.collection("UserCart").document(userId)
//
//            try {
//                val querySnapshot = userCartRef.collection("CartItems")
//                    .whereEqualTo("pid", productId)
//                    .get()
//                    .await()
//                for (document in querySnapshot.documents) {
//                    userCartRef.collection("CartItems").document(document.id).delete().await()
//                }
//                Log.d(TAG, "Product removed from cart")
//                loadCartItems() // Refresh the cart items
//            } catch (e: Exception) {
//                Log.w(TAG, "Error removing product from cart", e)
//            }
//        } else {
//            Log.w(TAG, "User is not authenticated")
//        }
//    }

    suspend fun removeCartProduct(documentId: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userCartRef = firestore.collection("UserCart").document(userId)

            try {
                userCartRef.collection("CartItems").document(documentId).delete().await()
                Log.d(TAG, "Product removed from cart")
                loadCartItems() // Refresh the cart items
            } catch (e: Exception) {
                Log.w(TAG, "Error removing product from cart", e)
            }
        } else {
            Log.w(TAG, "User is not authenticated")
        }
    }

    suspend fun removeAllCartProducts() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userCartRef = firestore.collection("UserCart").document(userId)

            try {
                val querySnapshot = userCartRef.collection("CartItems").get().await()
                for (document in querySnapshot.documents) {
                    userCartRef.collection("CartItems").document(document.id).delete().await()
                }
                Log.d(TAG, "All products removed from cart")
                loadCartItems() // Refresh the cart items
            } catch (e: Exception) {
                Log.w(TAG, "Error removing products from cart", e)
            }
        } else {
            Log.w(TAG, "User is not authenticated")
        }
    }


}