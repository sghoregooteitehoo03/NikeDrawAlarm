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
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel

class UpcomingListAdapter(
    private val context: Context,
    private val allowAlarmPreferences: SharedPreferences
) :
    PagedListAdapter<SpecialShoesDataModel, UpcomingListAdapter.SpecialShoesListViewHolder>(
        diffCallback
    ) {

    interface AlarmListener {
        fun onAlarmListener(specialShoesData: SpecialShoesDataModel?, pos: Int, isChecked: Boolean)
    }

    private var previousPosition = -1 // 이전에 선택한 리스트뷰에 위치
    private val viewBinderHelper = ViewBinderHelper()
    private lateinit var alarmListener: AlarmListener

    inner class SpecialShoesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthText = itemView.findViewById<TextView>(R.id.upcomingList_monthText)
        val dayText = itemView.findViewById<TextView>(R.id.upcomingList_dayText)
        val categoryText = itemView.findViewById<TextView>(R.id.upcomingList_categoryText)
        val shoesTitleText = itemView.findViewById<TextView>(R.id.upcomingList_shoesTitle_text)
        val shoesSubTitleText =
            itemView.findViewById<TextView>(R.id.upcomingList_shoesSubTitle_text)
        val whenStartEventText =
            itemView.findViewById<TextView>(R.id.upcomingList_whenStartEvent_text)
        val shoesImageView =
            itemView.findViewById<ImageView>(R.id.upcomingList_shoesImage_imageView)
        val alarmImageButton =
            itemView.findViewById<ImageButton>(R.id.upcomingList_alarm_imageButton)
        val moreInfoButton =
            itemView.findViewById<ImageButton>(R.id.upcomingList_moreInfo_imageButton)

        val mainLayout = itemView.findViewById<ConstraintLayout>(R.id.upcomingList_mainLayout)
        val subLayout = itemView.findViewById<FrameLayout>(R.id.upcomingList_subLayout)
        val swipeLayout = itemView.findViewById<SwipeRevealLayout>(R.id.upcomingList_swipeLayout)

        fun bindView(data: SpecialShoesDataModel?) {
            monthText.text = data?.SpecialMonth
            dayText.text = data?.SpecialDay
            categoryText.text = when (data?.ShoesCategory) {
                ShoesDataModel.CATEGORY_DRAW -> "DRAW"
                ShoesDataModel.CATEGORY_COMING_SOON -> "COMING"
                else -> "DRAW"
            }
            shoesTitleText.text = data?.ShoesTitle
            shoesSubTitleText.text = data?.ShoesSubTitle
            whenStartEventText.text = data?.SpecialWhenEvent
            Glide.with(itemView.context).load(data?.ShoesImageUrl).into(shoesImageView)

            if (data?.isOpened!!) { // 레이아웃 확장
                if (subLayout.visibility == View.GONE) {
                    expand()
                }
            } else {
                collapse()
            }

            mainLayout.setOnClickListener {
                currentList?.get(adapterPosition)?.isOpened =
                    !currentList?.get(adapterPosition)!!.isOpened
                Log.i("CheckList", "${data.isOpened}")

                if (previousPosition != -1 && previousPosition != adapterPosition) { // 다른 리스트를 눌렀을 때
                    currentList?.get(previousPosition)?.isOpened =
                        !currentList?.get(previousPosition)!!.isOpened
                    notifyItemChanged(previousPosition)
                }

                notifyItemChanged(adapterPosition)
                previousPosition = if (previousPosition == adapterPosition) { // 같은 리스트를 눌렀을 때
                    -1
                } else { // 다른 리스트를 눌렀을 때
                    adapterPosition
                }
            }

            if (isChecked("${data.ShoesTitle}-${data.ShoesSubTitle}")) {
                alarmImageButton.setImageResource(R.drawable.ic_baseline_notifications_active)

                alarmImageButton.setOnClickListener {
                    alarmListener.onAlarmListener(data, adapterPosition, true)
                }
            } else {
                alarmImageButton.setImageResource(R.drawable.ic_baseline_notifications_none)

                alarmImageButton.setOnClickListener {
                    alarmListener.onAlarmListener(data, adapterPosition, false)
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
            with(moreInfoButton) {
                animate().setDuration(200)
                    .rotation(-180f)
                    .withLayer()
            }

            with(subLayout) {
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
            with(moreInfoButton) {
                animate().setDuration(200)
                    .rotation(0f)
                    .withLayer()
            }

            with(subLayout) {
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

        private fun isChecked(preferenceKey: String?): Boolean {
            return allowAlarmPreferences.getBoolean(preferenceKey, false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialShoesListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.upcoming_listitem, parent, false)
        return SpecialShoesListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpecialShoesListViewHolder, position: Int) {
        with(viewBinderHelper) {
            setOpenOnlyOne(true)
            bind(holder.swipeLayout, getItemId(position).toString())
        }
        holder.bindView(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return currentList?.get(position)?.ShoesId?.toLong()!!
    }

    fun setOnAlarmListener(_alarmListener: AlarmListener) {
        alarmListener = _alarmListener
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