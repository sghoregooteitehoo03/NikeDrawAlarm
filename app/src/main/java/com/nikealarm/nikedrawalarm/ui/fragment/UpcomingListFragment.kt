package com.nikealarm.nikedrawalarm.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.adapter.UpcomingListAdapter
import com.nikealarm.nikedrawalarm.database.EventDay
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.databinding.FragmentUpcomingListBinding
import com.nikealarm.nikedrawalarm.other.AlarmBuilder
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.nikealarm.nikedrawalarm.viewmodel.upcoming.UpcomingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UpcomingListFragment : Fragment(R.layout.fragment_upcoming_list),
    UpcomingListAdapter.ClickListener {
    private val mViewModel by viewModels<UpcomingViewModel>()
    private lateinit var mAdapter: UpcomingListAdapter
    private var fragmentBinding: FragmentUpcomingListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인스턴스 설정
        mAdapter = UpcomingListAdapter(mViewModel.getAllowAlarmPref()).apply {
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
            removeAlarm(specialShoesData!!, pos)
        } else { // 알림이 설정 되어있지 않을 때
            setAlarm(specialShoesData!!, pos)
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
    // 알람 설정
    private fun setAlarm(specialShoesData: SpecialShoesDataModel, pos: Int) {
        val timeTrigger = getTimeInMillis(
            EventDay(
                specialShoesData.SpecialYear!!,
                specialShoesData.SpecialMonth!!,
                specialShoesData.SpecialDay!!,
                specialShoesData.SpecialWhenEvent!!
            )
        )

        if (timeTrigger != 0L) {
            with(AlarmBuilder(requireContext())) {
                val bundle = Bundle().apply { // 알람 설정
                    putString(Contents.INTENT_KEY_POSITION, specialShoesData.ShoesUrl)

                    if (specialShoesData.ShoesCategory == ShoesDataModel.CATEGORY_DRAW) {
                        putBoolean(Contents.INTENT_KEY_IS_DRAW, true)
                    }
                }

                setIntent(Contents.INTENT_ACTION_PRODUCT_ALARM, bundle)
                setAlarm(timeTrigger, specialShoesData.ShoesId!!)
            }
            mViewModel.setPreference(specialShoesData.ShoesUrl, timeTrigger) // 알람 데이터 삭제

            mAdapter.notifyItemChanged(pos) // 리스트 상태 변경
            Snackbar.make(fragmentBinding?.mainLayout!!, "해당 상품의 알림을 설정하였습니다.", Snackbar.LENGTH_SHORT)
                .show()
        } else {
            Snackbar.make(fragmentBinding?.mainLayout!!, "알람 설정 중 문제가 발생하였습니다.", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    // 알람 삭제
    private fun removeAlarm(specialShoesData: SpecialShoesDataModel, pos: Int) {
        with(AlarmBuilder(requireContext())) { // 알람 삭제
            val bundle = Bundle().apply {
                putString(Contents.INTENT_KEY_POSITION, specialShoesData.ShoesUrl)

                if (specialShoesData.ShoesCategory == ShoesDataModel.CATEGORY_DRAW) {
                    putBoolean(Contents.INTENT_KEY_IS_DRAW, true)
                }
            }

            setIntent(Contents.INTENT_ACTION_PRODUCT_ALARM, bundle)
            removeAlarm(specialShoesData.ShoesId!!)
        }

        mViewModel.removePreference(specialShoesData.ShoesUrl) // 알람 데이터 삭제
        mAdapter.notifyItemChanged(pos) // 리스트 상태 변경

        Snackbar.make(fragmentBinding?.mainLayout!!, "알림을 취소하였습니다.", Snackbar.LENGTH_SHORT)
            .show()
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