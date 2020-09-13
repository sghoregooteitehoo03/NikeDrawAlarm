package com.nikealarm.nikedrawalarm.adapter

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.component.MyAlarmReceiver
import com.nikealarm.nikedrawalarm.database.EventDay
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.dialog.AlarmDialog
import java.util.*

class SpecialShoesListAdapter(private val context: Context, private val fragmentManager: FragmentManager) :
    PagedListAdapter<SpecialShoesDataModel, SpecialShoesListAdapter.SpecialShoesListViewHolder>(
        diffCallback
    ) {

    private var previousPosition = -1 // 이전에 선택한 리스트뷰에 위치
    private val viewBinderHelper = ViewBinderHelper()

    inner class SpecialShoesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthText = itemView.findViewById<TextView>(R.id.upcomingList_monthText)
        val dayText = itemView.findViewById<TextView>(R.id.upcomingList_dayText)
        val shoesTitleText = itemView.findViewById<TextView>(R.id.upcomingList_shoesTitle_text)
        val shoesSubTitleText =
            itemView.findViewById<TextView>(R.id.upcomingList_shoesSubTitle_text)
        val whenStartEventText =
            itemView.findViewById<TextView>(R.id.upcomingList_whenStartEvent_text)
        val shoesImageView = itemView.findViewById<ImageView>(R.id.upcomingList_shoesImage_imageView)
        val alarmImageButton = itemView.findViewById<ImageButton>(R.id.upcomingList_alarm_imageButton)
        val moreInfoButton = itemView.findViewById<ImageButton>(R.id.upcomingList_moreInfo_imageButton)

        val mainLayout = itemView.findViewById<ConstraintLayout>(R.id.upcomingList_mainLayout)
        val subLayout = itemView.findViewById<ConstraintLayout>(R.id.upcomingList_subLayout)
        val swipeLayout = itemView.findViewById<SwipeRevealLayout>(R.id.upcomingList_swipeLayout)

        fun bindView(data: SpecialShoesDataModel?) {
            monthText.text = data?.shoesMonth
            dayText.text = data?.shoesDay
            shoesTitleText.text = data?.shoesTitle
            shoesSubTitleText.text = data?.shoesSubTitle
            whenStartEventText.text = data?.shoesWhenEvent
            Glide.with(itemView.context).load(data?.shoesImage).into(shoesImageView)

            if(data?.isOpened!!) { // 레이아웃 확장
                if(subLayout.visibility == View.GONE) {
                    expand()
                }
            } else {
                collapse()
            }

            mainLayout.setOnClickListener {
                data.isOpened = !data.isOpened

                if(previousPosition != -1 && previousPosition != adapterPosition) { // 다른 리스트를 눌렀을 때
                    currentList?.get(previousPosition)?.isOpened = !currentList?.get(previousPosition)!!.isOpened
                    notifyItemChanged(previousPosition)
                }

                notifyItemChanged(adapterPosition)
                previousPosition = if(previousPosition == adapterPosition) { // 같은 리스트를 눌렀을 때
                    -1
                } else { // 다른 리스트를 눌렀을 때
                    adapterPosition
                }
            }

            if (isChecked("${data.shoesTitle}-${data.shoesSubTitle}")) {
                alarmImageButton.setImageResource(R.drawable.ic_baseline_notifications_active)

                alarmImageButton.setOnClickListener {
                    removeNotification(adapterPosition, "${data.shoesTitle}-${data.shoesSubTitle}")
                }
            } else {
                alarmImageButton.setImageResource(R.drawable.ic_baseline_notifications_none)

                alarmImageButton.setOnClickListener {
                    setNotification(
                        EventDay(data.shoesMonth!!, data.shoesDay!!, data.shoesWhenEvent!!),
                        adapterPosition,
                        "${data.shoesTitle}-${data.shoesSubTitle}"
                    )
                }
            }
        }

        // 애니메이션 설정
        private fun expand() {
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

                        layoutParams.height = if(interpolatedTime.toInt() == 1) {
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        } else {
                            (actualHeight * interpolatedTime).toInt()
                        }
                        requestLayout()
                    }
                }

                animation.duration = (actualHeight / context.resources.displayMetrics.density).toLong()
                startAnimation(animation)
            }
        }

        private fun collapse() {
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

                        if(interpolatedTime.toInt() == 1) {
                            visibility = View.GONE
                        } else {
                            layoutParams.height = actualHeight - (actualHeight * interpolatedTime).toInt()
                            requestLayout()
                        }
                    }
                }

                animation.duration = (actualHeight / context.resources.displayMetrics.density).toLong()
                startAnimation(animation)
            }
        }

        // 데이터베이스에 저장
        private fun setPreference(preferenceKey: String?, timeTrigger: Long = 0L) {
            val isAllowAlarm = !isChecked(preferenceKey)

            val allowAlarmPreference = context.getSharedPreferences(
                Contents.PREFERENCE_NAME_ALLOW_ALARM,
                Context.MODE_PRIVATE
            )
            val timeSharedPreference =
                context.getSharedPreferences(Contents.PREFERENCE_NAME_TIME, Context.MODE_PRIVATE)

            if (isAllowAlarm) {
                with(timeSharedPreference.edit()) {
                    putLong(preferenceKey, timeTrigger)
                    commit()
                }

                with(allowAlarmPreference.edit()) {
                    this.putBoolean(preferenceKey, isAllowAlarm)
                    this.commit()
                }
            } else {
                with(timeSharedPreference.edit()) {
                    this.remove(preferenceKey)
                    commit()
                }

                with(allowAlarmPreference.edit()) {
                    this.remove(preferenceKey)
                    this.commit()
                }
            }

        }

        private fun isChecked(preferenceKey: String?): Boolean {
            val allowAlarmPreference = context.getSharedPreferences(
                Contents.PREFERENCE_NAME_ALLOW_ALARM,
                Context.MODE_PRIVATE
            )
            return allowAlarmPreference.getBoolean(preferenceKey, false)
        }

        // 알람 설정 알림창
        private fun setNotification(eventDay: EventDay, requestCode: Int, preferenceKey: String?) {
            val timeTrigger = getTimeInMillis(eventDay)
            AlarmDialog.getAlarmDialog("알림 설정", "이 상품의 알림을 설정하시겠습니까?")
                .show(fragmentManager, AlarmDialog.ALARM_DIALOG_TAG)

            AlarmDialog.setOnCheckClickListener(object : AlarmDialog.CheckClickListener {
                override fun onCheckClickListener(dialog: Dialog) {
                    setAlarm(timeTrigger, requestCode)
                    setPreference(preferenceKey, timeTrigger)

                    notifyItemChanged(adapterPosition)
                    dialog.dismiss()
                }
            })
        }

        // 알람 취소 알림창
        private fun removeNotification(requestCode: Int, preferenceKey: String?) {
            AlarmDialog.getAlarmDialog("알림 설정", "이 상품의 알림을 취소하시겠습니까?")
                .show(fragmentManager, AlarmDialog.ALARM_DIALOG_TAG)

            AlarmDialog.setOnCheckClickListener(object : AlarmDialog.CheckClickListener {
                override fun onCheckClickListener(dialog: Dialog) {
                    removeAlarm(requestCode)
                    setPreference(preferenceKey)

                    notifyItemChanged(adapterPosition)
                    dialog.dismiss()
                }
            })
        }

        // 알람 설정
        private fun setAlarm(timeTrigger: Long, requestCode: Int) {

            val alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
                action = Contents.INTENT_ACTION_PRODUCT_ALARM
                putExtra(Contents.INTENT_KEY_POSITION, requestCode)
            }
            val alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeTrigger,
                    alarmPendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeTrigger,
                    alarmPendingIntent
                )
            }
        }

        // 알람 삭제
        private fun removeAlarm(requestCode: Int) {
            val alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
                action = Contents.INTENT_ACTION_PRODUCT_ALARM
                putExtra(Contents.INTENT_KEY_POSITION, requestCode)
            }

            // 이미 설정된 알람이 있는지 확인
            if (checkExistAlarm(alarmIntent, requestCode)) {

                // 설정된 알람이 있으면 삭제함
                val alarmPendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(alarmPendingIntent)
                alarmPendingIntent.cancel()

                Log.i("RemoveAlarm", "동작")
            }
        }

        // 알림 확인
        private fun checkExistAlarm(mIntent: Intent, requestCode: Int): Boolean {
            val alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                mIntent,
                PendingIntent.FLAG_NO_CREATE
            )

            return alarmPendingIntent?.let {
                true
            }?:let {
                false
            }
        }

        private fun getTimeInMillis(eventDay: EventDay): Long {
            val month = eventDay.eventMonth[0].toString().toIntOrNull()
            val day = eventDay.eventDay.toIntOrNull()

            val time = eventDay.eventTime.substring(2, 8).trim().split(":")
            val hour = time[0].toIntOrNull()
            val minute = time[1].toIntOrNull()

            val mCalendar = Calendar.getInstance().apply {
                if (month != null && day != null && hour != null && minute != null) {
                    set(Calendar.MONTH, month - 1)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            }

            return mCalendar.timeInMillis
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialShoesListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upcoming_listitem, parent, false)
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
        return currentList?.get(position)?.id?.toLong()!!
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SpecialShoesDataModel>() {
            override fun areItemsTheSame(
                oldItem: SpecialShoesDataModel,
                newItem: SpecialShoesDataModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: SpecialShoesDataModel,
                newItem: SpecialShoesDataModel
            ): Boolean =
                oldItem.shoesTitle == newItem.shoesTitle
        }
    }
}