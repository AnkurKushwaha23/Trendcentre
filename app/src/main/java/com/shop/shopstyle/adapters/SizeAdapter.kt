package com.shop.shopstyle.adapters


import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.shop.shopstyle.R
import com.shop.shopstyle.databinding.ViewholderSizeBinding

class SizeAdapter(
    private val sizeList: List<String>,
    private val onClick: (sizes: String) -> Unit
) : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val binding = ViewholderSizeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentItem = sizeList[position]
        holder.binding.txtSize.text = currentItem
        holder.binding.root.setOnClickListener {
            val oldSelectedPosition = selectedPosition
            if (selectedPosition == position) {
                // Item is already selected, so deselect it
                selectedPosition = -1
                onClick("")
            } else {
                // Select the new item
                selectedPosition = position
                onClick(currentItem)
            }
            notifyItemChanged(oldSelectedPosition)
            notifyItemChanged(selectedPosition)
        }

        updateItemAppearance(holder, position)
    }

    private fun updateItemAppearance(holder: SizeViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.binding.txtSize.setTextColor(ContextCompat.getColor(context, R.color.black))
        if (selectedPosition == position) {
            holder.binding.txtSize.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding.sizeLayout.setBackgroundResource(R.drawable.bg_blue)
            ImageViewCompat.setImageTintList(
                holder.binding.imgSize,
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            )
        } else {
            holder.binding.sizeLayout.setBackgroundResource(R.drawable.bg_border)
            ImageViewCompat.setImageTintList(
                holder.binding.imgSize,
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            )
        }
    }

    override fun getItemCount(): Int = sizeList.size

    inner class SizeViewHolder(val binding: ViewholderSizeBinding) :
        RecyclerView.ViewHolder(binding.root)
}
