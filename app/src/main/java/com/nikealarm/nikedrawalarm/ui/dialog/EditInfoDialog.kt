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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.databinding.DialogEditinfoBinding
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.viewmodel.ShareDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class EditInfoDialog : DialogFragment() {
    @Inject
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER_V2)
    lateinit var autoEnterPreference: SharedPreferences

    private val mViewModel by activityViewModels<ShareDataViewModel>()
    private lateinit var imm: InputMethodManager
    private var fragmentBinding: DialogEditinfoBinding? = null

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
        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // 뷰 설정
        initView(view)
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (checkIsEmpty() || checkPreferences()) {
            mViewModel.allowAutoEnter.value = false
        }
        fragmentBinding = null

        super.onDismiss(dialog)
    }

    private fun initView(view: View) {
        val id = autoEnterPreference.getString(Contents.AUTO_ENTER_ID, "")!!
        val password = autoEnterPreference.getString(Contents.AUTO_ENTER_PASSWORD, "")!!

        val binding = DialogEditinfoBinding.bind(view)
        fragmentBinding = binding

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
        with(binding.sizeSpinner) {
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

        binding.idEdit.setText(id)
        // EditText Key Action
        with(binding.passEdit) {
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

        binding.checkBtn.setOnClickListener {
            setData()
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    // Preferences 저장
    private fun setData() {
        if (!checkIsEmpty()) {
            with(autoEnterPreference.edit()) {
                putString(Contents.AUTO_ENTER_ID, fragmentBinding?.idEdit?.text?.toString())
                putString(
                    Contents.AUTO_ENTER_PASSWORD,
                    fragmentBinding?.passEdit?.text?.toString()
                )
                putString(
                    Contents.AUTO_ENTER_SIZE,
                    fragmentBinding?.sizeSpinner?.selectedItem as String
                )
                commit()
            }

            Toast.makeText(requireContext(), "정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else {
            if (fragmentBinding?.idEdit?.text.toString().isEmpty()) { // id가 비어있을 때
                Toast.makeText(requireContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()

                fragmentBinding?.idLayout?.requestFocus()
                imm.showSoftInput(fragmentBinding?.idEdit, 0)
            } else if (fragmentBinding?.passEdit?.text.toString()
                    .isEmpty()
            ) { // password가 비어있을 때
                Toast.makeText(requireContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()

                fragmentBinding?.passLayout?.requestFocus()
                imm.showSoftInput(fragmentBinding?.passEdit, 0)
            } else if (fragmentBinding?.sizeSpinner?.selectedItemPosition == 0) { // 사이즈 선택 안했을 때
                Toast.makeText(requireContext(), "사이즈를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 데이터 유효성 검사
    private fun checkIsEmpty(): Boolean {
        return !(fragmentBinding?.idEdit?.text!!.toString()
            .isNotEmpty() && fragmentBinding?.passEdit?.text!!.toString()
            .isNotEmpty() && fragmentBinding?.sizeSpinner?.selectedItemPosition != 0)
    }

    private fun checkPreferences(): Boolean {
        return autoEnterPreference.getString(Contents.AUTO_ENTER_ID, "")!!.isEmpty()
    }
}