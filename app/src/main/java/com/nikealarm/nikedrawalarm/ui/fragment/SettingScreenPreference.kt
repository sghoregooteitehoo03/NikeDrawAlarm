package com.nikealarm.nikedrawalarm.ui.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.nikealarm.nikedrawalarm.BuildConfig
import com.nikealarm.nikedrawalarm.component.MyAlarmReceiver
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SettingScreenPreference : PreferenceFragmentCompat() {
    private lateinit var mAlarmManager: AlarmManager
    private lateinit var mViewModel: MyViewModel

    @Inject
    @Named(Contents.PREFERENCE_NAME_TIME)
    lateinit var timePreferences: SharedPreferences

    @Inject
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER)
    lateinit var autoEnterPreference: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_screen, rootKey)

        // 인스턴스 설정
        mAlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mViewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        mViewModel.allowAutoEnter.value = autoEnterPreference.getBoolean(Contents.AUTO_ENTER_ALLOW, false)

        // Preference 설정
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
        // 자동응모 허용
        val autoEnterSwitchPref =
            findPreference<SwitchPreferenceCompat>(getString(R.string.setting_preference_autoEnter))?.apply {
                setOnPreferenceChangeListener { preference, allow ->
                    mViewModel.allowAutoEnter.value = allow as Boolean

                    if(allow) {
                        findNavController().navigate(R.id.editInfoDialog)
                    }
                    true
                }
            }
        // 정보 수정
        val editInfoPref =
            findPreference<Preference>(getString(R.string.setting_preference_editInfo))?.apply {
                setOnPreferenceClickListener {
                    findNavController().navigate(R.id.editInfoDialog)
                    true
                }
            }
        // 문의하기
        val emailDropDownPreference =
            findPreference<Preference>(getString(R.string.setting_preference_email))?.apply {
                setOnPreferenceClickListener {
                    emailIntent()
                    true
                }
            }
        // 버전
        val showVersionPreference =
            findPreference<Preference>(getString(R.string.setting_preference_version))?.apply {
                summary = BuildConfig.VERSION_NAME
            }

        // 옵저버 설정
        mViewModel.allowAutoEnter.observe(this, { allow ->
            editInfoPref?.isEnabled = allow
            autoEnterSwitchPref?.isChecked = allow

            if (allow) {
                with(autoEnterPreference.edit()) {
                    putBoolean(Contents.AUTO_ENTER_ALLOW, true)
                    commit()
                }
            } else {
                with(autoEnterPreference.edit()) {
                    clear()
                    commit()
                }
            }
        })
    }

    // Preference 설정
    // 등록한 알람시간을 데이터베이스에 저장함
    private fun setPreference(timeTrigger: Long) {
        with(timePreferences.edit()) {
            putLong(Contents.SYNC_ALARM_KEY, timeTrigger)
            commit()
        }
    }

    // 등록한 알람시간을 데이터베이스에서 지움
    private fun removePreference() {
        with(timePreferences.edit()) {
            this.remove(Contents.SYNC_ALARM_KEY)
            commit()
        }
    }
    // Preference 설정 끝

    // 알람 설정
    private fun setAlarm() {
        val alarmIntent = Intent(context, MyAlarmReceiver::class.java).apply {
            action = Contents.INTENT_ACTION_SYNC_ALARM
        }

        val mCalendar = Calendar.getInstance().apply {
            val time = if (this.get(Calendar.HOUR_OF_DAY) == 24) {
                3
            } else {
                this.get(Calendar.HOUR_OF_DAY) + 3
            }
            set(Calendar.HOUR_OF_DAY, time)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val timeTrigger = mCalendar.timeInMillis

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

            Log.i("RemoveAlarm", "동작")
        }
        removePreference()
    }

    // 알림 확인
    private fun checkExistAlarm(mIntent: Intent): Boolean {
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            Contents.SYNC_ALARM_CODE,
            mIntent,
            PendingIntent.FLAG_NO_CREATE
        )

        return alarmPendingIntent?.let {
            true
        } ?: let {
            false
        }
    }
    // 알람 설정 끝

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