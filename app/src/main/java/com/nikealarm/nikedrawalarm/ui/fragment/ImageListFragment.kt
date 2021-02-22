package com.nikealarm.nikedrawalarm.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import androidx.work.*
import com.bumptech.glide.Glide
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.adapter.ImageListPagerAdapter
import com.nikealarm.nikedrawalarm.component.worker.GetImageWorker
import com.nikealarm.nikedrawalarm.databinding.FragmentImageListBinding
import com.nikealarm.nikedrawalarm.other.Contents

class ImageListFragment : Fragment(R.layout.fragment_image_list) {
    private lateinit var dots: Array<ImageView?>
    private val args: ImageListFragmentArgs by navArgs()
    private var fragmentBinding: FragmentImageListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.image_shared_element_transition)
        sharedElementReturnTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.image_shared_return_element_transition)

        startWork()
        activity?.onBackPressedDispatcher?.addCallback(backPressed)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 설정
        initView(view)
        // 옵저버 설정
        setObserver()
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

    private fun initView(view: View) { // 뷰 설정
        val binding = FragmentImageListBinding.bind(view)
        fragmentBinding = binding

        with(binding.firstImage) {
            Glide.with(context).load(args.shoesImageUrl).into(this)
            transitionName = args.shoesUrl
        }

        binding.cancelButton.setOnClickListener {
            exitFragment()
        }
    }

    private fun setObserver() { // 옵저버 설정
        WorkManager.getInstance(requireContext())
            .getWorkInfosByTagLiveData(Contents.WORKER_GET_IMAGE)
            .observe(viewLifecycleOwner, Observer {
                if (it[0].state == WorkInfo.State.SUCCEEDED) {
                    val imageList =
                        it[0].outputData.getStringArray(Contents.WORKER_GET_IMAGE_OUTPUT_KEY)
                    val dotsCount = imageList!!.size

                    Log.i("Check", "${imageList.size}")
                    setDots(dotsCount)
                    val viewPagerAdapter = ImageListPagerAdapter(imageList as Array<String>)

                    fragmentBinding?.imageViewPager?.adapter = viewPagerAdapter
                    setData()
                }
            })
    }

    private fun startWork() { // 작업 시작
        val getImageWork = OneTimeWorkRequestBuilder<GetImageWorker>()
            .addTag(Contents.WORKER_GET_IMAGE)
            .setInputData(workDataOf(Contents.WORKER_GET_IMAGE_INPUT_KEY to args.shoesUrl))
            .build()

        WorkManager.getInstance(requireContext())
            .enqueueUniqueWork(Contents.WORKER_GET_IMAGE, ExistingWorkPolicy.KEEP, getImageWork)
    }

    // 데이터가 준비 되었을 시
    private fun setData() {
        fragmentBinding?.firstImage?.visibility = View.GONE
        fragmentBinding?.mainLayout?.visibility = View.VISIBLE
    }

    private fun resetData() {
        fragmentBinding?.firstImage?.visibility = View.VISIBLE
        fragmentBinding?.mainLayout?.visibility = View.GONE
    }

    private fun setDots(size: Int) {
        dots = arrayOfNulls(size)

        for (i in 0 until size) {
            dots[i] = ImageView(requireContext())
            dots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.non_active_dot_shape
                )
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0)
            }

            fragmentBinding?.sliderDots?.addView(dots[i], params)
        }

        fragmentBinding?.imageViewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for (i in 0 until size) {
                    dots[i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.non_active_dot_shape
                        )
                    )
                }

                dots[position]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.active_dot_shape
                    )
                )
            }
        })
    }

    private fun exitFragment() {
        backPressed.isEnabled = false

        resetData()
        try {
            findNavController().navigateUp()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private val backPressed = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            exitFragment()
        }

    }
}