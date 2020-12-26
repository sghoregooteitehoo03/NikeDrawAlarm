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
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.databinding.ItemShoesListBinding

class ShoesListAdapter(
    private val mContext: Context
) :
    PagedListAdapter<ShoesDataModel, ShoesListAdapter.DrawListViewHolder>(
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawListViewHolder {
        val view =
            ItemShoesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrawListViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrawListViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class DrawListViewHolder(private val binding: ItemShoesListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(data: ShoesDataModel?) {
            with(binding.shoesImage) {
                Glide.with(mContext).load(data?.shoesImageUrl).into(this)
                transitionName = data?.shoesUrl

                setOnClickListener {
                    itemListener.onClickImage(
                        data?.shoesUrl!!,
                        data.shoesImageUrl!!,
                        this
                    )
                }
            }
            binding.shoesSubtitleText.text = data?.shoesSubTitle
            binding.shoesTitleText.text = data?.shoesTitle
            binding.shoesHowToEventText.text = data?.shoesPrice
            if (data?.shoesPrice == ShoesDataModel.SHOES_SOLD_OUT) {
                binding.shoesHowToEventText.setTextColor(Color.RED)
            } else {
                binding.shoesHowToEventText.setTextColor(Color.BLACK)
            }

            binding.learnMoreText.setOnClickListener {
                itemListener.onClickItem(data?.shoesUrl)
            }
        }
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