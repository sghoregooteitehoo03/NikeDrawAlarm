//package com.nikealarm.nikedrawalarm
//
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelStoreOwner
//import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.WorkInfo
//import androidx.work.WorkManager
//import androidx.work.WorkRequest
//import java.util.*
//
//class TestFragment : Fragment() {
//    private lateinit var mAlarmManager: AlarmManager
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_test, container, false)
//    }
//
//    // 시작
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        mAlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        val testText = view.findViewById<TextView>(R.id.test_text)
//        val testButton = view.findViewById<Button>(R.id.test_button).apply {
//            setOnClickListener {
//                // 버튼 클릭 시
//                val parsingWorkRequest = OneTimeWorkRequestBuilder<ParsingWorker>()
//                    .build()
//                WorkManager.getInstance(requireContext()).enqueue(parsingWorkRequest)
//            }
//        }
//    }
//
//    // 알람 설정
//    private fun setAlarm() {
//        val mIntent = Intent(context, MyAlarmReceiver::class.java)
//
//        val mCalendar = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 9)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }
//
//        val timeTrigger: Long
//        if (System.currentTimeMillis() > mCalendar.timeInMillis) {
//            timeTrigger = mCalendar.timeInMillis + 86400000
//            mIntent.putExtra(MainActivity.SET_ALARM, timeTrigger)
//        } else {
//            timeTrigger = mCalendar.timeInMillis
//            mIntent.putExtra(MainActivity.SET_ALARM, timeTrigger + 86400000)
//        }
//
//        val mPendingIntent = PendingIntent.getBroadcast(
//            requireContext(),
//            MainActivity.REQUEST_ALARM_CODE,
//            mIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        // 오전 9시 알람 설정
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeTrigger, mPendingIntent)
//        }
//
//        Toast.makeText(context, "눌림", Toast.LENGTH_SHORT).show()
//    }
//}