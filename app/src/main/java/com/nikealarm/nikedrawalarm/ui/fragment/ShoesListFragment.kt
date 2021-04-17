package com.nikealarm.nikedrawalarm.ui.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.nikealarm.nikedrawalarm.adapter.ShoesListAdapter
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.databinding.FragmentShoesListBinding
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.CustomTabsBuilder
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.nikealarm.nikedrawalarm.viewmodel.shoes.ShoesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class ShoesListFragment : Fragment(R.layout.fragment_shoes_list),
    ShoesListAdapter.ItemClickListener,
    NavigationView.OnNavigationItemSelectedListener {

    private var fragmentBinding: FragmentShoesListBinding? = null
    private lateinit var shoesAdapter: ShoesListAdapter
    private val mViewModel by viewModels<ShoesViewModel>()

    private val FINISH_INTERVAL_TIME = 2000L
    private var backPressedTime = 0L
    private lateinit var backToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(backPressedCallback)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인스턴스 설정
        shoesAdapter = ShoesListAdapter().apply {
            setOnItemClickListener(this@ShoesListFragment)
        }

        initView(view) // 뷰 초기화
        setObserver() // 옵저버 설정

        checkUpdate() // 업데이트 확인
    }

    override fun onClickItem(pos: Int) {
        val builder = CustomTabsBuilder().getBuilder()
        val newUrl = shoesAdapter.currentList?.get(pos)
            ?.shoesUrl!!

        try {
            with(builder) {
                ResourcesCompat.getDrawable(resources, R.drawable.ic_back, null)?.toBitmap()
                    ?.let { bitmap ->
                        setCloseButtonIcon(bitmap)
                    }

                build().launchUrl(requireContext(), Uri.parse(newUrl))
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "크롬 브라우저가 존재하지 않습니다.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onClickImage(pos: Int, imageView: ImageView) {
        val shoesData = shoesAdapter.currentList?.get(pos)!!
        val newUrl = shoesData.shoesUrl!!
        val shoesImageUrl = shoesData.shoesImageUrl!!

        try {
            val directions = ShoesListFragmentDirections.actionDrawListFragmentToImageListFragment(
                newUrl,
                shoesImageUrl
            )
            val extras = FragmentNavigatorExtras(
                imageView to newUrl
            )
            findNavController().navigate(
                directions,
                extras
            )
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    // 공유하기
    override fun onClickShare(pos: Int) {
        val shoesData = shoesAdapter.currentList
            ?.get(pos)!!

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shoesData.shoesUrl!!)
        }

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        fragmentBinding?.drawer?.closeDrawer(GravityCompat.START)

        return when (menuItem.itemId) {
            R.id.mainMenu_draw -> { // DRAW
                setToolbarTitle(ShoesDataModel.CATEGORY_DRAW)
                true
            }
            R.id.mainMenu_comingSoon -> { // COMING SOON
                setToolbarTitle(ShoesDataModel.CATEGORY_COMING_SOON)
                true
            }
            R.id.mainMenu_released -> { // RELEASED
                setToolbarTitle(ShoesDataModel.CATEGORY_RELEASED)
                true
            }
            R.id.mainMenu_upcoming -> { // UPCOMING
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        delay(230)
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(R.id.action_drawListFragment_to_upcomingListFragment)
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
                true
            }
            R.id.mainMenu_helper -> { // HELPER
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        delay(230)
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(R.id.action_drawListFragment_to_helpFragment)
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
                true
            }
            R.id.mainMenu_setting -> { // SETTING
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        delay(230)
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(R.id.action_drawListFragment_to_settingFragment)
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
                true
            }
            else -> false
        }
    }

    override fun onResume() {
        super.onResume()
        backPressedCallback.isEnabled = true
    }

    override fun onDestroyView() {
        fragmentBinding = null
        backPressedCallback.isEnabled = false
        super.onDestroyView()
    }

    private fun initView(view: View) { // 뷰 설정
        val binding = FragmentShoesListBinding.bind(view)
        fragmentBinding = binding

        // 툴바 설정
        with(binding.mainToolbar) {
            (activity as MainActivity).setSupportActionBar(this)
        }

        // 리스트 설정
        with(binding.shoesList) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = shoesAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (recyclerView.computeVerticalScrollOffset() == 0) {
                        disappearButton()
                    } else {
                        appearButton()
                    }
                }
            })
        }
        // 스크롤 업 버튼
        with(binding.scrollUpBtn) {
            isEnabled = false

            setOnClickListener {
                binding.shoesList.smoothScrollToPosition(0)
            }
        }
        // Navigation view
        with(binding.navView) {
            setCheckedItem(R.id.mainMenu_released)
            setNavigationItemSelectedListener(this@ShoesListFragment)
        }
        // Drawer 설정
        with(binding.drawer) {
            val toggle = ActionBarDrawerToggle(
                requireActivity(),
                this,
                binding.mainToolbar,
                R.string.open_drawer,
                R.string.close_drawer
            )

            addDrawerListener(toggle)
            toggle.syncState()
        }

        postponeEnterTransition()
        binding.shoesList.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun setObserver() { // 옵저버 설정
        mViewModel.shoesCategory.observe(viewLifecycleOwner, {
            fragmentBinding?.mainToolbar?.title = it

            if (fragmentBinding?.scrollUpBtn!!.isEnabled) {
                disappearButton()
            }
        })
        mViewModel.shoesList.observe(viewLifecycleOwner, {
            shoesAdapter.submitList(it)
            if (it.size == 0) {
                appearText()
            } else {
                if (fragmentBinding?.noItemText!!.isEnabled) {
                    disappearText()
                }
            }
        })
    }

    private fun setToolbarTitle(shoesCategory: String) {
        mViewModel.shoesCategory.value = shoesCategory
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            if (fragmentBinding?.drawer!!.isDrawerOpen(GravityCompat.START)) {
                fragmentBinding?.drawer?.closeDrawer(GravityCompat.START)
            } else {
                val tempTime = System.currentTimeMillis()
                val intervalTime = tempTime - backPressedTime

                if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                    requireActivity().finish()
                    backToast.cancel()
                } else {
                    backPressedTime = tempTime
                    backToast = Toast.makeText(
                        requireContext(),
                        "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.",
                        Toast.LENGTH_SHORT
                    ).apply {
                        show()
                    }
                }
            }
        }
    }

    private fun checkUpdate() {
        if (mViewModel.isUpdated()) {
            with(
                requireContext().getSharedPreferences(
                    Contents.PREFERENCE_NAME_AUTO_ENTER,
                    Context.MODE_PRIVATE
                ).edit()
            ) {
                clear()
                commit()
            }

            findNavController().navigate(R.id.action_drawListFragment_to_updateDialog) // 다이얼로그 보여줌
            mViewModel.afterUpdate()
        }
    }

    // 애니메이션 설정
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

    private fun appearButton() {
        with(fragmentBinding?.scrollUpBtn!!) {
            isEnabled = true
            animate().setDuration(100)
                .alpha(1f)
                .withLayer()
        }
    }

    private fun disappearButton() {
        with(fragmentBinding?.scrollUpBtn!!) {
            isEnabled = false
            animate().setDuration(100)
                .alpha(0f)
                .withLayer()
        }
    }
}