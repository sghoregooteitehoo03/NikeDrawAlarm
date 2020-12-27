package com.nikealarm.nikedrawalarm.adapter.holder

import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.adapter.UpcomingListAdapter
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.databinding.ItemUpcomingListBinding

class UpcomingItemViewHolder(private val binding: ItemUpcomingListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    val swipeLayout = binding.swipeLayout

    fun bindView(data: SpecialShoesDataModel?, clickListener: UpcomingListAdapter.ClickListener, allowAlarmPreferences: SharedPreferences) {
        binding.monthText.text = data?.SpecialMonth
        binding.dayText.text = data?.SpecialDay
        binding.categoryText.text = when (data?.ShoesCategory) {
            ShoesDataModel.CATEGORY_DRAW -> "DRAW"
            ShoesDataModel.CATEGORY_COMING_SOON -> "COMING"
            else -> "DRAW"
        }
        binding.shoesTitleText.text = data?.ShoesTitle
        binding.shoesSubtitleText.text = data?.ShoesSubTitle
        binding.whenStartEventText.text = data?.SpecialWhenEvent
        Glide.with(itemView.context).load(data?.ShoesImageUrl).into(binding.shoesImage)

        if (data?.isOpened!!) { // 레이아웃 확장
            if (binding.subLayout.visibility == View.GONE) {
                expand()
            }
        } else {
            collapse()
        }

        binding.mainLayout.setOnClickListener {
            clickListener.onItemClickListener(adapterPosition)
        }

        if (isChecked(data.ShoesUrl, allowAlarmPreferences)) {
            binding.alarmBtn.setImageResource(R.drawable.ic_baseline_notifications_active)

            binding.alarmBtn.setOnClickListener {
                clickListener.onAlarmListener(data, adapterPosition, true)
            }
        } else {
            binding.alarmBtn.setImageResource(R.drawable.ic_baseline_notifications_none)

            binding.alarmBtn.setOnClickListener {
                clickListener.onAlarmListener(data, adapterPosition, false)
            }
        }
    }

    // 레이아웃 확장
    private fun expand() {
        expandAnimation()
    }

    // 레이아웃 축소
    private fun collapse() {
        collapseAnimation()
    }

    // 애니메이션 설정 시작
    private fun expandAnimation() {
        with(binding.moreInfoBtn) {
            animate().setDuration(200)
                .rotation(-180f)
                .withLayer()
        }

        with(binding.subLayout) {
            measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val actualHeight = measuredHeight

            layoutParams.height = 0
            visibility = View.VISIBLE

            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    super.applyTransformation(interpolatedTime, t)

                    layoutParams.height = if (interpolatedTime.toInt() == 1) {
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    } else {
                        (actualHeight * interpolatedTime).toInt()
                    }
                    requestLayout()
                }
            }

            animation.duration =
                (actualHeight / context.resources.displayMetrics.density).toLong()
            startAnimation(animation)
        }
    }

    private fun collapseAnimation() {
        with(binding.moreInfoBtn) {
            animate().setDuration(200)
                .rotation(0f)
                .withLayer()
        }

        with(binding.subLayout) {
            val actualHeight = measuredHeight

            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    super.applyTransformation(interpolatedTime, t)

                    if (interpolatedTime.toInt() == 1) {
                        visibility = View.GONE
                    } else {
                        layoutParams.height =
                            actualHeight - (actualHeight * interpolatedTime).toInt()
                        requestLayout()
                    }
                }
            }

            animation.duration =
                (actualHeight / context.resources.displayMetrics.density).toLong()
            startAnimation(animation)
        }
    }
    // 애니메이션 설정 끝

    private fun isChecked(preferenceKey: String?, allowAlarmPreferences: SharedPreferences): Boolean {
        return allowAlarmPreferences.getBoolean(preferenceKey, false)
    }
}