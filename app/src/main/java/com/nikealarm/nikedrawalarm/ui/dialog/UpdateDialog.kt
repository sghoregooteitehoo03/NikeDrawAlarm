package com.nikealarm.nikedrawalarm.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginLeft
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
        with(notifyDialogFrag_messageText) {
            text =
                "- 자동응모 방식 변경\n( UPCOMING에서 DRAW 상품을 알림 설정해놓으시면 응모 당일 날 자동으로 응모가 진행됩니다. )"
            textSize = 18f
            setPadding(12, 0, 12, 0)
        }
        notifyDialogFrag_cancelButton.visibility = View.GONE
        notifyDialogFrag_checkButton.setOnClickListener {
            dismiss()
        }
    }
}