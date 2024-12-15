package com.shop.shopstyle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.shop.shopstyle.adapters.CartAdapter
import com.shop.shopstyle.databinding.FragmentCartBinding
import com.shop.shopstyle.models.CartModel
import com.shop.shopstyle.utils.NetworkUtil
import com.shop.shopstyle.utils.showSnackbar
import com.shop.shopstyle.viewmodels.CartViewModel
import com.shop.shopstyle.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {
    private val cartViewModel: CartViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val binding by lazy {
        FragmentCartBinding.inflate(layoutInflater)
    }

    private lateinit var cartItems: List<CartModel>
    private var userAddress: String? = null // Use nullable type

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCartProduct()

        cartViewModel.totalItemCount.observe(viewLifecycleOwner, Observer { itemCount ->
            binding.txtCartTotalItem.text = "SubTotal Items($itemCount)"
        })

        cartViewModel.totalPrice.observe(viewLifecycleOwner, Observer { price ->
            binding.txtCartTotalItemPrice.text = "₹$price"
            val total = 1000 + price
            binding.txtCartTotalPrice.text = "₹$total"
        })

        profileViewModel.userDetail.observe(viewLifecycleOwner, Observer { userDetail ->
            if (userDetail != null) {
                userAddress = userDetail.address
            }
        })

        binding.btnCartCheckout.setOnClickListener {
            if (userAddress.isNullOrEmpty()) {
                view.showSnackbar("Please update your address in the profile.")
            } else {
                if (NetworkUtil.isNetworkAvailable(requireContext())) {
                    val order = addOrder(cartItems, userAddress!!)
                    if (order) {
                        view.showSnackbar("Order placed successfully. Your order will be delivered in a few days.")
                        binding.progressBarCartItems.visibility = View.VISIBLE
                        cartViewModel.load()
                        binding.progressBarCartItems.visibility = View.GONE
                    } else {
                        view.showSnackbar("Cannot place order.")
                    }
                } else {
                    view.showSnackbar("Please check your internet connection.")
                }
            }
        }
    }

    private fun initCartProduct() {
        binding.layoutCheckout.visibility = View.GONE
        binding.progressBarCartItems.visibility = View.VISIBLE
        binding.emptyCartImage.visibility = View.GONE  // Initially hide the empty cart image

        // Create the adapter once
        val cartAdapter = CartAdapter(
            onRemoveClick = { id ->
                if (NetworkUtil.isNetworkAvailable(requireContext())) {
                    val remove = removeCartItem(id)
                    if (remove) {
                        view?.showSnackbar("Cart item removed successfully.")
                    } else {
                        view?.showSnackbar("Cannot remove cart item.")
                    }
                } else {
                    view?.showSnackbar("Please check your internet connection.")
                }
            },
            onIncrementClick = { cartItem -> cartViewModel.incrementQuantity(cartItem) },
            onDecrementClick = { cartItem -> cartViewModel.decrementQuantity(cartItem) }
        )

        binding.recyclerViewCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }

        // Observe cart products
        cartViewModel.cartProduct.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                cartItems = products
                cartAdapter.submitList(products)
                binding.layoutCheckout.visibility = View.VISIBLE
                binding.progressBarCartItems.visibility = View.GONE
                binding.emptyCartImage.visibility = View.GONE
            } else {
                cartItems = emptyList()
                cartAdapter.submitList(cartItems)
                binding.layoutCheckout.visibility = View.GONE
                binding.progressBarCartItems.visibility = View.GONE
                binding.emptyCartImage.visibility = View.VISIBLE
            }
        }
    }

    private fun removeCartItem(id: String): Boolean {
        cartViewModel.removeCartItem(id)
        return true
    }

    private fun addOrder(orders: List<CartModel>, address: String): Boolean {
        cartViewModel.checkout(orders, address)
        return true
    }
}
