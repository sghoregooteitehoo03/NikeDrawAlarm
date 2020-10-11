package com.nikealarm.nikedrawalarm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.component.ParsingWorker
import com.nikealarm.nikedrawalarm.other.Contents
import kotlinx.android.synthetic.main.fragment_loading.*

class LoadingFragment : Fragment() {
    private lateinit var explainText: TextView
    private lateinit var restartBtn: Button
    private lateinit var progressBar: ProgressBar

    companion object {
        private var isStarted = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        startWork()
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // id 설정
        explainText = view.findViewById(R.id.loadingFrag_errorText)
        restartBtn = view.findViewById<Button>(R.id.loadingFrag_restart_btn).apply {
            setOnClickListener {
                startWorkAnimation()
                startWork()
            }
        }
        progressBar = view.findViewById(R.id.loadingFrag_progressBar)

        // 옵저버 설정
        WorkManager.getInstance(requireContext())
            .getWorkInfosByTagLiveData(Contents.WORKER_PARSING_DATA)
            .observe(viewLifecycleOwner, Observer {
                when (it[0].state) {
                    WorkInfo.State.SUCCEEDED -> { // 로딩 성공 시
                        isStarted = true
                        findNavController().navigate(R.id.action_loadingFragment_to_drawListFragment)
                    }
                    WorkInfo.State.FAILED -> { // 로딩 실패 시
                        failedWorkAnimation()
                    }
                    WorkInfo.State.RUNNING -> { // 로딩 중
                        val progress = it[0].progress
                        val value = progress.getInt(Contents.WORKER_PARSING_DATA_OUTPUT_KEY, 0)

                        loadingFrag_percent_textView.text = "$value%"
                    }
                    else -> {}
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()

        if(!isStarted) { // 로딩중에 앱을 나갔을 경우
            WorkManager.getInstance(requireContext())
                .cancelUniqueWork(Contents.WORKER_PARSING_DATA)
        }
    }

    private fun startWork() {
        if (!isStarted) {
            val parsingWork: OneTimeWorkRequest = OneTimeWorkRequestBuilder<ParsingWorker>()
                .addTag(Contents.WORKER_PARSING_DATA)
                .build()

            WorkManager.getInstance(requireContext()).enqueueUniqueWork(
                Contents.WORKER_PARSING_DATA,
                ExistingWorkPolicy.KEEP,
                parsingWork
            )
        }
    }

    // 애니메이션 설정
    private fun failedWorkAnimation() {
        with(loadingFrag_errorLayout) {
            animate().setDuration(200)
                .alpha(1f)
                .withLayer()

            restartBtn.isEnabled = true
        }
        with(loadingFrag_mainLayout) {
            animate().setDuration(200)
                .alpha(0f)
                .withLayer()
        }
        with(loadingFrag_explainText) {
            animate().setDuration(200)
                .alpha(0f)
                .withLayer()
        }
    }

    private fun startWorkAnimation() {
        with(loadingFrag_errorLayout) {
            animate().setDuration(200)
                .alpha(0f)
                .withLayer()

            restartBtn.isEnabled = false
        }
        with(loadingFrag_mainLayout) {
            animate().setDuration(200)
                .alpha(1f)
                .withLayer()
        }
        with(loadingFrag_explainText) {
            animate().setDuration(200)
                .alpha(1f)
                .withLayer()
        }
    }
    // 애니메이션 설정 끝
}