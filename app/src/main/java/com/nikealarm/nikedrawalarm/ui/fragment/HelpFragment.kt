package com.nikealarm.nikedrawalarm.ui.fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.adapter.ExplainImagePagerAdapter
import com.nikealarm.nikedrawalarm.databinding.FragmentHelpBinding
import com.nikealarm.nikedrawalarm.ui.MainActivity
import kotlin.math.abs

class HelpFragment : Fragment(R.layout.fragment_help) {
    private var fragmentBinding: FragmentHelpBinding? = null
    private var pagerAdapter: ExplainImagePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(backPressedCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        initView(view)
    }

    override fun onDestroyView() {
        backPressedCallback.isEnabled = false
        fragmentBinding = null
        super.onDestroyView()
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

    private fun initView(view: View) {
        val binding = FragmentHelpBinding.bind(view)
        fragmentBinding = binding

        // 툴바
        with(binding.mainToolbar) {
            (activity as MainActivity).setSupportActionBar(this)
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        // 상품 알림
        binding.setAlarmLayout.setOnClickListener {
            val text =
                "1. UPCOMING을 누릅니다.\n2. 상품을 오른쪽으로 스와이프 합니다.\n3. 종 모양 아이콘을 눌러 알람을 설정/해제를 합니다.\n4. 출시 당일 알림을 받으실 수 있습니다."
            val imageList = listOf(
                R.drawable.set_alarm1,
                R.drawable.set_alarm2,
                R.drawable.set_alarm3,
                R.drawable.set_alarm4
            )

            setData(binding.setAlarmText.text.toString(), text, imageList)
        }

        // 드로우 정보
        binding.getDrawInfoLayout.setOnClickListener {
            val text =
                "1. 설정을 누릅니다.\n2. DRAW 정보 받기를 활성화 합니다.\n3. 앱이 꺼져있는 상태에서도 새로운 DRAW 정보를 받으실 수 있습니다."
            val imageList = listOf(
                R.drawable.get_draw1,
                R.drawable.get_draw2,
                R.drawable.get_draw3
            )
            setData(binding.getDrawInfoText.text.toString(), text, imageList)
        }

        // 자동응모
        binding.useAutoLayout.setOnClickListener {
            val text =
                "1. 설정을 누릅니다.\n2. 자동응모 허용을 누른 후 자동응모에 이용할 정보를 입력합니다.\n3. 메인화면으로 돌아가 UPCOMING을 누릅니다.\n" +
                        "4. 원하는 DRAW 상품을 알림설정을 합니다.\n5. 응모 당일 자동으로 응모가 되는것을 확인할 수 있습니다."
            val imageList = listOf(
                R.drawable.get_draw1,
                R.drawable.use_auto1,
                R.drawable.set_alarm1,
                R.drawable.use_auto2,
                R.drawable.use_auto3
            )
            setData(binding.useAutoText.text.toString(), text, imageList)
        }

        // 앱 실행 x
        binding.noLaunchAppLayout.setOnClickListener {
            val text = "1. 앱 아이콘을 꾹 눌러 앱 정보를 누릅니다.\n2. 저장공간을 누릅니다.\n3. 데이터 삭제를 누른 후 앱을 재실행 합니다."
            val imageList = listOf(
                R.drawable.no_launch1,
                R.drawable.no_launch2,
                R.drawable.no_launch3,
                R.drawable.no_launch4
            )
            setData(binding.noLaunchAppText.text.toString(), text, imageList)
        }
    }

    private fun setData(title: String, text: String, imageList: List<Int>) {
        fragmentBinding!!.mainLayout.visibility = View.GONE
        fragmentBinding!!.explainLayout.visibility = View.VISIBLE

        fragmentBinding!!.titleText.text = title
        fragmentBinding!!.explainText.text = text

        with(fragmentBinding!!.explainPager) {
            pagerAdapter = ExplainImagePagerAdapter(imageList)

            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 1
            getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

            val transformer = CompositePageTransformer()
            transformer.addTransformer(MarginPageTransformer(4))
            transformer.addTransformer(ViewPager2.PageTransformer { page, position ->
                val v = 1 - abs(position)
                page.scaleY = (0.8f + v * 0.2f)
            })

            setPageTransformer(transformer)
            adapter = pagerAdapter
        }

        backPressedCallback.isEnabled = true
    }

    private fun resetData() {
        pagerAdapter = null
        fragmentBinding!!.explainPager.adapter = null

        fragmentBinding!!.mainLayout.visibility = View.VISIBLE
        fragmentBinding!!.explainLayout.visibility = View.GONE

        fragmentBinding!!.titleText.text = ""
        fragmentBinding!!.explainText.text = ""

        backPressedCallback.isEnabled = false
    }

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            resetData()
        }
    }
}