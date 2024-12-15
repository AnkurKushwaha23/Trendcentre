package com.shop.shopstyle.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.shop.shopstyle.R
import com.shop.shopstyle.databinding.FragmentSplashBinding
import com.shop.shopstyle.utils.navigateToNextScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val binding by lazy {
        FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGetStarted.setOnClickListener {
            if (firebaseAuth.currentUser!=null){
                (activity as? SplashActivity)?.navigateNextActivity()
            }else{
                navigateToNextScreen(R.id.action_splashFragment_to_loginFragment,R.id.splashFragment)
            }

        }
    }
}