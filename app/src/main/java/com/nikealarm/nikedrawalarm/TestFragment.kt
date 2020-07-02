package com.nikealarm.nikedrawalarm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelStoreOwner
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest

class TestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val testText = view.findViewById<TextView>(R.id.test_text)
        val testButton = view.findViewById<Button>(R.id.test_button).apply {
            setOnClickListener {
                // 버튼 클릭 시

                val parsingWorkRequest = OneTimeWorkRequestBuilder<ParsingWorker>()
                    .build()
                WorkManager.getInstance(context).enqueue(parsingWorkRequest)

                WorkManager.getInstance(context).getWorkInfoByIdLiveData(parsingWorkRequest.id)
                    .observe(viewLifecycleOwner, Observer {
                        if(it != null && it.state == WorkInfo.State.SUCCEEDED) {
                            val result = it.outputData.getString("Data")

                            testText.text = result
                        }
                    })
            }
        }
    }
}