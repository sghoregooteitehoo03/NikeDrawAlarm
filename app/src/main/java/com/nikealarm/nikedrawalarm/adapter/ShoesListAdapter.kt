package com.nikealarm.nikedrawalarm.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.adapter.holder.ShoesItemViewHolder
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.databinding.ItemShoesListBinding

class ShoesListAdapter(
    private val mContext: Context
) :
    PagedListAdapter<ShoesDataModel, ShoesItemViewHolder>(
        diffCallback
    ) {

    private lateinit var itemListener: ItemClickListener

    interface ItemClickListener {
        fun onClickItem(newUrl: String?)
        fun onClickImage(newUrl: String, shoesImageUrl: String, imageView: ImageView)
    }

    fun setOnItemClickListener(listener: ItemClickListener) {
        itemListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoesItemViewHolder {
        val view =
            ItemShoesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoesItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoesItemViewHolder, position: Int) {
        holder.bindView(getItem(position), itemListener)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ShoesDataModel>() {
            override fun areItemsTheSame(
                oldItem: ShoesDataModel,
                newItem: ShoesDataModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ShoesDataModel,
                newItem: ShoesDataModel
            ): Boolean =
                oldItem.shoesTitle == newItem.shoesTitle
        }
    }
}