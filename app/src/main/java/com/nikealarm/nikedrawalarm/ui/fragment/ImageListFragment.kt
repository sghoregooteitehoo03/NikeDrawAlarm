package com.nikealarm.nikedrawalarm.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import androidx.work.*
import com.bumptech.glide.Glide
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.adapter.ImageListPagerAdapter
import com.nikealarm.nikedrawalarm.component.worker.GetImageWorker
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.fragment_image_list.*

class ImageListFragment : Fragment() {
    private lateinit var mViewModel: MyViewModel
    private lateinit var dots: Array<ImageView?>

    private lateinit var viewPager: ViewPager2
    private lateinit var sliderDotspanel: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.image_shared_element_transition)
        sharedElementReturnTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.image_shared_return_element_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]
        val getImageWork = OneTimeWorkRequestBuilder<GetImageWorker>()
            .addTag(Contents.WORKER_GET_IMAGE)
            .setInputData(workDataOf(Contents.WORKER_GET_IMAGE_INPUT_KEY to mViewModel.getUrl().value))
            .build()

        WorkManager.getInstance(requireContext())
            .enqueueUniqueWork(Contents.WORKER_GET_IMAGE, ExistingWorkPolicy.KEEP, getImageWork)

        activity?.onBackPressedDispatcher?.addCallback(backPressed)
        return inflater.inflate(R.layout.fragment_image_list, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // id 설정
        with(imageListFrag_firstImage) {
//            Picasso.get().load(mViewModel.shoesImageUrl.value).into(this)
            Glide.with(context).load(mViewModel.shoesImageUrl.value).into(this)
            transitionName = mViewModel.getUrl().value
        }
        viewPager = view.findViewById(R.id.imageListFrag_viewpager)
        sliderDotspanel = view.findViewById(R.id.imageListFrag_sliderDots)
        val cancelBtn = view.findViewById<ImageButton>(R.id.imageListFrag_cancelBtn).apply {
            setOnClickListener {
                exitFragment()
            }
        }

        // 옵저버 설정
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

                    viewPager.adapter = viewPagerAdapter
                    setData()
                }
            })
    }

    // 데이터가 준비 되었을 시
    private fun setData() {
        imageListFrag_firstImage.visibility = View.GONE
        imageListFrag_layout.visibility = View.VISIBLE
    }

    private fun resetData() {
        imageListFrag_firstImage.visibility = View.VISIBLE
        imageListFrag_layout.visibility = View.GONE
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

            sliderDotspanel.addView(dots[i], params)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

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
        findNavController().navigateUp()
    }

    private val backPressed = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            exitFragment()
        }

    }
}