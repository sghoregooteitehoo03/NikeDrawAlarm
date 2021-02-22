package com.nikealarm.nikedrawalarm.adapter.holder

import android.content.SharedPreferences
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

class UpcomingItemViewHolder(
    private val binding: ItemUpcomingListBinding,
    clickListener: UpcomingListAdapter.ClickListener,
    private val allowAlarmPreferences: SharedPreferences
) :
    RecyclerView.ViewHolder(binding.root) {

    val swipeLayout = binding.swipeLayout

    init {
        binding.mainLayout.setOnClickListener {
            clickListener.onItemClickListener(adapterPosition)
        }

        binding.alarmBtn.setOnClickListener {
            if (binding.alarmBtn.tag == R.drawable.ic_baseline_notifications_active) {
                clickListener.onAlarmListener(adapterPosition, true)
            } else {
                clickListener.onAlarmListener(adapterPosition, false)
            }
        }
    }

    fun bindView(data: SpecialShoesDataModel?) {
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



        if (isChecked(data.ShoesUrl)) {
            val res = R.drawable.ic_baseline_notifications_active

            binding.alarmBtn.setImageResource(res)
            binding.alarmBtn.tag = res

        } else {
            val res = R.drawable.ic_baseline_notifications_none

            binding.alarmBtn.setImageResource(res)
            binding.alarmBtn.tag = res
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

    private fun isChecked(
        preferenceKey: String?
    ): Boolean {
        return allowAlarmPreferences.getBoolean(preferenceKey, false)
    }
}