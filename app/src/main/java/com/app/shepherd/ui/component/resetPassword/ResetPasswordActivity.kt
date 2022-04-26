package com.app.shepherd.ui.component.resetPassword

import CommonFunctions.font
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.ActivityResetPasswordBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.login.LoginActivity
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.observe
import com.app.shepherd.utils.setupSnackbar
import com.app.shepherd.utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_reset_password.*

@AndroidEntryPoint
class ResetPasswordActivity : BaseActivity(), View.OnClickListener {

    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()
    private lateinit var binding: ActivityResetPasswordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.listener = this
        setResetPasswordDescription()
    }


    override fun initViewBinding() {
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        observe(resetPasswordViewModel.resetPasswordLiveData, ::handleResetPasswordResult)
        observeSnackBarMessages(resetPasswordViewModel.showSnackBar)
        observeToast(resetPasswordViewModel.showToast)
    }

    private fun doResetPassword() {
        resetPasswordViewModel.doResetPassword(
            binding.editTextEmail.text?.trim().toString()
        )
    }


    private fun handleResetPasswordResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {
            }
            is Resource.DataError -> {
                status.errorCode?.let {
                    resetPasswordViewModel.showToastMessage(it)
                }
            }
        }
    }


    private fun setResetPasswordDescription() {
        textViewDescription.text =
            buildSpannedString {
                color(ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark)) {
                    font(getFont(applicationContext, R.font.poppins_regular)) {
                        append(getString(R.string.enter_your_email))
                    }
                }
                color(ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark)) {
                    font(getFont(applicationContext, R.font.poppins_medium)) {
                        append(getString(R.string.reset_password_description))
                    }
                }
            }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonReset -> {
                doResetPassword()
            }
            R.id.textViewLogin -> {
                navigateToLogin()
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun navigateToLogin() {
        finishActivity()
    }


}