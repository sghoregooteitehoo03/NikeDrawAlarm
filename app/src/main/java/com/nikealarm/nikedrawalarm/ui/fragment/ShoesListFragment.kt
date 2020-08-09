package com.nikealarm.nikedrawalarm.ui.fragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.nikealarm.nikedrawalarm.adapter.ShoesListAdapter
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.component.ParsingWorker
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.nikealarm.nikedrawalarm.ui.dialog.ExitDialog
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.fragment_shoes_list.*

class ShoesListFragment : Fragment(), ShoesListAdapter.ItemClickListener,
    NavigationView.OnNavigationItemSelectedListener, ShoesListAdapter.ImageClickListener {
    private lateinit var drawer: DrawerLayout

    private lateinit var mViewModel: MyViewModel

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
            requireContext(),
            requireActivity().supportFragmentManager
        ).apply {
            setOnItemClickListener(this@ShoesListFragment)
            setOnImageClickListener(this@ShoesListFragment)
        }

        // 옵저버 설정
        mViewModel.getShoesCategory().observe(viewLifecycleOwner, Observer {
            mToolbar.title = it

            if(drawListFrag_scrollUp_Button.isEnabled) {
                disappearButton()
            }
//            disappearButton()
        })
        mViewModel.getShoesData().observe(viewLifecycleOwner, Observer {
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
        return when (menuItem.itemId) {
            R.id.mainMenu_draw -> {
                drawer.closeDrawer(GravityCompat.START)

                setToolbarTitle(ShoesDataModel.CATEGORY_DRAW)
                true
            }
            R.id.mainMenu_comingSoon -> {
                drawer.closeDrawer(GravityCompat.START)

                setToolbarTitle(ShoesDataModel.CATEGORY_COMING_SOON)
                true
            }
            R.id.mainMenu_released -> {
                drawer.closeDrawer(GravityCompat.START)

                setToolbarTitle(ShoesDataModel.CATEGORY_RELEASED)
                true
            }
            R.id.mainMenu_setting -> {
                findNavController().navigate(R.id.action_drawListFragment_to_settingFragment)
                drawer.closeDrawer(GravityCompat.START)
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
        mViewModel.setShoesCategory(shoesCategory)
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                terminationApp()
            }
        }
    }

    private fun terminationApp() {
        ExitDialog.getExitDialog()
            .show(requireActivity().supportFragmentManager, ExitDialog.EXIT_DIALOG_TAG)
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