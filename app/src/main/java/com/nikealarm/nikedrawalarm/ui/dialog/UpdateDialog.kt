package com.nikealarm.nikedrawalarm.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nikealarm.nikedrawalarm.R
import kotlinx.android.synthetic.main.dialog_notification.*

class UpdateDialog : DialogFragment() {

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

        initView()
    }

    private fun initView() {
        notifyDialogFrag_titleText.text = "업데이트"
        notifyDialogFrag_messageText.text =
            "- 보안 업데이트\n (자동응모 정보 초기화 됨)\n"
        notifyDialogFrag_cancelButton.visibility = View.GONE
        notifyDialogFrag_checkButton.setOnClickListener {
            dismiss()
        }
    }
}