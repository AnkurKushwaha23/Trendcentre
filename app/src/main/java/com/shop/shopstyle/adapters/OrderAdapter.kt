package com.shop.shopstyle.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.shop.shopstyle.databinding.ViewholderOrderBinding
import com.shop.shopstyle.models.ProductOrderModel

class OrderAdapter(
    options: FirestoreRecyclerOptions<ProductOrderModel>
) : FirestoreRecyclerAdapter<ProductOrderModel, OrderAdapter.ProductOrderViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductOrderViewHolder {
        val binding = ViewholderOrderBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductOrderViewHolder, position: Int, model: ProductOrderModel) {
        holder.bind(model)
    }

    inner class ProductOrderViewHolder(private val binding: ViewholderOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(orderModel: ProductOrderModel) {
            binding.apply {
                txtOrderItemBrand.text = orderModel.brand
                txtOrderItemName.text = orderModel.name
                txtOrderItemName.isSelected = true
                txtOrderItemPrice.text = "₹${orderModel.price}"
                txtOrderItemSize.text = "Size: ${orderModel.size}"
                txtOrderItemQuantity.text = "Quantity: ${orderModel.quantity}"
                txtOrderItemDate.text = "Order Date: ${orderModel.date}"
                txtOrderItemAddress.text = "Address: ${orderModel.address}"

                Glide.with(itemView.context)
                    .load(orderModel.imageUrl)
                    .into(imgOrderItem)
            }
        }
    }
}
