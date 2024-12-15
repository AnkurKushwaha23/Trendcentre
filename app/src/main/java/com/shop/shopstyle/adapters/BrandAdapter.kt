package com.shop.shopstyle.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shop.shopstyle.R
import com.shop.shopstyle.databinding.ViewholderBrandBinding
import com.shop.shopstyle.models.BrandModel

class BrandAdapter(
    private val items: MutableList<BrandModel>,
    private val onClick: (brand: String?) -> Unit
) : RecyclerView.Adapter<BrandAdapter.BrandViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val binding = ViewholderBrandBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BrandViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val currentItem = items[position]
        holder.binding.txtBrandTitle.text = currentItem.title

        Glide.with(holder.itemView.context)
            .load(currentItem.picUrl)
            .into(holder.binding.imgBrand)

        holder.binding.root.setOnClickListener {
            val oldSelectedPosition = selectedPosition
            if (selectedPosition == position) {
                // Item is already selected, so deselect it
                selectedPosition = -1
                onClick(null)
            } else {
                // Select the new item
                selectedPosition = position
                onClick(currentItem.title)
            }
            notifyItemChanged(oldSelectedPosition)
            notifyItemChanged(selectedPosition)
        }

        updateItemAppearance(holder, position)
    }

    private fun updateItemAppearance(holder: BrandViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.binding.txtBrandTitle.setTextColor(context.resources.getColor(R.color.white))
        if (selectedPosition == position) {
            holder.binding.imgBrand.setBackgroundResource(0)
            holder.binding.brandLayout.setBackgroundResource(R.drawable.bg_blue)
            ImageViewCompat.setImageTintList(
                holder.binding.imgBrand,
                ColorStateList.valueOf(context.getColor(R.color.white))
            )
            holder.binding.txtBrandTitle.visibility = View.VISIBLE
        } else {
            holder.binding.imgBrand.setBackgroundResource(R.drawable.bg_grey)
            holder.binding.brandLayout.setBackgroundResource(R.drawable.bg_grey)
            ImageViewCompat.setImageTintList(
                holder.binding.imgBrand,
                ColorStateList.valueOf(context.getColor(R.color.black))
            )
            holder.binding.txtBrandTitle.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size

    inner class BrandViewHolder(val binding: ViewholderBrandBinding) :
        RecyclerView.ViewHolder(binding.root)
}