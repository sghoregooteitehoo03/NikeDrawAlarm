package com.nikealarm.nikedrawalarm.adapter.holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nikealarm.nikedrawalarm.databinding.ItemExplainImageBinding

class ExplainImageViewHolder(private val binding: ItemExplainImageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindView(imageRes: Int) {

        Glide.with(itemView.context)
            .load(imageRes)
            .into(binding.itemImage)
    }
}