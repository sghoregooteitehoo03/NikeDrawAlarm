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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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
class ReEditDialog : DialogFragment() {
    @Inject
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER)
    lateinit var autoEnterPreference: SharedPreferences

    private lateinit var mViewModel: MyViewModel
    private lateinit var imm: InputMethodManager

    private lateinit var setDataListener: ReEditSetDataListener

    interface ReEditSetDataListener {
        fun onSetDataListener()
    }

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

    private fun initView() {
        val id = autoEnterPreference.getString(Contents.AUTO_ENTER_ID, "")!!
        val password = autoEnterPreference.getString(Contents.AUTO_ENTER_PASSWORD, "")!!

        val spinnerAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                listOf(
                    "신발 사이즈 선택",
                    "240",
                    "245",
                    "250",
                    "255",
                    "260",
                    "265",
                    "270",
                    "275",
                    "280",
                    "285",
                    "290",
                    "295",
                    "300",
                    "310"
                )
            )
        with(editInfoDialogFrag_spinner) {
            adapter = spinnerAdapter

            val size = autoEnterPreference.getString(Contents.AUTO_ENTER_SIZE, "")!!
            setSelection(
                if (size.isEmpty()) {
                    0
                } else {
                    spinnerAdapter.getPosition(size)
                }
            )
        }

        editInfoDialogFrag_idEdit.setText(id)
        // EditText Key Action
        with(editInfoDialogFrag_passEdit) {
            setText(password)
            setOnEditorActionListener { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        setData()
                    }
                }
                true
            }
        }

        with(editInfoDialogFrag_checkButton) {
            text = "재시도"

            setOnClickListener {
                setData()
            }
        }
        editInfoDialogFrag_cancelButton.setOnClickListener {
            dismiss()
        }
    }

    // Preferences 저장
    private fun setData() {
        if (!checkIsEmpty()) {
            with(autoEnterPreference.edit()) {
                putString(Contents.AUTO_ENTER_ID, editInfoDialogFrag_idEdit.text.toString())
                putString(
                    Contents.AUTO_ENTER_PASSWORD,
                    editInfoDialogFrag_passEdit.text.toString()
                )
                putString(
                    Contents.AUTO_ENTER_SIZE,
                    editInfoDialogFrag_spinner.selectedItem as String
                )
                commit()
            }

            editInfoDialogFrag_idEdit.clearFocus()
            editInfoDialogFrag_passEdit.clearFocus()
            imm.hideSoftInputFromWindow(editInfoDialogFrag_idEdit.windowToken, 0)
            imm.hideSoftInputFromWindow(editInfoDialogFrag_passEdit.windowToken, 0)
        } else {
            if (editInfoDialogFrag_idEdit.text.toString().isEmpty()) { // id가 비어있을 때
                Toast.makeText(requireContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()

                editInfoDialogFrag_idLayout.requestFocus()
                imm.showSoftInput(editInfoDialogFrag_idEdit, 0)
            } else if (editInfoDialogFrag_passEdit.text.toString()
                    .isEmpty()
            ) { // password가 비어있을 때
                Toast.makeText(requireContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()

                editInfoDialogFrag_passLayout.requestFocus()
                imm.showSoftInput(editInfoDialogFrag_passEdit, 0)
            } else if (editInfoDialogFrag_spinner.selectedItemPosition == 0) { // 사이즈 선택 안했을 때
                Toast.makeText(requireContext(), "사이즈를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

//        setDataListener.onSetDataListener()
        mViewModel.retryEnter.value = true
        dismiss()
    }

    // 데이터 유효성 검사
    private fun checkIsEmpty(): Boolean {
        return !(editInfoDialogFrag_idEdit.text.toString()
            .isNotEmpty() && editInfoDialogFrag_passEdit.text.toString()
            .isNotEmpty() && editInfoDialogFrag_spinner.selectedItemPosition != 0)
    }

    fun setOnDataListener(_setDataListener: ReEditSetDataListener) {
        setDataListener = _setDataListener
    }
}