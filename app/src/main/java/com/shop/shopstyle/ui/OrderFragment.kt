package com.shop.shopstyle.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shop.shopstyle.adapters.OrderAdapter
import com.shop.shopstyle.databinding.FragmentOrderBinding
import com.shop.shopstyle.models.ProductOrderModel
import com.shop.shopstyle.repository.OrderRepository.Companion.TAG
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderFragment : Fragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    //    private val orderViewModel: OrderViewModel by viewModels()
    private val binding by lazy {
        FragmentOrderBinding.inflate(layoutInflater)
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
        initOrderList()
    }

    private fun initOrderList() {
        binding.emptyOrderImage.visibility = View.GONE
        binding.progressBarOrderItems.visibility = View.VISIBLE

        // Get current user ID
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val query = firestore.collection("UserOrder").document(userId).collection("OrderItems")

            // Set up FirestoreRecyclerOptions
            val options = FirestoreRecyclerOptions.Builder<ProductOrderModel>()
                .setQuery(query, ProductOrderModel::class.java)
                .build()

            // Pass options to adapter
            val orderAdapter = OrderAdapter(options)

            binding.recyclerViewOrderItems.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = orderAdapter
            }

            // Start listening to Firestore updates
            orderAdapter.startListening()

            // Register adapter data observer for empty state handling
            orderAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    handleEmptyState(orderAdapter)
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    handleEmptyState(orderAdapter)
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    super.onItemRangeRemoved(positionStart, itemCount)
                    handleEmptyState(orderAdapter)
                }
            })

        } else {
            Log.w(TAG, "User is not authenticated")
            binding.progressBarOrderItems.visibility = View.GONE
            binding.emptyOrderImage.visibility = View.VISIBLE
        }
    }

    private fun handleEmptyState(orderAdapter: OrderAdapter) {
        if (orderAdapter.itemCount == 0) {
            binding.progressBarOrderItems.visibility = View.GONE
            binding.emptyOrderImage.visibility = View.VISIBLE
        } else {
            binding.progressBarOrderItems.visibility = View.GONE
            binding.emptyOrderImage.visibility = View.GONE
        }
    }


}