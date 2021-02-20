package com.nikealarm.nikedrawalarm.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.component.worker.ParsingWorker
import com.nikealarm.nikedrawalarm.databinding.FragmentLoadingBinding
import com.nikealarm.nikedrawalarm.other.Contents

class LoadingFragment : Fragment(R.layout.fragment_loading) {
    private var fragmentBinding: FragmentLoadingBinding? = null

    companion object {
        var isOpened = false
        private var isStarted = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startWork()
        activity?.onBackPressedDispatcher?.addCallback(backPressedCallback)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 설정
        initView(view)

        // 옵저버 설정
        setObserver()
    }

    override fun onDestroy() {
        fragmentBinding = null
        if (!isStarted) { // 로딩중에 앱을 나갔을 경우
            WorkManager.getInstance(requireContext())
                .cancelUniqueWork(Contents.WORKER_PARSING_DATA)
        }
        super.onDestroy()
    }

    private fun initView(view: View) { // 뷰 초기화
        val binding = FragmentLoadingBinding.bind(view)
        fragmentBinding = binding

        binding.restartBtn.setOnClickListener {
            startWorkAnimation()
            startWork()
        }
        binding.exitBtn.setOnClickListener {
            terminationApp()
        }
    }

    private fun setObserver() { // 옵저버 설정
        WorkManager.getInstance(requireContext())
            .getWorkInfosByTagLiveData(Contents.WORKER_PARSING_DATA)
            .observe(viewLifecycleOwner, Observer {
                when (it[0].state) {
                    WorkInfo.State.SUCCEEDED -> { // 로딩 성공 시
                        isStarted = true

                        if (isOpened) {
                            findNavController().navigateUp()
                        }
                        findNavController().navigate(R.id.action_loadingFragment_to_drawListFragment)
                    }
                    WorkInfo.State.FAILED -> { // 로딩 실패 시
                        failedWorkAnimation()
                    }
                    WorkInfo.State.RUNNING -> { // 로딩 중
                        val progress = it[0].progress
                        val value = progress.getInt(Contents.WORKER_PARSING_DATA_OUTPUT_KEY, 0)

                        fragmentBinding?.percentText?.text = "$value%"
                    }
                    else -> {
                    }
                }
            })
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            terminationApp()
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

    private fun terminationApp() {
        isOpened = true
        findNavController().navigate(R.id.terminationDialog)
    }

    // 애니메이션 설정
    private fun failedWorkAnimation() {
        with(fragmentBinding?.errorLayout!!) {
            animate().setDuration(200)
                .alpha(1f)
                .withLayer()

            fragmentBinding?.restartBtn?.isEnabled = true
        }
        with(fragmentBinding?.mainLayout!!) {
            animate().setDuration(200)
                .alpha(0f)
                .withLayer()
        }
        with(fragmentBinding?.explainText!!) {
            animate().setDuration(200)
                .alpha(0f)
                .withLayer()
        }
    }

    private fun startWorkAnimation() {
        with(fragmentBinding?.errorLayout!!) {
            animate().setDuration(200)
                .alpha(0f)
                .withLayer()

            fragmentBinding?.restartBtn?.isEnabled = false
        }
        with(fragmentBinding?.mainLayout!!) {
            animate().setDuration(200)
                .alpha(1f)
                .withLayer()
        }
        with(fragmentBinding?.explainText!!) {
            animate().setDuration(200)
                .alpha(1f)
                .withLayer()
        }
    }
    // 애니메이션 설정 끝
}