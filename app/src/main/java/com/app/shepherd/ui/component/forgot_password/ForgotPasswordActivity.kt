package com.app.shepherd.ui.component.forgot_password

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.app.shepherd.R
import com.app.shepherd.databinding.ActivityForgotPasswordBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.login.LoginActivity
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.view_model.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_forgot_password.*

/**
 * Created by Deepak Rattan on 02/06/22
 */
@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()
    private val TAG = "ForgotPasswordActivity"

    //Handle Validation
    private val isValid: Boolean
        get() {
            when {
                binding.edtEmail.text.toString().isEmpty() -> {
                    binding.edtEmail.error = getString(R.string.please_enter_email_id)
                    binding.edtEmail.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.btnReset -> {
                if (isValid) {
                    forgotPasswordViewModel.forgotPassword(edtEmail.text.toString().trim())
                }
            }
        }
    }

    override fun observeViewModel() {
        forgotPasswordViewModel.forgotPasswordResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    Log.d(TAG, "Exception is : ${it.errorCode}")
//                    it.errorCode?.let { showError(this, it.toString()) }
                    it.message?.let { showError(this, it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    it.data.message?.let { it1 ->
                        // showSuccess(this, it1)
                        val alertDialog = AlertDialog.Builder(this)

                        alertDialog.apply {
                            setTitle("Reset Password")
                            setMessage(it1)
                            setPositiveButton("OK") { _, _ ->
                                navigateToLoginScreen()
                            }
                        }.create().show()
                    }
                }
            }
        }
    }

    override fun initViewBinding() {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun navigateToLoginScreen() {
        startActivityWithFinish<LoginActivity>()
    }
}