package com.nikealarm.nikedrawalarm.ui.fragment

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.adapter.UpcomingListAdapter
import com.nikealarm.nikedrawalarm.component.MyAlarmReceiver
import com.nikealarm.nikedrawalarm.database.EventDay
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.databinding.FragmentUpcomingListBinding
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.nikealarm.nikedrawalarm.ui.dialog.AlarmDialog
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class UpcomingListFragment : Fragment(R.layout.fragment_upcoming_list), UpcomingListAdapter.ClickListener {
    private val mViewModel by activityViewModels<MyViewModel>()
    private lateinit var mAdapter: UpcomingListAdapter
    private var fragmentBinding: FragmentUpcomingListBinding? = null

    @Inject
    @Named(Contents.PREFERENCE_NAME_TIME)
    lateinit var timePreferences: SharedPreferences

    @Inject
    @Named(Contents.PREFERENCE_NAME_ALLOW_ALARM)
    lateinit var allowAlarmPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인스턴스 설정
        mAdapter = UpcomingListAdapter(requireContext(), allowAlarmPreferences).apply {
            setHasStableIds(true)
            setOnAlarmListener(this@UpcomingListFragment)
        }

        // 옵저버 설정
        setObserver()
        // 뷰 설정
        initView(view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    override fun onAlarmListener(
        specialShoesData: SpecialShoesDataModel?,
        pos: Int,
        isChecked: Boolean
    ) {
        if (isChecked) { // 알림이 설정 되어있을 때
            removeNotification(specialShoesData!!, pos)
        } else { // 알림이 설정 되어있지 않을 때6
            setNotification(specialShoesData!!, pos)
        }
    }

    override fun onItemClickListener(position: Int) {
        mAdapter.currentList?.get(position)?.isOpened =
            !mAdapter.currentList?.get(position)!!.isOpened
        Log.i("CheckList", "${mAdapter.currentList?.get(position)?.isOpened}")

        if (mAdapter.previousPosition != -1 && mAdapter.previousPosition != position) { // 다른 리스트를 눌렀을 때
            mAdapter.currentList?.get(mAdapter.previousPosition)?.isOpened =
                !mAdapter.currentList?.get(mAdapter.previousPosition)!!.isOpened
            mAdapter.notifyItemChanged(mAdapter.previousPosition)
        }

        mAdapter.notifyItemChanged(position)
        mAdapter.previousPosition = if (mAdapter.previousPosition == position) { // 같은 리스트를 눌렀을 때
            -1
        } else { // 다른 리스트를 눌렀을 때
            position
        }
    }

    override fun onDestroy() {
        fragmentBinding = null
        mViewModel.upcomingCategory.value = "DEFAULT"
        super.onDestroy()
    }

    private fun initView(view: View) { // 뷰 설정
        val binding = FragmentUpcomingListBinding.bind(view)
        fragmentBinding = binding

        with(binding.mainToolbar) {  // 툴바
            (requireActivity() as MainActivity).setSupportActionBar(this)
            (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        with(binding.filterSpinner) { // 스피너
            val spinnerAdapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                listOf("DEFAULT", "DRAW", "COMING")
            )

            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    v: View?,
                    pos: Int,
                    id: Long
                ) {
                    mViewModel.upcomingCategory.value = when (pos) {
                        0 -> "DEFAULT"
                        1 -> ShoesDataModel.CATEGORY_DRAW
                        2 -> ShoesDataModel.CATEGORY_COMING_SOON
                        else -> "DEFAULT"
                    }

                    mAdapter.changeCategory()

                    v?.let {
                        (v as TextView).setTextColor(Color.WHITE)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {

                }
            }
        }
        with(binding.upcomingList) { // 리사이클
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun setObserver() {
        mViewModel.specialShoesList.observe(viewLifecycleOwner, Observer {
            with(mAdapter) {
                submitList(it)
                notifyDataSetChanged()
            }

            if (it.size == 0) {
                appearText()
            } else {
                if (fragmentBinding?.noItemText?.isEnabled!!) {
                    disappearText()
                }
            }
        })
    }

    // 알람 시작
    // 알람 설정 알림창
    private fun setNotification(specialShoesData: SpecialShoesDataModel, pos: Int) {
        val timeTrigger = getTimeInMillis(
            EventDay(
                specialShoesData.SpecialYear!!,
                specialShoesData.SpecialMonth!!,
                specialShoesData.SpecialDay!!,
                specialShoesData.SpecialWhenEvent!!
            )
        )

        if (timeTrigger != 0L) {
            AlarmDialog.getAlarmDialog("알림 설정", "이 상품의 알림을 설정하시겠습니까?")
                .show(requireActivity().supportFragmentManager, AlarmDialog.ALARM_DIALOG_TAG)

            AlarmDialog.setOnCheckClickListener(object : AlarmDialog.CheckClickListener {
                override fun onCheckClickListener(dialog: Dialog) {
                    setAlarm(timeTrigger, specialShoesData)
                    setPreference(
                        specialShoesData.ShoesUrl,
                        timeTrigger
                    )

                    mAdapter.notifyItemChanged(pos)
                    dialog.dismiss()
                }
            })
        } else {
            Toast.makeText(requireContext(), "알람 설정 중 문제가 발생하였습니다.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // 알람 취소 알림창
    private fun removeNotification(specialShoesData: SpecialShoesDataModel, pos: Int) {
        AlarmDialog.getAlarmDialog("알림 설정", "이 상품의 알림을 취소하시겠습니까?")
            .show(requireActivity().supportFragmentManager, AlarmDialog.ALARM_DIALOG_TAG)

        AlarmDialog.setOnCheckClickListener(object : AlarmDialog.CheckClickListener {
            override fun onCheckClickListener(dialog: Dialog) {
                removeAlarm(specialShoesData)
                removePreference(specialShoesData.ShoesUrl)

                mAdapter.notifyItemChanged(pos)
                dialog.dismiss()
            }
        })
    }

    // 알람 설정
    private fun setAlarm(timeTrigger: Long, specialShoesData: SpecialShoesDataModel) {
        val alarmIntent = Intent(requireContext(), MyAlarmReceiver::class.java).apply {
            action = Contents.INTENT_ACTION_PRODUCT_ALARM
            putExtra(Contents.INTENT_KEY_POSITION, specialShoesData.ShoesUrl)

            if (specialShoesData.ShoesCategory == ShoesDataModel.CATEGORY_DRAW) {
                putExtra(Contents.INTENT_KEY_IS_DRAW, true)
            }
        }
        val alarmPendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            specialShoesData.ShoesId!!,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

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
    private fun removeAlarm(specialShoesData: SpecialShoesDataModel) {
        val alarmIntent = Intent(requireContext(), MyAlarmReceiver::class.java).apply {
            action = Contents.INTENT_ACTION_PRODUCT_ALARM
            putExtra(Contents.INTENT_KEY_POSITION, specialShoesData.ShoesUrl)

            if (specialShoesData.ShoesCategory == ShoesDataModel.CATEGORY_DRAW) {
                putExtra(Contents.INTENT_KEY_IS_DRAW, true)
            }
        }

        // 이미 설정된 알람이 있는지 확인
        if (checkExistAlarm(alarmIntent, specialShoesData.ShoesId!!)) {

            // 설정된 알람이 있으면 삭제함
            val alarmPendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                specialShoesData.ShoesId,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager =
                requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(alarmPendingIntent)
            alarmPendingIntent.cancel()

            Log.i("RemoveAlarm", "동작")
        }
    }

    // 알림 확인
    private fun checkExistAlarm(mIntent: Intent, requestCode: Int): Boolean {
        val alarmPendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            mIntent,
            PendingIntent.FLAG_NO_CREATE
        )

        return alarmPendingIntent?.let {
            true
        } ?: let {
            false
        }
    }

    private fun getTimeInMillis(eventDay: EventDay): Long {
        val time = eventDay.eventTime.substring(2, 8).trim().split(":")

        if (time.size > 1) {
            val year = eventDay.eventYear.toIntOrNull()
            val month = if (eventDay.eventMonth[1].toString() != "월") {
                "${eventDay.eventMonth[0]}${eventDay.eventMonth[1]}".toIntOrNull() // 10월, 11월, 12월 처리
            } else {
                eventDay.eventMonth[0].toString().toIntOrNull()
            }
            val day = eventDay.eventDay.toIntOrNull()

            val hour = time[0].toIntOrNull()
            val minute = time[1].toIntOrNull()

            val mCalendar = Calendar.getInstance().apply {
                if (year != null && month != null && day != null && hour != null && minute != null) {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month - 1)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                } else {
                    return 0
                }
            }

            return mCalendar.timeInMillis
        }

        return 0
    }

    // 데이터베이스에 저장
    private fun setPreference(preferenceKey: String?, timeTrigger: Long) {
        with(timePreferences.edit()) {
            putLong(preferenceKey, timeTrigger)
            commit()
        }

        with(allowAlarmPreferences.edit()) {
            putBoolean(preferenceKey, true)
            commit()
        }
    }

    private fun removePreference(preferenceKey: String?) {
        with(timePreferences.edit()) {
            remove(preferenceKey)
            commit()
        }

        with(allowAlarmPreferences.edit()) {
            remove(preferenceKey)
            commit()
        }
    }
    // 알람 끝

    // 애니메이션 설정 시작
    private fun appearText() {
        with(fragmentBinding?.noItemText!!) {
            isEnabled = true

            animate().setDuration(350)
                .alpha(1f)
                .withLayer()
        }
    }

    private fun disappearText() {
        with(fragmentBinding?.noItemText!!) {
            isEnabled = false

            animate().setDuration(100)
                .alpha(0f)
                .withLayer()
        }
    }
    // 애니메이션 설정 끝
}