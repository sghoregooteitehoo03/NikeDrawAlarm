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
        explainText = view.findViewById(R.id.loadingFrag_explainText)
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
                if (it[0].state == WorkInfo.State.SUCCEEDED) {
                    isStarted = true
                    findNavController().navigate(R.id.action_loadingFragment_to_drawListFragment)
                } else if(it[0].state == WorkInfo.State.FAILED) {
                    failedWorkAnimation()
                }
            })
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
        with(explainText) {
            animate().setDuration(200)
                .alpha(1f)
                .withLayer()
        }
        with(restartBtn) {
            animate().setDuration(200)
                .alpha(1f)
                .withLayer()

            isEnabled = true
        }
        with(progressBar) {
            animate().setDuration(200)
                .alpha(0f)
                .withLayer()
        }
    }

    private fun startWorkAnimation() {
        with(explainText) {
            animate().setDuration(200)
                .alpha(0f)
                .withLayer()
        }
        with(restartBtn) {
            animate().setDuration(200)
                .alpha(0f)
                .withLayer()

            isEnabled = false
        }
        with(progressBar) {
            animate().setDuration(200)
                .alpha(1f)
                .withLayer()
        }
    }
}