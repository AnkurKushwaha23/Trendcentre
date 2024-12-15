package com.shop.shopstyle.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.shop.shopstyle.R
import com.shop.shopstyle.databinding.FragmentLoginBinding
import com.shop.shopstyle.utils.NetworkUtil
import com.shop.shopstyle.utils.navigateToNextScreen
import com.shop.shopstyle.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val binding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSignUp.setOnClickListener {
            navigateToNextScreen(R.id.action_loginFragment_to_signUpFragment, R.id.loginFragment)
        }

        binding.btnLogin.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (NetworkUtil.isNetworkAvailable(requireContext())) {
                    logIn()
                } else {
                    view.showSnackbar("Please check internet connection.")
                }
            }
        }
    }

    private fun logIn() {
        val email = binding.etEmailAddress.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        (activity as? SplashActivity)?.navigateNextActivity()
                        view?.showSnackbar("LogIn Successfully !!")
                    } else {
                        if (task.exception is FirebaseAuthInvalidUserException || task.exception is FirebaseAuthInvalidCredentialsException) {
                            view?.showSnackbar("Invalid email or password")
                        } else {
                            Log.d("LoginError", task.exception.toString())
                        }
                    }
                }
        } else {
            view?.showSnackbar("Please fill all Fields")
        }
    }
}