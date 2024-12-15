package com.shop.shopstyle.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shop.shopstyle.databinding.ViewholderCartBinding
import com.shop.shopstyle.models.CartModel

class CartAdapter(
    private val onRemoveClick: (documentId: String) -> Unit,
    private val onIncrementClick: (cartItem: CartModel) -> Unit,
    private val onDecrementClick: (cartItem: CartModel) -> Unit,
) : ListAdapter<CartModel, CartAdapter.CartItemViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding =
            ViewholderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartItemViewHolder(private val binding: ViewholderCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(cartItem: CartModel) {
            binding.apply {
                txtCartItemBrand.text = cartItem.brand
                txtCartItemName.text = cartItem.name
                txtCartItemName.isSelected = true
                txtCartItemPrice.text = "₹${cartItem.price}"
                txtCartItemSize.text = "Size: ${cartItem.size}"
                tvCartItemCount.text = cartItem.quantity.toString()

                Glide.with(itemView.context)
                    .load(cartItem.imageUrl)
                    .into(imgCartItem)

                // Set initial state of the minus button
                btnCartItemMinus.isEnabled = cartItem.quantity > 1

                btnCartItemAdd.setOnClickListener {
                    onIncrementClick(cartItem)
                    notifyItemChanged(adapterPosition)
                }

                btnCartItemMinus.setOnClickListener {
                    onDecrementClick(cartItem)
                    notifyItemChanged(adapterPosition)
                }

                imgBtnRemoveCartItem.setOnClickListener {
                    onRemoveClick(cartItem.documentId)
                }
            }
        }
    }
}

private class CartItemDiffCallback : DiffUtil.ItemCallback<CartModel>() {
    override fun areItemsTheSame(oldItem: CartModel, newItem: CartModel): Boolean {
        return oldItem.pid == newItem.pid
    }

    override fun areContentsTheSame(oldItem: CartModel, newItem: CartModel): Boolean {
        return oldItem == newItem
    }
}