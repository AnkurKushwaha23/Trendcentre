package com.shop.shopstyle.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.shop.shopstyle.R
import com.shop.shopstyle.adapters.ProductAdapter
import com.shop.shopstyle.databinding.ActivitySearchBinding
import com.shop.shopstyle.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat


@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivitySearchBinding
    lateinit var searchItemAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupSearchView()

        // Observe data from ViewModel
        homeViewModel.filteredProducts.observe(this) { items ->
            searchItemAdapter.submitList(items)
        }
    }

    private fun setupRecyclerView() {
        searchItemAdapter = ProductAdapter(
            isProductFavorite = {
                homeViewModel.isProductFavorite(it)
            },
            onFavoriteButtonClick = { product ->
                lifecycleScope.launch {
                    homeViewModel.addFavorite(product)
                    searchItemAdapter.notifyDataSetChanged()
                }
            },
            onClick = { productId ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("Id", productId)
                startActivity(intent)
            }
        )

        binding.searchRecyclerView.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            adapter = searchItemAdapter
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setupSearchView() {
        val searchView = binding.searchView

        // Access the search text view
        val searchEditText =
            searchView.findViewById<androidx.appcompat.widget.SearchView.SearchAutoComplete>(
                androidx.appcompat.R.id.search_src_text
            )
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.black)) // Set text color
        searchEditText.setHintTextColor(
            ContextCompat.getColor(
                this,
                R.color.black
            )
        )

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                homeViewModel.setSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                homeViewModel.setSearchQuery(newText)
                return true
            }
        })
    }

}
