package com.shop.shopstyle.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shop.shopstyle.models.UserDetailModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val _userDetail = MutableLiveData<UserDetailModel?>()
    val userDetail: MutableLiveData<UserDetailModel?> = _userDetail

    companion object {
        const val TAG = "ProfileRepository"
    }

    suspend fun loadUserDetail() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userOrderRef = firestore.collection("UserDetails").document(userId)

            try {
                val documentSnapshot = userOrderRef.get().await()
                val userDetail = documentSnapshot.toObject(UserDetailModel::class.java)
                _userDetail.value = userDetail
            } catch (e: Exception) {
                Log.w(TAG, "Error getting user details", e)
                _userDetail.value = null
            }
        } else {
            Log.w(TAG, "User is not authenticated")
            _userDetail.value = null
        }
    }

    suspend fun addUserDetail(userDetail: UserDetailModel) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userOrderRef = firestore.collection("UserDetails").document(userId)

            try {
                userOrderRef.set(userDetail).await()
                Log.d(TAG, "User added/updated successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Error adding or updating user detail", e)
            }
        } else {
            Log.w(TAG, "User is not authenticated")
        }
    }
}
