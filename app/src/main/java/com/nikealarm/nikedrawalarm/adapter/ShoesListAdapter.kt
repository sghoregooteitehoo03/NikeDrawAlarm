package com.nikealarm.nikedrawalarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.nikealarm.nikedrawalarm.adapter.holder.ShoesItemViewHolder
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.databinding.ItemShoesListBinding

class ShoesListAdapter() :
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