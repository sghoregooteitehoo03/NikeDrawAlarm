package com.nikealarm.nikedrawalarm.ui.fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.databinding.FragmentSettingBinding
import com.nikealarm.nikedrawalarm.ui.MainActivity

class SettingFragment : Fragment(R.layout.fragment_setting) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 설정
        initView(view)
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

    private fun initView(view: View) {
        val binding = FragmentSettingBinding.bind(view)
        with(binding.mainToolbar) {
            (activity as MainActivity).setSupportActionBar(this)
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}