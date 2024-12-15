package com.shop.shopstyle.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.shop.shopstyle.R
import com.shop.shopstyle.adapters.SizeAdapter
import com.shop.shopstyle.databinding.ActivityDetailBinding
import com.shop.shopstyle.models.CartModel
import com.shop.shopstyle.models.ProductModel
import com.shop.shopstyle.utils.NetworkUtil
import com.shop.shopstyle.utils.showSnackbar
import com.shop.shopstyle.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private var quantity: Int = 1
    private var shoeSize: String = ""

    private val binding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productId = intent.getIntExtra("Id", -1)

        if (productId != -1) {
            val product = homeViewModel.getProductById(productId)
            if (product != null) {
                displayProductDetails(product)
            } else {
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Product ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayProductDetails(product: ProductModel) {
        binding.apply {
            imgBackBtn.setOnClickListener { onBackClick() }
            txtProductTitle.text = product.title
            txtProductBrand.text = product.brand
            txtProductPrice.text = "₹${product.price}"
            txtProductDescription.text = product.description
            txtProductRating.text = product.rating.toString()

            // Initially disable the "Add to Cart" button
            btnAddToCart.isEnabled = false

            btnAddToCart.setOnClickListener {

                if (NetworkUtil.isNetworkAvailable(this@DetailActivity)) {
                    val isProductAddedToCart = addCart(product)
                    if (isProductAddedToCart) {
                        it.showSnackbar("Product successfully added in cart.")
                    } else {
                        it.showSnackbar("Please select the shoe size.")
                    }
                } else {
                    it.showSnackbar("Please check internet connection.")
                }

            }

            imgFavoriteBtn.setOnClickListener {
                if (NetworkUtil.isNetworkAvailable(this@DetailActivity)) {
                    homeViewModel.addFavorite(product)
                    updateFavoriteIcon(imgFavoriteBtn, product.id)
                } else {
                    it.showSnackbar("Please check internet connection.")
                }
            }
            updateFavoriteIcon(imgFavoriteBtn, product.id)
        }

        val requestOption = RequestOptions().transform(CenterInside())
        Glide.with(this)
            .load(product.picUrl)
            .apply(requestOption)
            .placeholder(R.drawable.bg_grey)
            .into(binding.imgProduct)

        // size RecyclerView
        binding.recyclerViewSize.apply {
            layoutManager =
                LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = SizeAdapter(product.size) { selectedSize ->
                shoeSize = selectedSize.toString()
                // Enable the "Add to Cart" button when a size is selected
                binding.btnAddToCart.isEnabled = shoeSize.isNotBlank()
            }
        }
    }

    private fun addCart(product: ProductModel): Boolean {
        if (shoeSize.isNotBlank()) {
            val cart = CartModel(
                product.id, product.brand,
                product.title, product.price,
                shoeSize, quantity,
                product.picUrl
            )
            homeViewModel.addCart(cart)
            return true
        }
        return false
    }

    //Todo updation here
    private fun updateFavoriteIcon(imageView: ImageView, productId: Int) {
        val isFavorite = homeViewModel.isProductFavorite(productId)
        imageView.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }

    private fun onBackClick() {
        finish()
    }
}
