package com.shop.shopstyle.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shop.shopstyle.R
import com.shop.shopstyle.databinding.UserDetailBottomsheetBinding
import com.shop.shopstyle.models.UserDetailModel
import com.shop.shopstyle.utils.showSnackbar
import com.shop.shopstyle.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailBottomSheet : BottomSheetDialogFragment() {
    private val profileViewModel: ProfileViewModel by viewModels()
    private val binding by lazy {
        UserDetailBottomsheetBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe user details and update UI
        profileViewModel.userDetail.observe(viewLifecycleOwner, Observer { userDetail ->
            userDetail?.let {
                binding.apply {
                    etUserName.setText(it.name)
                    etUserMobNo.setText(it.mobNo.toString())
                    etUserAddress.setText(it.address)
                    when (it.gender) {
                        "Male" -> genderRadioGroup.check(R.id.radioMale)
                        "Female" -> genderRadioGroup.check(R.id.radioFemale)
                    }
                }
            }
        })

        binding.apply {

            // Function to update the state of the register button
            fun updateButtonState() {
                val isNameValid = etUserName.text.toString().isNotEmpty()
                val isAddressValid = etUserAddress.text.toString().isNotEmpty()
                btnUserRegister.isEnabled = isNameValid && isAddressValid
            }

            // Add TextWatcher for the name field
            etUserName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    updateButtonState()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // Add TextWatcher for the address field
            etUserAddress.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    updateButtonState()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // Initially check the button state
            updateButtonState()

            // Set up the register button click listener
            btnUserRegister.setOnClickListener {
                // Retrieve user inputs
                val name = etUserName.text.toString()
                val gender = when (genderRadioGroup.checkedRadioButtonId) {
                    R.id.radioMale -> "Male"
                    R.id.radioFemale -> "Female"
                    else -> "Any"
                }
                val mobileNumberText = etUserMobNo.text.toString()
                val mobileNumber = mobileNumberText.toLongOrNull() ?: 0L
                val address = etUserAddress.text.toString()

                // Validate mobile number: must be exactly 10 digits and numeric
                if (mobileNumberText.length != 10 || mobileNumberText.any { !it.isDigit() }) {
                    view.showSnackbar("Mobile number must be exactly 10 digits and numeric.")
                    return@setOnClickListener
                }

                // Create UserDetailModel instance
                val userDetail = UserDetailModel(
                    name = name,
                    gender = gender,
                    mobNo = mobileNumber,
                    address = address
                )

                // Try to add user details and show appropriate message
                if (addUserDetails(userDetail)) {
                    view.showSnackbar("Successfully updated user details.")
                    dismiss()  // Hide the bottom sheet
                } else {
                    view.showSnackbar("Cannot update user details.")
                }
            }
        }
    }

    private fun addUserDetails(userDetail: UserDetailModel): Boolean {
        return if (userDetail.name.isNotEmpty() && userDetail.address.isNotEmpty()) {
            profileViewModel.addUserDetail(userDetail)
            true
        } else {
            false
        }
    }
}
