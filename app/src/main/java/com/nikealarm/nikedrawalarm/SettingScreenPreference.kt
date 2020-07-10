package com.nikealarm.nikedrawalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.*

/*
* 알림설정(버전 별마다)
* */

class SettingScreenPreference : PreferenceFragmentCompat() {
    private lateinit var mAlarmManager: AlarmManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_screen, rootKey)
        setHasOptionsMenu(true)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 인스턴스 설정
        mAlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 알람 설정 스위치
        val allowAlarmSwitch =
            findPreference<SwitchPreferenceCompat>(getString(R.string.setting_preference_switchKey))?.apply {
                setOnPreferenceClickListener {
                    if (this.isChecked) {
                        setAlarm()
                    } else {
                        removeAlarm()
                    }
                    true
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.action_settingScreenPreference_to_mainFragment)
                true
            }
            else -> false
        }
    }

    // 알람 설정
    private fun setAlarm() {
        val mIntent = Intent(context, MyAlarmReceiver::class.java)

        val mCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val timeTrigger: Long
        if (System.currentTimeMillis() > mCalendar.timeInMillis) {
            timeTrigger = mCalendar.timeInMillis + 86400000
            mIntent.putExtra(MainActivity.SET_ALARM, timeTrigger + 86400000)
        } else {
            timeTrigger = mCalendar.timeInMillis
            mIntent.putExtra(MainActivity.SET_ALARM, timeTrigger + 86400000)
        }

        val mPendingIntent = PendingIntent.getBroadcast(
            context,
            MainActivity.REQUEST_ALARM_CODE,
            mIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 오전 9시 알람 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeTrigger,
                mPendingIntent
            )
        }

//        val parsingWorkRequest = OneTimeWorkRequestBuilder<ParsingWorker>()
//            .build()
//        WorkManager.getInstance(requireContext()).enqueue(parsingWorkRequest)
        Log.i("SetAlarm", "동작")
    }

    // 알람 지우기
    private fun removeAlarm() {
        val mIntent = Intent(context, MyAlarmReceiver::class.java)

        // 이미 설정된 알람이 있는지 확인
        if (checkExistAlarm(mIntent)) {

            // 설정된 알람이 있으면 삭제함
            val alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                MainActivity.REQUEST_ALARM_CODE,
                mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            mAlarmManager.cancel(alarmPendingIntent)
            alarmPendingIntent.cancel()

            Log.i("RemoveAlarm", "동작")
        }
    }

    // 알림 확인
    private fun checkExistAlarm(mIntent: Intent): Boolean {
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            MainActivity.REQUEST_ALARM_CODE,
            mIntent,
            PendingIntent.FLAG_NO_CREATE
        )

        if (alarmPendingIntent != null) {
            return true
        }

        return false
    }
}