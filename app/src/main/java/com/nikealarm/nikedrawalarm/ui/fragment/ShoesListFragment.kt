package com.nikealarm.nikedrawalarm.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.core.view.doOnPreDraw
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.nikealarm.nikedrawalarm.BuildConfig
import com.nikealarm.nikedrawalarm.adapter.ShoesListAdapter
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.CustomTabsBuilder
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_shoes_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ShoesListFragment : Fragment(), ShoesListAdapter.ItemClickListener,
    NavigationView.OnNavigationItemSelectedListener, ShoesListAdapter.ImageClickListener {
    @Inject
    @Named(Contents.PREFERENCE_NAME_UPDATE)
    lateinit var updatePref: SharedPreferences

    private lateinit var drawer: DrawerLayout
    private lateinit var backToast: Toast

    private lateinit var mViewModel: MyViewModel
    private val FINISH_INTERVAL_TIME = 2000L
    private var backPressedTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        activity?.onBackPressedDispatcher?.addCallback(backPressedCallback)
        return inflater.inflate(R.layout.fragment_shoes_list, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바 설정
        val mToolbar = view.findViewById<Toolbar>(R.id.drawListFrag_toolbar).apply {
            (activity as MainActivity).setSupportActionBar(this)
        }

        // 인스턴스 설정
        mViewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        val mAdapter = ShoesListAdapter(
            requireContext()
        ).apply {
            setOnItemClickListener(this@ShoesListFragment)
            setOnImageClickListener(this@ShoesListFragment)
        }

        // 옵저버 설정
        mViewModel.shoesCategory.observe(viewLifecycleOwner, {
            mToolbar.title = it

            if (drawListFrag_scrollUp_Button.isEnabled) {
                disappearButton()
            }
        })
        mViewModel.shoesList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
            if (it.size == 0) {
                appearText()
            } else {
                if (drawListFrag_noItem_text.isEnabled) {
                    disappearText()
                }
            }
        })

        // id 설정
        val listView = view.findViewById<RecyclerView>(R.id.drawListFrag_listView).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter

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
        with(drawListFrag_scrollUp_Button) {
            isEnabled = false
            setOnClickListener {
                listView.smoothScrollToPosition(0)
            }
        }
        val navView = view.findViewById<NavigationView>(R.id.drawListFrag_navView).apply {
            setCheckedItem(R.id.mainMenu_released)
            setNavigationItemSelectedListener(this@ShoesListFragment)
        }
        drawer = view.findViewById(R.id.drawListFrag_drawer)

        // navigation 설정
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            drawer,
            mToolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        postponeEnterTransition()
        listView.doOnPreDraw {
            startPostponedEnterTransition()
        }

        showUpdate()
    }

    override fun onClickItem(newUrl: String?) {
        val builder = CustomTabsBuilder().getBuilder()
        with(builder) {
            ResourcesCompat.getDrawable(resources, R.drawable.ic_back, null)?.toBitmap()?.let { bitmap ->
                setCloseButtonIcon(bitmap)
            }

            build().launchUrl(requireContext(), Uri.parse(newUrl!!))
        }
    }

    override fun onClickImage(newUrl: String, shoesImageUrl: String, imageView: ImageView) {
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
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        drawer.closeDrawer(GravityCompat.START)

        return when (menuItem.itemId) {
            R.id.mainMenu_draw -> {
                setToolbarTitle(ShoesDataModel.CATEGORY_DRAW)
                true
            }
            R.id.mainMenu_comingSoon -> {
                setToolbarTitle(ShoesDataModel.CATEGORY_COMING_SOON)
                true
            }
            R.id.mainMenu_released -> {
                setToolbarTitle(ShoesDataModel.CATEGORY_RELEASED)
                true
            }
            R.id.mainMenu_upcoming -> {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(230)
                    findNavController().navigate(R.id.action_drawListFragment_to_upcomingListFragment)
                }
                true
            }
            R.id.mainMenu_setting -> {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(230)
                    findNavController().navigate(R.id.action_drawListFragment_to_settingFragment)
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
        super.onDestroyView()
        backPressedCallback.isEnabled = false
    }

    private fun setToolbarTitle(shoesCategory: String) {
        mViewModel.shoesCategory.value = shoesCategory
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
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

    private fun showUpdate() { // 업데이트 내용 보여줌
        val isFirst = updatePref.getBoolean(BuildConfig.VERSION_CODE.toString(), true)

        if(isFirst) {
            with(requireContext().getSharedPreferences(Contents.PREFERENCE_NAME_AUTO_ENTER, Context.MODE_PRIVATE).edit()) {
                clear()
                commit()
            }
            with(requireContext().getSharedPreferences(Contents.PREFERENCE_NAME_AUTO_ENTER_V2, Context.MODE_PRIVATE).edit()) {
                clear()
                commit()
            }

            findNavController().navigate(R.id.action_drawListFragment_to_updateDialog) // 다이얼로그 보여줌
            with(updatePref.edit()) { // 한번만 보여주게 함
                clear()
                putBoolean(BuildConfig.VERSION_CODE.toString(), false)
                commit()
            }
        }
    }

    // 애니메이션 설정
    private fun appearText() {
        with(drawListFrag_noItem_text) {
            isEnabled = true

            animate().setDuration(350)
                .alpha(1f)
                .withLayer()
        }
    }

    private fun disappearText() {
        with(drawListFrag_noItem_text) {
            isEnabled = false

            animate().setDuration(100)
                .alpha(0f)
                .withLayer()
        }
    }

    private fun appearButton() {
        with(drawListFrag_scrollUp_Button) {
            isEnabled = true
            animate().setDuration(100)
                .alpha(1f)
                .withLayer()
        }
    }

    private fun disappearButton() {
        with(drawListFrag_scrollUp_Button) {
            isEnabled = false
            animate().setDuration(100)
                .alpha(0f)
                .withLayer()
        }
    }
}