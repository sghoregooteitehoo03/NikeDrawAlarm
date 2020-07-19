package com.nikealarm.nikedrawalarm

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
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
import java.util.*

class DrawListAdapter(private val mContext: Context) :
    PagedListAdapter<DrawShoesDataModel, DrawListAdapter.DrawListViewHolder>(
        diffCallback
    ) {

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

        fun bindView(data: DrawShoesDataModel?) {
            shoesImage.setImageBitmap(data?.shoesImage)
            shoesSubTitleText.text = data?.shoesSubTitle
            shoesTitleText.text = data?.shoesTitle
            howToEventText.text = data?.howToEvent

            allowAlarmBtn.setOnClickListener {
                setNotification(data?.howToEvent, adapterPosition)
            }
        }
    }

    // 알람을 설정함
    private fun setNotification(howToEvent: String?, requestCode: Int) {
        val dialog = createDialog()
        with(dialog) {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                setAlarm(howToEvent, requestCode)
                dismiss()
            }
        }
    }

    private fun createDialog(): AlertDialog {
        val dialog = AlertDialog.Builder(mContext)
            .setTitle("확인")
            .setMessage("이 상품의 알림을 받도록 설정하시겠습니까?")
            .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            })
            .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
            .create()

        return dialog
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
        }
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

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<DrawShoesDataModel>() {
            override fun areItemsTheSame(
                oldItem: DrawShoesDataModel,
                newItem: DrawShoesDataModel
            ): Boolean =
                oldItem.shoesTitle == newItem.shoesTitle

            override fun areContentsTheSame(
                oldItem: DrawShoesDataModel,
                newItem: DrawShoesDataModel
            ): Boolean =
                oldItem.id == newItem.id
        }
    }
}