package com.nikealarm.nikedrawalarm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nikealarm.nikedrawalarm.R

class ImageListPagerAdapter(private val imageList: Array<String>) : RecyclerView.Adapter<ImageListPagerAdapter.ImageListViewHolder>() {

    inner class ImageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shoesImageView = itemView.findViewById<ImageView>(R.id.showImageFrag_imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_show_image, parent, false)
        return ImageListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        Glide.with(holder.itemView).load(imageList[position]).into(holder.shoesImageView)
    }
}