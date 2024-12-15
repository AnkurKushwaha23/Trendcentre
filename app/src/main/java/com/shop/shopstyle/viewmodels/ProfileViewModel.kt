package com.shop.shopstyle.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shop.shopstyle.models.UserDetailModel
import com.shop.shopstyle.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val userDetail: LiveData<UserDetailModel?> = profileRepository.userDetail


    init {
        loadUserDetail()
    }
    // Function to load user details
    fun loadUserDetail() {
        viewModelScope.launch {
            profileRepository.loadUserDetail()
        }
    }

    // Function to add or update user details
    fun addUserDetail(userDetail: UserDetailModel) {
        viewModelScope.launch {
            profileRepository.addUserDetail(userDetail)
        }
    }
}
