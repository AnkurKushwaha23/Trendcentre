package com.shop.shopstyle.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shop.shopstyle.R
import com.shop.shopstyle.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var bottomNav: BottomNavigationView
    private var currentSelectedItemId = R.id.navigation_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bottomNav = findViewById(R.id.bottom_navigation)


//        bottom navigation
        bottomNav.setOnItemSelectedListener { item ->
            resetNavigationIcons()
            when (item.itemId) {
                R.id.navigation_home -> {
                    item.setIcon(R.drawable.ic_home)
                    loadFragment(HomeFragment())
                }

                R.id.navigation_cart -> {
                    loadFragment(CartFragment())
                }

                R.id.navigation_order -> {
                    loadFragment(OrderFragment())
                }

                R.id.navigation_favorite -> {
                    item.setIcon(R.drawable.ic_favorite_filled)
                    loadFragment(FavoriteFragment())
                }

                R.id.navigation_profile -> {
                    item.setIcon(R.drawable.ic_person_filled)
                    loadFragment(ProfileFragment())
                }

                else -> return@setOnItemSelectedListener false
            }
            currentSelectedItemId = item.itemId
            true
        }

        // Load default fragment and set home icon to filled
        loadFragment(HomeFragment())
        bottomNav.selectedItemId = R.id.navigation_home
    }

    private fun resetNavigationIcons() {
        bottomNav.menu.findItem(R.id.navigation_favorite).setIcon(R.drawable.ic_favorite_border)
        bottomNav.menu.findItem(R.id.navigation_profile).setIcon(R.drawable.ic_person_outline)
        bottomNav.menu.findItem(R.id.navigation_home).setIcon(R.drawable.ic_outline_home)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun navigateToLogin() {
        startActivity(Intent(this@MainActivity, SplashActivity::class.java))
        finish()
    }

}