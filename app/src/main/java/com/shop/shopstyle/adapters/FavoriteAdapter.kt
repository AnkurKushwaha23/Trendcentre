package com.shop.shopstyle.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.shop.shopstyle.R
import com.shop.shopstyle.databinding.ViewholderRecommendedItemBinding
import com.shop.shopstyle.models.ProductModel

class FavoriteAdapter(
    options: FirestoreRecyclerOptions<ProductModel>,
    private val isProductFavorite: (Int) -> Boolean,
    private val onFavoriteButtonClick: (id: Int) -> Unit,
    private val onClick: (product: ProductModel) -> Unit
) : FirestoreRecyclerAdapter<ProductModel, FavoriteAdapter.FavProductViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavProductViewHolder {
        val binding = ViewholderRecommendedItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return FavProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavProductViewHolder, position: Int, model: ProductModel) {
        holder.bind(model)
    }

    inner class FavProductViewHolder(private val binding: ViewholderRecommendedItemBinding) :
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
                    onClick(product)
                }

                imgFavorite.setOnClickListener {
                    onFavoriteButtonClick(product.id)
                    imgFavorite.setImageResource(
                        R.drawable.ic_favorite_filled
                    )
                    notifyItemChanged(adapterPosition)
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
