package com.nikealarm.nikedrawalarm.ui.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.ui.fragment.LoadingFragment
import kotlinx.android.synthetic.main.dialog_notification.*

class TerminationDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_notification, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 설정
        initView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        LoadingFragment.isOpened = false
        super.onDismiss(dialog)
    }

    private fun initView() {
        notifyDialogFrag_titleText.text = "종료" // 타이틀
        notifyDialogFrag_messageText.text = "정말 앱을 종료하시겠습니까?" // 내용

        notifyDialogFrag_checkButton.setOnClickListener {  // 확인
            activity?.finish()
        }
        notifyDialogFrag_cancelButton.setOnClickListener { // 취소
            dismiss()
        }
    }
}