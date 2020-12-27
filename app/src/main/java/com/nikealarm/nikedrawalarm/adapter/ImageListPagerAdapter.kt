package com.nikealarm.nikedrawalarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikealarm.nikedrawalarm.adapter.holder.ImageItemViewHolder
import com.nikealarm.nikedrawalarm.databinding.ItemImageListBinding

class ImageListPagerAdapter(private val imageList: Array<String>) :
    RecyclerView.Adapter<ImageItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
        val view =
            ItemImageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        holder.bindView(imageList[position])
    }
}