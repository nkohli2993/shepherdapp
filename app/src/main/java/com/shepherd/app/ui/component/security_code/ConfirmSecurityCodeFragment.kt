package com.shepherd.app.ui.component.security_code

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.poovam.pinedittextfield.PinField
import com.shepherd.app.R
import com.shepherd.app.data.dto.security_code.SendSecurityCodeRequestModel
import com.shepherd.app.databinding.FragmentConfirmSecurityCodeBinding
import com.shepherd.app.databinding.FragmentSecurityCodeBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.extensions.hideKeyboard
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.view_model.SecurityCodeViewModel
import org.jetbrains.annotations.NotNull

class ConfirmSecurityCodeFragment : BaseFragment<FragmentConfirmSecurityCodeBinding>(),
    View.OnClickListener {
    private val securityCodeViewModel: SecurityCodeViewModel by viewModels()
    private lateinit var fragmentConfirmSecurityCodeBinding: FragmentConfirmSecurityCodeBinding
    private var otp: String? = null
    private var code: String? = null
    private var type: String? = null
    private val args: ConfirmSecurityCodeFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentConfirmSecurityCodeBinding =
            FragmentConfirmSecurityCodeBinding.inflate(inflater, container, false)

        return fragmentConfirmSecurityCodeBinding.root
    }

    override fun observeViewModel() {
        securityCodeViewModel.addSecurityCodeLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(requireContext(), it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    when (type) {
                        Const.SET_SECURITY_CODE -> {
                            showInfo(
                                requireContext(),
                                getString(R.string.security_code_added_successfully)
                            )
                        }
                        Const.RESET_SECURITY_CODE -> {
                            showInfo(
                                requireContext(),
                                getString(R.string.security_code_reset_successfully)
                            )
                        }
                    }
                    backPress()
                }
            }
        }

    }


    override fun initViewBinding() {
        type = args.type
        code = args.code
        fragmentConfirmSecurityCodeBinding.listener = this
        fragmentConfirmSecurityCodeBinding.squareField.highlightPaintColor =
            ContextCompat.getColor(requireContext(), R.color._192032)
        fragmentConfirmSecurityCodeBinding.squareField.fieldColor =
            ContextCompat.getColor(requireContext(), R.color._192032)
        fragmentConfirmSecurityCodeBinding.squareField.onTextCompleteListener =
            object : PinField.OnTextCompleteListener {
                override fun onTextComplete(@NotNull enteredText: String): Boolean {
                    Toast.makeText(requireContext(), enteredText, Toast.LENGTH_SHORT).show()
                    return true
                }
            }

        editTextViewHandling()
        if (type != null) {
            when (type) {
                Const.SET_SECURITY_CODE -> {
                    fragmentConfirmSecurityCodeBinding.tvTitle.text =
                        getString(R.string.set_security_code)
                }
                Const.RESET_SECURITY_CODE -> {
                    fragmentConfirmSecurityCodeBinding.tvTitle.text =
                        getString(R.string.reset_security_code)
                }
            }
        }
    }

    private fun editTextViewHandling() {
        fragmentConfirmSecurityCodeBinding.etFirst.isLongClickable = false
        fragmentConfirmSecurityCodeBinding.etSecond.isLongClickable = false
        fragmentConfirmSecurityCodeBinding.etThird.isLongClickable = false
        fragmentConfirmSecurityCodeBinding.etFourth.isLongClickable = false
        fragmentConfirmSecurityCodeBinding.etFirst.doOnTextChanged { text, start, before, count ->
            if (text.toString().length == 1 && before == 0) {
                fragmentConfirmSecurityCodeBinding.etSecond.requestFocus()
            }

        }
        fragmentConfirmSecurityCodeBinding.etSecond.doOnTextChanged { text, start, before, count ->
            editTextHandling(
                text,
                before,
                count,
                fragmentConfirmSecurityCodeBinding.etFirst,
                fragmentConfirmSecurityCodeBinding.etThird
            )
        }
        fragmentConfirmSecurityCodeBinding.etThird.doOnTextChanged { text, start, before, count ->
            editTextHandling(
                text,
                before,
                count,
                fragmentConfirmSecurityCodeBinding.etSecond,
                fragmentConfirmSecurityCodeBinding.etFourth
            )
        }
        fragmentConfirmSecurityCodeBinding.etFourth.doOnTextChanged { text, start, before, count ->
            if (text.toString().length == 1 && before == 0) {
                hideKeyboard()
            } else if (count == 0) {
                fragmentConfirmSecurityCodeBinding.etThird.requestFocus()
            }
        }
    }

    private fun editTextHandling(
        text: CharSequence?,
        before: Int,
        count: Int,
        view: AppCompatEditText,
        viewSecond: AppCompatEditText
    ) {
        if (text.toString().length == 1 && before == 0) {
            viewSecond.requestFocus()
        } else if (count == 0) {
            view.requestFocus()
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_confirm_security_code
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.btnSaveChange -> {
                otp = fragmentConfirmSecurityCodeBinding.etFirst.text.toString()
                    .plus(fragmentConfirmSecurityCodeBinding.etSecond.text.toString())
                    .plus(fragmentConfirmSecurityCodeBinding.etThird.text.toString())
                    .plus(fragmentConfirmSecurityCodeBinding.etFourth.text.toString())
                if (otp!!.length == 4 && otp == code) {
                    when (type) {
                        Const.SET_SECURITY_CODE -> {
                            securityCodeViewModel.addSecurityCode(SendSecurityCodeRequestModel(otp))
                        }
                        Const.RESET_SECURITY_CODE -> {
                            securityCodeViewModel.resetSecurityCode(SendSecurityCodeRequestModel(otp))
                        }
                    }

                } else {
                    if (otp!!.length < 4) {
                        showError(requireContext(), getString(R.string.please_enter_confirm_code))
                    } else if (otp != code) {
                        showError(
                            requireContext(),
                            getString(R.string.entered_confrim_code_doesnot_matched_with_entered_code)
                        )
                    }
                }
            }
        }
    }
}