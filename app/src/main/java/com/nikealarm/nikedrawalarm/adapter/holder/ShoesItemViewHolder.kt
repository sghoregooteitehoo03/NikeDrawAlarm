package com.nikealarm.nikedrawalarm.adapter.holder

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nikealarm.nikedrawalarm.adapter.ShoesListAdapter
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.databinding.ItemShoesListBinding

class ShoesItemViewHolder(private val binding: ItemShoesListBinding, clickListener: ShoesListAdapter.ItemClickListener) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.shoesImage.setOnClickListener {
            clickListener.onClickImage(
                adapterPosition,
                binding.shoesImage
            )
        }

        binding.learnMoreText.setOnClickListener {
            clickListener.onClickItem(adapterPosition)
        }
//        binding.shareText.setOnClickListener {
//            clickListener.onClickShare(adapterPosition)
//        }
    }

    fun bindView(data: ShoesDataModel?) {
        with(binding.shoesImage) {
            Glide.with(itemView).load(data?.shoesImageUrl).into(this)
            transitionName = data?.shoesUrl
        }
        binding.shoesSubtitleText.text = data?.shoesSubTitle
        binding.shoesTitleText.text = data?.shoesTitle
        binding.shoesHowToEventText.text = data?.shoesPrice
        if (data?.shoesPrice == ShoesDataModel.SHOES_SOLD_OUT) {
            binding.shoesHowToEventText.setTextColor(Color.RED)
        } else {
            binding.shoesHowToEventText.setTextColor(Color.BLACK)
        }
    }
}