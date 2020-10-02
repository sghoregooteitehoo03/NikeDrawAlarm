package com.nikealarm.nikedrawalarm.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.adapter.SpecialShoesListAdapter
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.fragment_shoes_list.*
import kotlinx.android.synthetic.main.fragment_upcoming_list.*

class UpcomingListFragment : Fragment() {

    private lateinit var mViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_upcoming_list, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인스턴스 설정
        mViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        val mAdapter = SpecialShoesListAdapter(requireContext(), requireActivity().supportFragmentManager).apply {
            setHasStableIds(true)
        }
        val spinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("DEFAULT", "DRAW", "COMING"))

        // 옵저버 설정
        mViewModel.specialShoesList.observe(viewLifecycleOwner, Observer {
            mAdapter.submitList(it)

            if (it.size == 0) {
                appearText()
            } else {
                if (upcomingFrag_noitemText.isEnabled) {
                    disappearText()
                }
            }
        })

        // 뷰 설정
        with(upcomingFrag_toolbar) {
            (requireActivity() as MainActivity).setSupportActionBar(this)
            (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        with(upcomingFrag_spinner) {
            adapter = spinnerAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    (v as TextView).setTextColor(Color.WHITE)
                    mViewModel.upcomingCategory.value = when(pos) {
                        0 -> "DEFAULT"
                        1 -> ShoesDataModel.CATEGORY_DRAW
                        2 -> ShoesDataModel.CATEGORY_COMING_SOON
                        else -> "DEFAULT"
                    }

                    mAdapter.changeCategory()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {

                }
            }
        }
        with(upcomingFrag_list) {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//                    mAdapter.scrollClose()
//                }
//            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.upcomingCategory.value = "DEFAULT"
    }

    // 애니메이션 설정
    private fun appearText() {
        with(upcomingFrag_noitemText) {
            isEnabled = true

            animate().setDuration(350)
                .alpha(1f)
                .withLayer()
        }
    }

    private fun disappearText() {
        with(upcomingFrag_noitemText) {
            isEnabled = false

            animate().setDuration(100)
                .alpha(0f)
                .withLayer()
        }
    }
}