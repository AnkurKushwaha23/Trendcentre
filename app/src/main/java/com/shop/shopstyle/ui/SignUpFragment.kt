package com.shop.shopstyle.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.shop.shopstyle.R
import com.shop.shopstyle.databinding.FragmentSignUpBinding
import com.shop.shopstyle.utils.NetworkUtil
import com.shop.shopstyle.utils.navigateToNextScreen
import com.shop.shopstyle.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseDatabase: FirebaseDatabase

    private val binding by lazy {
        FragmentSignUpBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogin.setOnClickListener {
            navigateToNextScreen(R.id.action_signUpFragment_to_loginFragment, R.id.signUpFragment)
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.etEmailAddress.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (NetworkUtil.isNetworkAvailable(requireContext())) {
                if (validateInput(email, password, confirmPassword)) {
                    signUpUser(email, password)
                }
            } else {
                view.showSnackbar("Please check internet connection.")
            }
        }
    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                view?.showSnackbar("Please fill all Fields")
                false
            }

            password != confirmPassword -> {
                view?.showSnackbar("Password is not matching")
                false
            }

            else -> true
        }
    }

    private fun signUpUser(email: String, password: String) {
        showToast("Signing up...")

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navigateToNextScreen(
                        R.id.action_signUpFragment_to_loginFragment,
                        R.id.signUpFragment
                    )
                    Log.d("SignUp", "User created successfully")
                    view?.showSnackbar("User created successfully")
                } else {
                    Log.e("SignUp", "Sign up failed", task.exception)
                    showToast("Sign up failed: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}