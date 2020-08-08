package com.nikealarm.nikedrawalarm.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.nikealarm.nikedrawalarm.R
import kotlinx.android.synthetic.main.fragment_notification_dialog.*

class ExitDialog : DialogFragment() {

    companion object {
        const val EXIT_DIALOG_TAG = "EXIT_DIALOG_TAG"

        fun getExitDialog(): ExitDialog {
            return ExitDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return inflater.inflate(R.layout.fragment_notification_dialog, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 설정
        notifyDialogFrag_titleText.text = "종료"
        notifyDialogFrag_messageText.text = "앱을 종료하시겠습니까?"

        notifyDialogFrag_cancelButton.setOnClickListener {
            dismiss()
        }
        notifyDialogFrag_checkButton.setOnClickListener {
            requireActivity().finish()
        }
    }
}