package com.nikealarm.nikedrawalarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikealarm.nikedrawalarm.adapter.holder.ExplainImageViewHolder
import com.nikealarm.nikedrawalarm.databinding.ItemExplainImageBinding

class ExplainImagePagerAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<ExplainImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExplainImageViewHolder {
        val view =
            ItemExplainImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExplainImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExplainImageViewHolder, position: Int) {
        holder.bindView(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }
}