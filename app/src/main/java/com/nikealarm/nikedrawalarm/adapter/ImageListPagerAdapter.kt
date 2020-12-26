package com.nikealarm.nikedrawalarm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.databinding.ItemImageListBinding

class ImageListPagerAdapter(private val imageList: Array<String>) :
    RecyclerView.Adapter<ImageListPagerAdapter.ImageListViewHolder>() {

    inner class ImageListViewHolder(private val binding: ItemImageListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(imageUrl: String) {
            Glide.with(itemView).load(imageUrl).into(binding.shoesImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val view =
            ItemImageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        holder.bindView(imageList[position])
    }
}