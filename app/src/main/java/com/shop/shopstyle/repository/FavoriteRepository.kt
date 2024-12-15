package com.shop.shopstyle.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shop.shopstyle.models.ProductModel
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val _favProduct = MutableLiveData<MutableList<ProductModel>>()
    val favProduct: LiveData<MutableList<ProductModel>> = _favProduct

    companion object {
        const val TAG = "com.shop.shopstyle.repository.FavoriteRepository"
    }

    fun loadFavoriteItems() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userFavoriteRef = firestore.collection("UserFavorite").document(userId)

            userFavoriteRef.collection("FavItems").get()
                .addOnSuccessListener { querySnapshot ->
                    val favoriteList = mutableListOf<ProductModel>()
                    for (document in querySnapshot.documents) {
                        val favorite = document.toObject(ProductModel::class.java)
                        favorite?.let { favoriteList.add(it) }
                    }
                    _favProduct.value = favoriteList
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error getting favorite items", e)
                    _favProduct.value = mutableListOf()
                }
        } else {
            Log.w(TAG, "User is not authenticated")
            _favProduct.value = mutableListOf()
        }
    }

    fun toggleFavorite(product: ProductModel) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userFavoriteRef = firestore.collection("UserFavorite").document(userId)
            val favItemRef = userFavoriteRef.collection("FavItems").document(product.id.toString())

            favItemRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Product is already a favorite, remove it
                        favItemRef.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Product removed from favorites")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error removing product from favorites", e)
                            }
                    } else {
                        val favorite = ProductModel(
                            id = product.id,
                            title = product.title,
                            description = product.description,
                            picUrl = product.picUrl,
                            size = product.size,
                            rating = product.rating,
                            price = product.price,
                            brand = product.brand,
                            isFavorite = true
                        )
                        favItemRef.set(favorite)
                            .addOnSuccessListener {
                                Log.d(TAG, "Product added to favorites")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding product to favorites", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error checking favorite status", e)
                }
        } else {
            Log.w(TAG, "User is not authenticated")
        }
    }
} 