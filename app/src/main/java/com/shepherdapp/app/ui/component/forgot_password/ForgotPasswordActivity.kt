package com.shepherdapp.app.ui.component.forgot_password

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.ActivityForgotPasswordBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.login.LoginActivity
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.ForgotPasswordViewModel
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
                        val builder = AlertDialog.Builder(this)
                        val dialog = builder.apply {
                            setTitle("Reset Password")
                            setMessage(it1)
                            setPositiveButton("OK") { _, _ ->
                                navigateToLoginScreen()
                            }
                        }.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
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
        //startActivityWithFinish<LoginActivity>()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open

    }
}