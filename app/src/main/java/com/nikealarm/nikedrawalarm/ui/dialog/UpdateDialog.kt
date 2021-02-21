package com.nikealarm.nikedrawalarm.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.databinding.DialogNotificationBinding

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

        initView(view)
    }

    private fun initView(view: View) {
        val binding = DialogNotificationBinding.bind(view)

        binding.titleText.text = "업데이트"
        // TODO: 업데이트 내용 채워넣기
        with(binding.messageText) {
            text =
                "- 자동응모 방식 변경\n( UPCOMING에서 DRAW 상품을 알림 설정해놓으시면 응모 당일 날 자동으로 응모가 진행됩니다. )\n\n- 데이터 못 읽어오는 버그 수정\n(알림을 다시 설정해주세요!)"
            textSize = 18f
            setPadding(12, 0, 12, 0)
        }
        binding.cancelBtn.visibility = View.GONE
        binding.checkBtn.setOnClickListener {
            dismiss()
        }
    }
}