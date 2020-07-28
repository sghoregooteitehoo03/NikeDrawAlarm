package com.nikealarm.nikedrawalarm.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.nikealarm.nikedrawalarm.adapter.ShoesListAdapter
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.component.ParsingWorker
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel

class ShoesListFragment : Fragment(), ShoesListAdapter.ItemClickListener,
    NavigationView.OnNavigationItemSelectedListener, ShoesListAdapter.ImageClickListener {
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var loadingLayout: ConstraintLayout
    private lateinit var drawer: DrawerLayout

    private lateinit var mViewModel: MyViewModel
    private val parsingWork: OneTimeWorkRequest = OneTimeWorkRequestBuilder<ParsingWorker>()
        .addTag(Contents.WORKER_PARSING_DATA)
        .build()

    companion object {
        var isStarted = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (!isStarted) {
            WorkManager.getInstance(requireContext()).enqueueUniqueWork(
                Contents.WORKER_PARSING_DATA,
                ExistingWorkPolicy.KEEP,
                parsingWork
            )
        }

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
        WorkManager.getInstance(requireContext())
            .getWorkInfosByTagLiveData(Contents.WORKER_PARSING_DATA)
            .observe(viewLifecycleOwner, Observer {
                if (it[0].state == WorkInfo.State.SUCCEEDED) {
                    isStarted = true
                    appearList()
                }
            })
        mViewModel.getShoesCategory().observe(viewLifecycleOwner, Observer {
            mToolbar.title = it
        })
        mViewModel.getShoesData().observe(viewLifecycleOwner, Observer {
            mAdapter.submitList(it)
        })

        // id 설정
        mainLayout = view.findViewById(R.id.drawListFrag_mainLayout)
        loadingLayout = view.findViewById<ConstraintLayout>(R.id.drawListFrag_loadingLayout).apply {
            setOnTouchListener { v, event ->
                true
            }
        }
        val listView = view.findViewById<RecyclerView>(R.id.drawListFrag_listView).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
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
    }

    override fun onClickItem(newUrl: String?) {
        mViewModel.setUrl(newUrl ?: "https://www.nike.com/kr/launch/?type=feed")
        findNavController().navigate(R.id.action_drawListFragment_to_mainWebFragment)
    }

    override fun onClickImage(newUrl: String) {
        mViewModel.setUrl(newUrl)
        findNavController().navigate(R.id.action_drawListFragment_to_imageListFragment)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        drawer.closeDrawer(GravityCompat.START)

        return when (menuItem.itemId) {
            R.id.mainMenu_draw -> {
                setToolbarTitle(menuItem)
                true
            }
            R.id.mainMenu_comingSoon -> {
                setToolbarTitle(menuItem)
                true
            }
            R.id.mainMenu_released -> {
                setToolbarTitle(menuItem)
                true
            }
            R.id.mainMenu_setting -> {
                findNavController().navigate(R.id.action_drawListFragment_to_settingFragment)
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

    private fun setToolbarTitle(menuItem: MenuItem) {
        mViewModel.setShoesCategory(menuItem.title.toString())
    }

    // 애니메이션 설정
    private fun appearList() {
        if (isStarted) {
            with(mainLayout) {
                animate().setDuration(200)
                    .alpha(1f)
                    .withLayer()
            }
            with(loadingLayout) {
                animate().setDuration(200)
                    .alpha(0f)
                    .withLayer()

                visibility = View.GONE
            }
        }
    }

    private fun disappearList() {

    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                super.setEnabled(false)
                activity?.onBackPressed()
            }
        }
    }
}