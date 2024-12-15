package com.shop.shopstyle.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.shop.shopstyle.adapters.BrandAdapter
import com.shop.shopstyle.adapters.ProductAdapter
import com.shop.shopstyle.databinding.FragmentHomeBinding
import com.shop.shopstyle.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment() : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    lateinit var productAdapter: ProductAdapter
    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var firebaseDatabase: FirebaseDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBanner()
        initBrand()
        initProduct()
        initUser()

        binding.imgSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        binding.btnEditUser.setOnClickListener {
            val bottomSheet = UserDetailBottomSheet()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

    }

    private fun initUser() {
        binding.txtUserGreet.visibility = View.GONE
        binding.btnEditUser.visibility = View.GONE
        homeViewModel.userDetail.observe(viewLifecycleOwner, Observer { userData ->
            if (userData != null) {
                binding.btnEditUser.visibility = View.GONE
                binding.txtUserGreet.visibility = View.VISIBLE
                binding.txtUserGreet.text = "Welcome Back,\n${userData?.name}"
            } else {
                binding.txtUserGreet.visibility = View.GONE
                binding.btnEditUser.visibility = View.VISIBLE
            }
        })
    }

    private fun initProduct() {
        binding.progressBarItems.visibility = View.VISIBLE

        // Create the adapter once
        productAdapter = ProductAdapter(
            isProductFavorite = {
                homeViewModel.isProductFavorite(it)
            },
            onFavoriteButtonClick = { product ->
                lifecycleScope.launch {
                    binding.progressBarItems.visibility = View.VISIBLE
                    homeViewModel.addFavorite(product)
                    (activity as? OnReloadFavFragmentListener)?.reloadFavFragment()
                    productAdapter.notifyDataSetChanged()
                    binding.progressBarItems.visibility = View.GONE
                }
            },
            onClick = { productId ->
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("Id", productId)
                startActivity(intent)
            }
        )

        binding.recyclerViewItems.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }

        // Observe filtered products
        homeViewModel.filteredProducts.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
            binding.progressBarItems.visibility = View.GONE
        }
    }

    private fun initBrand() {
        binding.progressBar2.visibility = View.VISIBLE
        homeViewModel.brand.observe(requireActivity(), Observer {
            binding.recyclerViewBrands.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = BrandAdapter(it) { selectedBrand ->
                    homeViewModel.setSelectedBrand(selectedBrand)
                }
                binding.progressBar2.visibility = View.GONE
            }
        })
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        homeViewModel.banner.observe(viewLifecycleOwner, Observer { items ->
            viewLifecycleOwner.lifecycleScope.launch {
                // Clear existing views
                binding.viewFlipper.removeAllViews()

                // Add new views
                for (url in items) {
                    val imageView = ImageView(requireContext()).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    Glide.with(this@HomeFragment)
                        .load(url)
                        .into(imageView)
                    binding.viewFlipper.addView(imageView)
                }

                binding.viewFlipper.flipInterval = 3000 // 3 seconds
            }

            binding.progressBarBanner.visibility = View.GONE
        })
    }

    interface OnReloadFavFragmentListener {
        fun reloadFavFragment()
    }

}