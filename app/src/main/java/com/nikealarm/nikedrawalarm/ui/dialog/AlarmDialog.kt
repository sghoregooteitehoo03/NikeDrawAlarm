package com.nikealarm.nikedrawalarm.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nikealarm.nikedrawalarm.R
import kotlinx.android.synthetic.main.fragment_notification_dialog.*

class AlarmDialog : DialogFragment() {

    companion object {
        const val ALARM_DIALOG_TAG = "ALARM_DIALOG_TAG"

        private lateinit var mListener: CheckClickListener

        private lateinit var title: String
        private lateinit var message: String

        fun setOnCheckClickListener(_listener: CheckClickListener) {
            mListener = _listener
        }

        fun getAlarmDialog(_title: String, _message: String): DialogFragment {
            title = _title
            message = _message

            return AlarmDialog()
        }
    }

    interface CheckClickListener {
        fun onCheckClickListener(dialog: Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.fragment_notification_dialog, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 설정
        notifyDialogFrag_titleText.text = title
        notifyDialogFrag_messageText.text = message

        notifyDialogFrag_cancelButton.setOnClickListener {
            dialog?.dismiss()
        }
        notifyDialogFrag_checkButton.setOnClickListener {
            mListener.onCheckClickListener(requireDialog())
        }
    }
}