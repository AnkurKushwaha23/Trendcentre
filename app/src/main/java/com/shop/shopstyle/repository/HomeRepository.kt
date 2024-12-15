package com.shop.shopstyle.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shop.shopstyle.models.BrandModel
import com.shop.shopstyle.models.ProductModel
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
) {
    private val _banner = MutableLiveData<List<String>>()
    val banner: LiveData<List<String>> = _banner

    private val _brand = MutableLiveData<MutableList<BrandModel>>()
    val brand: LiveData<MutableList<BrandModel>> = _brand

    private val _product = MutableLiveData<MutableList<ProductModel>>()
    val product: LiveData<MutableList<ProductModel>> = _product

    companion object {
        const val TAG = "HomeRepository"
    }

    fun getProductById(id: Int): ProductModel? {
        val productList = _product.value ?: return null
        return productList.find { it.id == id }
    }

    fun loadBanner() {
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val bannerList = mutableListOf<String>()

                for (bannerSnapshot in dataSnapshot.children) {
                    val url = bannerSnapshot.child("url").getValue(String::class.java)
                    url?.let { bannerList.add(it) }
                }
                _banner.postValue(bannerList)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled called: ${databaseError.message}")
            }
        })
    }

    fun loadBrand() {
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<BrandModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(BrandModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _brand.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "loadBrand onCancelled: ${error.message}")
            }
        })
    }

    fun loadProduct() {
        val ref = firebaseDatabase.getReference("Items")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ProductModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(ProductModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _product.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "loadProduct onCancelled: ${error.message}")
            }
        })
    }
}