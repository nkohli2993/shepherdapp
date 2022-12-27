package com.shepherdapp.app.ui.component.change_password

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.FragmentChangePasswordBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.ChangePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>(), View.OnClickListener {
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()
    private lateinit var fragmentChangePasswordBinding: FragmentChangePasswordBinding
    private var isPasswordShown = false
    private lateinit var homeActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            homeActivity = context
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentChangePasswordBinding =
            FragmentChangePasswordBinding.inflate(inflater, container, false)

        return fragmentChangePasswordBinding.root
    }

    override fun observeViewModel() {
        changePasswordViewModel.changePasswordResponseLiveData.observeEvent(this) {
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
//                    homeActivity.navigateToLoginScreen()
                    changePasswordViewModel.logOut()
                }
            }
        }


        // Observe Logout Response
        changePasswordViewModel.logoutResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(requireContext(), it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
//                    navigateToLoginScreen()
                    homeActivity.navigateToLoginScreen("ChangePassword")
                }
            }
        }
    }

    override fun initViewBinding() {
        fragmentChangePasswordBinding.listener = this

    }

    private fun showHidePassword(view: AppCompatImageView, editText: AppCompatEditText) {
        if (isPasswordShown) {
            //Hide Password
            editText.transformationMethod =
                PasswordTransformationMethod.getInstance()
            view.setImageResource(
                R.drawable.ic_eye
            )
        } else {
            //Show etConfirmPassword
            editText.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            view.setImageResource(
                R.drawable.ic_eye_on
            )
        }
        isPasswordShown = !isPasswordShown
        editText.setSelection(
            editText.length()
        )
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_change_password
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.btnSaveChange -> {
                if (isValid) {
                    changePasswordViewModel.changePassword(
                        fragmentChangePasswordBinding.etOldPassword.text.toString(),
                        fragmentChangePasswordBinding.etNewPassword.text.toString(),
                        fragmentChangePasswordBinding.etConfirmPassword.text.toString(),
                    )
                }
            }
            R.id.imageViewPasswordToggle -> {
                showHidePassword(
                    fragmentChangePasswordBinding.imageViewPasswordToggle,
                    fragmentChangePasswordBinding.etOldPassword
                )
            }
            R.id.newPasswordToggle -> {
                showHidePassword(
                    fragmentChangePasswordBinding.newPasswordToggle,
                    fragmentChangePasswordBinding.etNewPassword
                )
            }
            R.id.confirmPasswordToggle -> {
                showHidePassword(
                    fragmentChangePasswordBinding.confirmPasswordToggle,
                    fragmentChangePasswordBinding.etConfirmPassword
                )
            }
        }
    }

    private val isValid: Boolean
        get() {
            when {
                fragmentChangePasswordBinding.etOldPassword.text.toString().trim().isEmpty() -> {
                    fragmentChangePasswordBinding.etOldPassword.error = "Please enter old password"
                    fragmentChangePasswordBinding.etOldPassword.requestFocus()
                }
                fragmentChangePasswordBinding.etNewPassword.text.toString().trim().isEmpty() -> {
                    fragmentChangePasswordBinding.etNewPassword.error = "Please enter new password"
                    fragmentChangePasswordBinding.etNewPassword.requestFocus()
                }
                fragmentChangePasswordBinding.etConfirmPassword.text.toString().trim()
                    .isEmpty() -> {
                    fragmentChangePasswordBinding.etConfirmPassword.error =
                        "Please enter confirm password"
                    fragmentChangePasswordBinding.etConfirmPassword.requestFocus()
                }
                fragmentChangePasswordBinding.etNewPassword.length() < 4 -> {
                    fragmentChangePasswordBinding.etNewPassword.error =
                        getString(R.string.password_should_be_min_4_character)
                    fragmentChangePasswordBinding.etNewPassword.requestFocus()
                }
                fragmentChangePasswordBinding.etConfirmPassword.length() < 4 -> {
                    fragmentChangePasswordBinding.etConfirmPassword.error =
                        getString(R.string.password_should_be_min_4_character)
                    fragmentChangePasswordBinding.etConfirmPassword.requestFocus()
                }
                fragmentChangePasswordBinding.etNewPassword.text.toString()
                    .trim() != fragmentChangePasswordBinding.etConfirmPassword.text.toString()
                    .trim() -> {
                    fragmentChangePasswordBinding.etConfirmPassword.error =
                        getString(R.string.new_and_confirm_password_not_match)
                    fragmentChangePasswordBinding.etConfirmPassword.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }

}