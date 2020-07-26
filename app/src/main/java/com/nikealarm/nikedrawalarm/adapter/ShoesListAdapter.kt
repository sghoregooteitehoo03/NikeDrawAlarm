package com.nikealarm.nikedrawalarm.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nikealarm.nikedrawalarm.component.MyAlarmReceiver
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.squareup.picasso.Picasso
import java.util.*

class ShoesListAdapter(
    private val mContext: Context,
    private val mSharedPreferences: SharedPreferences?
) :
    PagedListAdapter<ShoesDataModel, ShoesListAdapter.DrawListViewHolder>(
        diffCallback
    ) {

    private lateinit var mListener: ItemClickListener

    interface ItemClickListener {
        fun onClickItem(newUrl: String?)
    }

    fun setOnItemClickListener(listener: ItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.drawlist_item, parent, false)
        return DrawListViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrawListViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class DrawListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shoesImage = itemView.findViewById<ImageView>(R.id.drawList_imgItem)
        val shoesSubTitleText = itemView.findViewById<TextView>(R.id.drawList_shoesSubTitle_text)
        val shoesTitleText = itemView.findViewById<TextView>(R.id.drawList_shoesTitle_text)
        val howToEventText = itemView.findViewById<TextView>(R.id.drawList_howToEvent_text)
        val allowAlarmBtn = itemView.findViewById<ImageButton>(R.id.drawList_allowAlarm_btn)
        val learnMoreText = itemView.findViewById<TextView>(R.id.drawList_learnMore_text)

        fun bindView(data: ShoesDataModel?) {
            Picasso.get().load(data?.shoesImageUrl).into(shoesImage)
            shoesSubTitleText.text = data?.shoesSubTitle
            shoesTitleText.text = data?.shoesTitle
            howToEventText.text = data?.shoesPrice

            learnMoreText.setOnClickListener {
                mListener.onClickItem(data?.shoesUrl)
            }

            if(data?.shoesCategory == ShoesDataModel.CATEGORY_DRAW) {
                allowAlarmBtn.visibility = View.VISIBLE

                if (isChecked(data.shoesTitle)) {
                    allowAlarmBtn.setImageResource(R.drawable.ic_baseline_notifications_active)

                    allowAlarmBtn.setOnClickListener {
                        removeNotification(data.shoesPrice, adapterPosition, data.shoesTitle)
                    }
                } else {
                    allowAlarmBtn.setImageResource(R.drawable.ic_baseline_notifications_none)

                    allowAlarmBtn.setOnClickListener {
                        setNotification(data.shoesPrice, adapterPosition, data.shoesTitle)
                    }
                }
            } else {
                allowAlarmBtn.visibility = View.GONE
            }
        }

        private fun setPreference(preferenceKey: String?, timeTrigger: Long) {
            val isAllowAlarm = !isChecked(preferenceKey)
            val timeSharedPreference = mContext.getSharedPreferences(Contents.PREFERENCE_NAME_TIME, Context.MODE_PRIVATE)

            if(isAllowAlarm) {
                with(timeSharedPreference.edit()) {
                    putLong(preferenceKey, timeTrigger)
                    commit()
                }

                with(mSharedPreferences?.edit()) {
                    this?.putBoolean(preferenceKey, isAllowAlarm)
                    this?.commit()
                }
            } else {
                with(timeSharedPreference.edit()) {
                    this.remove(preferenceKey)
                    commit()
                }

                with(mSharedPreferences?.edit()) {
                    this?.remove(preferenceKey)
                    this?.commit()
                }
            }

        }

        private fun isChecked(preferenceKey: String?): Boolean {
            return mSharedPreferences!!.getBoolean(preferenceKey, false)
        }

        // 알람을 설정함
        private fun setNotification(howToEvent: String?, requestCode: Int, preferenceKey: String?) {
            val dialog = createDialog()
            with(dialog) {
                setMessage("이 상품의 알림을 설정하시겠습니까?")
                show()
                getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    setAlarm(howToEvent, requestCode)
                    setPreference(preferenceKey, getTimeInMillis(howToEvent))

                    notifyDataSetChanged()
                    dismiss()
                }
            }
        }

        private fun removeNotification(howToEvent: String?, requestCode: Int, preferenceKey: String?) {
            val dialog = createDialog()
            with(dialog) {
                setMessage("이 상품의 알림을 취소하시겠습니까?")
                show()

                getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    removeAlarm(requestCode)

                    setPreference(preferenceKey, getTimeInMillis(howToEvent))
                    notifyDataSetChanged()

                    dismiss()
                }
            }
        }

        private fun createDialog(): AlertDialog {

            return AlertDialog.Builder(mContext)
                .setTitle("알림")
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
                .create()
        }

        private fun setAlarm(howToEvent: String?, requestCode: Int) {
            val timeTrigger = getTimeInMillis(howToEvent)

            val alarmIntent = Intent(mContext, MyAlarmReceiver::class.java).apply {
                action = Contents.INTENT_ACTION_PRODUCT_ALARM
                putExtra(Contents.INTENT_KEY_POSITION, requestCode)
            }
            val alarmPendingIntent = PendingIntent.getBroadcast(
                mContext,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

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

        private fun removeAlarm(requestCode: Int) {
            val alarmIntent = Intent(mContext, MyAlarmReceiver::class.java).apply {
                action = Contents.INTENT_ACTION_PRODUCT_ALARM
                putExtra(Contents.INTENT_KEY_POSITION, requestCode)
            }

            // 이미 설정된 알람이 있는지 확인
            if (checkExistAlarm(alarmIntent, requestCode)) {

                // 설정된 알람이 있으면 삭제함
                val alarmPendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    requestCode,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(alarmPendingIntent)
                alarmPendingIntent.cancel()

                Log.i("RemoveAlarm", "동작")
            }
        }

        // 알림 확인
        private fun checkExistAlarm(mIntent: Intent, requestCode: Int): Boolean {
            val alarmPendingIntent = PendingIntent.getBroadcast(
                mContext,
                requestCode,
                mIntent,
                PendingIntent.FLAG_NO_CREATE
            )

            if (alarmPendingIntent != null) {
                return true
            }

            return false
        }

        private fun getTimeInMillis(howToEvent: String?): Long {
            val month = howToEvent?.substring(6, 8)?.toInt() ?: -1
            val day = howToEvent?.substring(9, 11)?.toInt() ?: -1
            val hour = howToEvent?.substring(15, 17)?.toInt() ?: -1
            val minute = howToEvent?.substring(18, 20)?.toInt() ?: -1

            val mCalendar = Calendar.getInstance().apply {
                if (month != -1 && day != -1 && hour != -1 && minute != -1) {
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

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ShoesDataModel>() {
            override fun areItemsTheSame(
                oldItem: ShoesDataModel,
                newItem: ShoesDataModel
            ): Boolean =
                oldItem.shoesTitle == newItem.shoesTitle

            override fun areContentsTheSame(
                oldItem: ShoesDataModel,
                newItem: ShoesDataModel
            ): Boolean =
                oldItem.id == newItem.id
        }
    }
}