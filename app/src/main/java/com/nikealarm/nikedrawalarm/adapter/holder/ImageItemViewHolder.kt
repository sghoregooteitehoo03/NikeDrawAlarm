package com.nikealarm.nikedrawalarm.adapter.holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nikealarm.nikedrawalarm.databinding.ItemImageListBinding

class ImageItemViewHolder(private val binding: ItemImageListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindView(imageUrl: String) {
        Glide.with(itemView).load(imageUrl).into(binding.shoesImage)
    }
}