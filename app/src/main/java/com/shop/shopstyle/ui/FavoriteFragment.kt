package com.shop.shopstyle.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shop.shopstyle.adapters.FavoriteAdapter
import com.shop.shopstyle.databinding.FragmentFavoriteBinding
import com.shop.shopstyle.models.ProductModel
import com.shop.shopstyle.viewmodels.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() , HomeFragment.OnReloadFavFragmentListener {

    @Inject
    lateinit var firestore: FirebaseFirestore
    @Inject
    lateinit var auth: FirebaseAuth

    private val mainViewModel: FavoriteViewModel by viewModels()
    private val binding by lazy {
        FragmentFavoriteBinding.inflate(layoutInflater)
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
        mainViewModel.retrieveFav()
        initFavoriteProduct()
    }


    private fun initFavoriteProduct() {
        binding.progressBarFavItems.visibility = View.VISIBLE

        val userId = auth.currentUser?.uid

        // Setup FirestoreRecyclerOptions
        val query = userId?.let {
            firestore.collection("UserFavorite").document(it)
                .collection("FavItems")
        } // Modify this query based on your Firestore structure

        val options = FirestoreRecyclerOptions.Builder<ProductModel>()
            .setQuery(query!!, ProductModel::class.java)
            .setLifecycleOwner(viewLifecycleOwner) // Automatically manages the adapter's lifecycle
            .build()

        // Create the adapter with FirestoreRecyclerOptions
        val favProductAdapter = FavoriteAdapter(
            options = options,
            isProductFavorite = { mainViewModel.isProductFavorite(it) },
            onFavoriteButtonClick = { productId ->
                val product = mainViewModel.getProductById(productId)
                if (product != null) {
                    mainViewModel.toggleFavorite(product)
                }
            },
            onClick = { product ->
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("Id", product.id)
                startActivity(intent)
            }
        )

        binding.recyclerViewFavItems.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = favProductAdapter
        }

        // Hide progress bar when data is loaded
        favProductAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.progressBarFavItems.visibility = View.GONE
                if (favProductAdapter.itemCount == 0) {
                    binding.emptyImage.visibility = View.VISIBLE
                } else {
                    binding.emptyImage.visibility = View.GONE
                }
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                if (favProductAdapter.itemCount == 0) {
                    binding.emptyImage.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun reloadFavFragment() {
        initFavoriteProduct()
    }
}

//    private fun initFavoriteProduct() {
//        binding.progressBarFavItems.visibility = View.VISIBLE
//        // Create the adapter once
//        val favProductAdapter = FavoriteAdapter(
//            isProductFavorite = { mainViewModel.isProductFavorite(it) },
//            onFavoriteButtonClick = { productId ->
//                val product = mainViewModel.getProductById(productId)
//                if (product != null) {
//                    mainViewModel.addFavorite(product)
//                }
//            },
//            onClick = { product ->
//                val intent = Intent(context, DetailActivity::class.java)
//                intent.putExtra("Id", product.id)
//                startActivity(intent)
//            }
//        )
//
//        binding.recyclerViewFavItems.apply {
//            layoutManager = GridLayoutManager(requireContext(), 2)
//            adapter = favProductAdapter
//        }
//
//        // Observe filtered products
//        mainViewModel.favProduct.observe(viewLifecycleOwner) { products ->
//            if (products.isNotEmpty()) {
//                favProductAdapter.submitList(products)
//                binding.progressBarFavItems.visibility = View.GONE
//            } else {
//                binding.progressBarFavItems.visibility = View.GONE
//                binding.emptyImage.visibility = View.VISIBLE
//            }
//        }
//    }