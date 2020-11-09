package com.nikealarm.nikedrawalarm.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_editinfo.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class EditInfoDialog : DialogFragment() {
    @Inject
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER)
    lateinit var autoEnterPreference: SharedPreferences

    private lateinit var mViewModel: MyViewModel
    private lateinit var imm: InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_editinfo, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인스턴스 설정
        mViewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]
        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // 뷰 설정
        initView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(checkIsEmpty()) {
            mViewModel.allowAutoEnter.value = false
        }
    }

    private fun initView() {
        val id = autoEnterPreference.getString(Contents.AUTO_ENTER_ID, "")!!
        val password = autoEnterPreference.getString(Contents.AUTO_ENTER_PASSWORD, "")!!

        editInfoDialogFrag_idEdit.setText(id)
        editInfoDialogFrag_passEdit.setText(password)

        editInfoDialogFrag_checkButton.setOnClickListener {
            if(!checkIsEmpty()) {
                with(autoEnterPreference.edit()) {
                    putString(Contents.AUTO_ENTER_ID, editInfoDialogFrag_idEdit.text.toString())
                    putString(Contents.AUTO_ENTER_PASSWORD, editInfoDialogFrag_passEdit.text.toString())
                    commit()
                }

                Toast.makeText(requireContext(), "아이디 비밀번호가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                imm.hideSoftInputFromWindow(editInfoDialogFrag_idEdit.windowToken, 0)
                imm.hideSoftInputFromWindow(editInfoDialogFrag_passEdit.windowToken, 0)
            } else {
                Toast.makeText(requireContext(), "아이디 및 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        editInfoDialogFrag_cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun checkIsEmpty(): Boolean {
        // 텍스트가 안 비어있을 때
        return !(editInfoDialogFrag_idEdit.text.toString()
            .isNotEmpty() && editInfoDialogFrag_passEdit.text.toString().isNotEmpty())
    }
}