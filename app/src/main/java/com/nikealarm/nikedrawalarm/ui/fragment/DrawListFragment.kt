package com.nikealarm.nikedrawalarm.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nikealarm.nikedrawalarm.adapter.DrawListAdapter
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel

class DrawListFragment : Fragment() {
    private lateinit var mViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return inflater.inflate(R.layout.fragment_draw_list, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인스턴스 설정
        val mSharedPreference = activity?.getPreferences(Context.MODE_PRIVATE)
        // 뷰모델 설정
        mViewModel = ViewModelProvider(this)[MyViewModel::class.java]

        val mAdapter = DrawListAdapter(
            requireContext(),
            mSharedPreference
        )
        mViewModel.getAllShoesPagingData().observe(viewLifecycleOwner, Observer {
            mAdapter.submitList(it)

            if(it.size == 0) {
                with(mSharedPreference?.edit()) {
                    this?.clear()
                    this?.commit()
                }
            }
        })

        // id 설정
        val listView = view.findViewById<RecyclerView>(R.id.drawListFrag_listView).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    // 메뉴설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.action_drawListFragment_to_settingScreenPreference)
                true
            }
            else -> false
        }
    }
}