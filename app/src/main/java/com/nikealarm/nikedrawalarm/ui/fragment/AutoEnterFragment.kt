package com.nikealarm.nikedrawalarm.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.component.worker.AutoEnterWorker
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.CustomTabsBuilder
import com.nikealarm.nikedrawalarm.other.JavaScriptInterface
import com.nikealarm.nikedrawalarm.other.WebState
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auto_enter.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AutoEnterFragment : Fragment() {
    private lateinit var mViewModel: MyViewModel

    @Inject
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER_V2)
    lateinit var autoEnterPref: SharedPreferences
    private lateinit var textJob: Job

    private var isWorkStarted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.onBackPressedDispatcher?.addCallback(backPressedCallback)
        return inflater.inflate(R.layout.fragment_auto_enter, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인스턴스 설정
        mViewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]
        startWork()

        // 뷰 설정
        initView()

        // 옵저버 설정
        mViewModel.retryEnter.observe(viewLifecycleOwner, {
            if (it) {
                retry()
            }
        })
        WorkManager.getInstance(requireContext())
            .getWorkInfosByTagLiveData(Contents.WORKER_AUTO_ENTER)
            .observe(viewLifecycleOwner, {
                when (it[0].state) {
                    WorkInfo.State.SUCCEEDED -> { // 응모 성공 했을 시
                        success()
                    }
                    WorkInfo.State.FAILED -> { // 응모 실패 했을 시
                        if(isWorkStarted) {
                            val errorMsg =
                                it[0].outputData.getString(Contents.WORKER_AUTO_ENTER_OUTPUT_KEY)

                            if(errorMsg != null) {
                                fail(errorMsg)
                            }
                        }
                    }
                }
            })
    }

    override fun onDestroy() {
        if (textJob.isActive) {
            textJob.cancel()
        }
        super.onDestroy()
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            terminationApp()
        }
    }

    private fun startWork() {
        val url = activity?.intent?.getStringExtra(Contents.DRAW_URL)

        if (url != null) {
            val autoEnterWork = OneTimeWorkRequestBuilder<AutoEnterWorker>()
                .addTag(Contents.WORKER_AUTO_ENTER)
                .setInputData(workDataOf(Contents.WORKER_AUTO_ENTER_INPUT_KEY to url))
                .build()

            WorkManager.getInstance(requireContext())
                .enqueueUniqueWork(
                    Contents.WORKER_AUTO_ENTER,
                    ExistingWorkPolicy.KEEP,
                    autoEnterWork
                )

            isWorkStarted = true
        } else {
            fail(WebState.ERROR_OTHER)
        }
    }

    private fun initView() { // 뷰 설정
        textJob = CoroutineScope(Dispatchers.IO).launch {
            val stateText = autoEnterFrag_stateText.text.toString()
            var count = 0

            while (true) {
                delay(500)
                withContext(Dispatchers.Main) {
                    if (count < 3) {
                        autoEnterFrag_stateText.text =
                            autoEnterFrag_stateText.text.toString().plus(".")
                        count++
                    } else {
                        autoEnterFrag_stateText.text = stateText
                        count = 0
                    }
                }
            }
        }

        autoEnterFrag_reloadingButton.setOnClickListener { // 재시도 버튼
            findNavController().navigate(R.id.action_autoEnterFragment_to_reEditDialog)
        }
        autoEnterFrag_goManual_button.setOnClickListener {  // 직접 응모 버튼
            showWeb()
        }
        autoEnterFrag_exitButton.setOnClickListener { // 종료 버튼
            terminationApp()
        }
    }

    private fun terminationApp() {
        findNavController().navigate(R.id.terminationDialog)
    }

    private fun showWeb() {
        val url = requireActivity().intent.getStringExtra(Contents.DRAW_URL)
            ?: "https://www.nike.com/kr/launch/?type=feed"
        val builder = CustomTabsBuilder().getBuilder()

        with(builder) {
            build().launchUrl(requireContext(), Uri.parse(url))
        }
    }

    private fun success() { // 응모 성공
        textJob.cancel()
        animationSuccess()

//        activity?.finish()
    }

    private fun fail(errorMessage: String) { // 응모 실패
        textJob.cancel()
        autoEnterFrag_errorText.text = errorMessage

        when (errorMessage) {
            WebState.ERROR_LOGIN -> { // 로그인 오류 처리
                animationFailLoginOrSize()
            }
            WebState.ERROR_SIZE -> { // 사이즈 미존재
                animationFailLoginOrSize()
            }
            WebState.ERROR_END_DRAW -> { // Draw 종료
                animationEndDraw()
            }
            WebState.ERROR_OTHER -> { // 기타 오류
                animationFailOther()
            }
        }
    }

    private fun retry() { // 재시도
        startWork()
        animationRetry()
    }

    // 애니메이션 설정
    private fun animationSuccess() { // 응모 성공 애니메이션
        autoEnterFrag_progressBar.animate()
            .alpha(0f)
            .setDuration(200)
            .withLayer()
        with(autoEnterFrag_successImage) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        with(autoEnterFrag_successImage) {
                            setImageResource(R.drawable.ic_check)

                            val animation = drawable as AnimatedVectorDrawable
                            animation.start()
                        }
                    }
                })
                .withLayer()
        }

        autoEnterFrag_stateText.text = "응모 완료!"
    }

    private fun animationFailLoginOrSize() { // 로그인 실패 및 사이즈 미 존재 애니메이션
        autoEnterFrag_loadingLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    autoEnterFrag_loadingLayout.visibility = View.GONE
                }
            })
            .withLayer()
        with(autoEnterFrag_errorLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .setListener(null)
                .withLayer()
        }
    }

    private fun animationEndDraw() { // Draw 종료
        autoEnterFrag_loadingLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    autoEnterFrag_loadingLayout.visibility = View.GONE
                }
            })
            .withLayer()
        with(autoEnterFrag_errorLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .setListener(null)
                .withLayer()
        }

        autoEnterFrag_reloadingButton.visibility = View.GONE
        autoEnterFrag_goManual_button.visibility = View.GONE
    }

    private fun animationFailOther() { // 기타 오류
        autoEnterFrag_loadingLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    autoEnterFrag_loadingLayout.visibility = View.GONE
                }
            })
            .withLayer()
        with(autoEnterFrag_errorLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .setListener(null)
                .withLayer()
        }

        autoEnterFrag_reloadingButton.visibility = View.GONE
    }

    private fun animationRetry() { // 재시도
        with(autoEnterFrag_loadingLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .setListener(null)
                .withLayer()
        }
        autoEnterFrag_errorLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    autoEnterFrag_errorLayout.visibility = View.GONE
                }
            })
            .withLayer()
    }
    // 애니메이션 설정 끝
}