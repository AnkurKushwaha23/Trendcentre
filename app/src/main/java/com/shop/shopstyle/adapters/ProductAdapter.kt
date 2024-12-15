package com.shop.shopstyle.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.shop.shopstyle.R
import com.shop.shopstyle.databinding.ViewholderRecommendedItemBinding
import com.shop.shopstyle.models.ProductModel

class ProductAdapter(
    private val isProductFavorite: (Int) -> Boolean,
    private val onFavoriteButtonClick: (product: ProductModel) -> Unit,
    private val onClick: (id: Int) -> Unit
) : ListAdapter<ProductModel, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ViewholderRecommendedItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ProductViewHolder(private val binding: ViewholderRecommendedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: ProductModel) {
            binding.apply {
                val requestOption = RequestOptions().transform(CenterInside())
                Glide.with(itemView.context)
                    .load(product.picUrl)
                    .apply(requestOption)
                    .placeholder(R.drawable.bg_grey)
                    .into(imgPic)

                txtPrice.text = "₹${product.price}"
                txtRating.text = product.rating.toString()
                txtItemName.text = product.title
                txtItemName.isSelected = true
                txtItemBrand.text = product.brand

                constraintLayout.setOnClickListener {
                    onClick(product.id)
                }

                imgFavorite.setOnClickListener {
                    // Immediately update UI
                    product.isFavorite = !product.isFavorite
                    updateFavoriteIcon(imgFavorite, product.id)
                    onFavoriteButtonClick(product)
                }
                updateFavoriteIcon(imgFavorite, product.id)
            }
        }
    }

    private fun updateFavoriteIcon(imageView: ImageView, productId: Int) {
        val isFavorite = isProductFavorite(productId)
        imageView.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }
}

private class ProductDiffCallback : DiffUtil.ItemCallback<ProductModel>() {
    override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
        return oldItem == newItem
    }
}
