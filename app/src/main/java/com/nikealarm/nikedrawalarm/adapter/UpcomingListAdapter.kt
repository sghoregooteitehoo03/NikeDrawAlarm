package com.nikealarm.nikedrawalarm.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.adapter.holder.UpcomingItemViewHolder
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.databinding.ItemUpcomingListBinding

class UpcomingListAdapter(
    private val context: Context,
    private val allowAlarmPreferences: SharedPreferences
) :
    PagedListAdapter<SpecialShoesDataModel, UpcomingItemViewHolder>(
        diffCallback
    ) {

    interface ClickListener {
        fun onAlarmListener(specialShoesData: SpecialShoesDataModel?, pos: Int, isChecked: Boolean)
        fun onItemClickListener(position: Int)
    }

    var previousPosition = -1 // 이전에 선택한 리스트뷰에 위치
    private val viewBinderHelper = ViewBinderHelper()
    private lateinit var clickListener: ClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingItemViewHolder {
        val view =
            ItemUpcomingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UpcomingItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingItemViewHolder, position: Int) {
        with(viewBinderHelper) {
            setOpenOnlyOne(true)
            bind(holder.swipeLayout, getItemId(position).toString())
        }
        holder.bindView(getItem(position), clickListener, allowAlarmPreferences)
    }

    override fun getItemId(position: Int): Long {
        return currentList?.get(position)?.ShoesId?.toLong()!!
    }

    fun setOnAlarmListener(_clickListener: ClickListener) {
        clickListener = _clickListener
    }

    fun changeCategory() {
        if (previousPosition != -1) {
            currentList?.get(previousPosition)?.isOpened = false
            previousPosition = -1
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SpecialShoesDataModel>() {
            override fun areItemsTheSame(
                oldItem: SpecialShoesDataModel,
                newItem: SpecialShoesDataModel
            ): Boolean =
                oldItem.ShoesId == newItem.ShoesId

            override fun areContentsTheSame(
                oldItem: SpecialShoesDataModel,
                newItem: SpecialShoesDataModel
            ): Boolean =
                oldItem.ShoesTitle == newItem.ShoesTitle
        }
    }
}