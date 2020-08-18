package com.nikealarm.nikedrawalarm.ui.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.nikealarm.nikedrawalarm.component.MyAlarmReceiver
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.component.FindDrawWorker
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel
import java.util.*

class SettingScreenPreference : PreferenceFragmentCompat() {
    private lateinit var mAlarmManager: AlarmManager
    private lateinit var mSharedPreference: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_screen, rootKey)

        // 인스턴스 설정
        mAlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mSharedPreference = requireContext().getSharedPreferences(
            Contents.PREFERENCE_NAME_TIME,
            Context.MODE_PRIVATE
        )

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
//        val shareDropDownPreference =
//            findPreference<Preference>(getString(R.string.setting_preference_share))?.apply {
//                setOnPreferenceClickListener {
//                    shareIntent()
//                    true
//                }
//            }
        val emailDropDownPreference =
            findPreference<Preference>(getString(R.string.setting_preference_email))?.apply {
                setOnPreferenceClickListener {
                    emailIntent()
                    true
                }
            }
    }

    // 알람 설정
    private fun setAlarm() {
        val alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
            action = Contents.INTENT_ACTION_SYNC_ALARM
        }

        val mCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val timeTrigger = if (System.currentTimeMillis() > mCalendar.timeInMillis) {
            mCalendar.timeInMillis + 86400000
        } else {
            mCalendar.timeInMillis
        }

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            Contents.SYNC_ALARM_CODE,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 오전 8시 알람 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeTrigger,
                alarmPendingIntent
            )
        } else {
            mAlarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeTrigger,
                alarmPendingIntent
            )
        }
        setPreference(timeTrigger)

        // 테스트
//        val parsingWorkRequest = OneTimeWorkRequestBuilder<FindDrawWorker>()
//            .build()
//        WorkManager.getInstance(requireContext()).enqueue(parsingWorkRequest)

        Log.i("SetAlarm", "동작")
    }

    // 등록한 알람시간을 데이터베이스에 저장함
    private fun setPreference(timeTrigger: Long) {
        with(mSharedPreference.edit()) {
            putLong(Contents.SYNC_ALARM_KEY, timeTrigger)
            commit()
        }
    }

    // 알람 지우기
    private fun removeAlarm() {
        val mIntent = Intent(context, MyAlarmReceiver::class.java).apply {
            action = Contents.INTENT_ACTION_SYNC_ALARM
        }

        // 이미 설정된 알람이 있는지 확인
        if (checkExistAlarm(mIntent)) {

            // 설정된 알람이 있으면 삭제함
            val alarmPendingIntent = PendingIntent.getBroadcast(
                context,
                Contents.SYNC_ALARM_CODE,
                mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            mAlarmManager.cancel(alarmPendingIntent)
            alarmPendingIntent.cancel()
            removePreference()

            Log.i("RemoveAlarm", "동작")
        }
    }

    // 알림 확인
    private fun checkExistAlarm(mIntent: Intent): Boolean {
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            Contents.SYNC_ALARM_CODE,
            mIntent,
            PendingIntent.FLAG_NO_CREATE
        )

        if (alarmPendingIntent != null) {
            return true
        }

        return false
    }

    // 등록한 알람시간을 데이터베이스에서 지움
    private fun removePreference() {
        with(mSharedPreference.edit()) {
            this.remove(Contents.SYNC_ALARM_KEY)
            commit()
        }
    }

    // 출시 후 수정하기
    private fun shareIntent() {

    }

    private fun emailIntent() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            val email = arrayOf(getString(R.string.developer_email))
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, email)
        }

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }
}