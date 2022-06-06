package com.app.shepherd.ui.component.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.ActivityLoginNewBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.createAccount.CreateNewAccountActivity
import com.app.shepherd.ui.component.forgot_password.ForgotPasswordActivity
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.ui.component.welcome.WelcomeUserActivity
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.extensions.showSuccess
import com.app.shepherd.utils.setupSnackbar
import com.app.shepherd.utils.showToast
import com.app.shepherd.view_model.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Sumit Kumar
 */
@AndroidEntryPoint
class LoginActivity : BaseActivity(), View.OnClickListener {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginNewBinding
    private var isPasswordShown = false


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolBar.listener = this
        binding.listener = this
        binding.viewModel = loginViewModel

        // Handle the click of Show or Hide Password Icon
        binding.imageViewPasswordToggle.setOnClickListener {
            if (isPasswordShown) {
                //Hide Password
                binding.edtPasswd.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imageViewPasswordToggle.setImageResource(R.drawable.ic_eye)
            } else {
                //Show password
                binding.edtPasswd.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.imageViewPasswordToggle.setImageResource(R.drawable.ic_eye_on)
            }
            isPasswordShown = !isPasswordShown
            binding.edtPasswd.setSelection(binding.edtPasswd.length())
        }


    }

    override fun initViewBinding() {
        binding = ActivityLoginNewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    override fun observeViewModel() {
        loginViewModel.loginResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
//                    it.data.message?.let { it1 -> showSuccess(this, it1) }
                    it.data.let { it ->
                        it.message?.let { it1 -> showSuccess(this, it1) }
                        // Save User Detail to SharedPref
                        it.payload?.userProfile?.let { it1 -> loginViewModel.saveUser(it1) }
                    }
                    navigateToWelcomeUserScreen()
                }

                is DataResult.Failure -> {
                    //handleAPIFailure(it.message, it.errorCode)

                    hideLoading()
                    it.errorCode?.let { showError(this, it.toString()) }

                }
            }

        }
    }

    /*private fun doLogin() {
        loginViewModel.doLogin(
            this,
            binding.editTextEmail.text?.trim().toString(),
            binding.editTextPassword.text.toString()
        )
    }*/

    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
        when (status) {
            is Resource.Loading -> {
            }
            is Resource.Success -> status.data?.let {
                navigateToHomeScreen()
            }
            is Resource.DataError -> {
//                status.errorCode?.let { loginViewModel.showToastMessage(it) }
            }
        }
    }

    private fun navigateToHomeScreen() {
        startActivityWithFinish<HomeActivity>()
    }

    private fun navigateToWelcomeUserScreen() {
        startActivity<WelcomeUserActivity>()
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtForgotPassword -> {
                navigateToForgotPasswordScreen()
            }
            R.id.btnLogin -> {
                doLogin()
            }
            R.id.txtCreateAccount -> {
                navigateToCreateNewAccountScreen()
            }
            R.id.imgBack -> {
                onBackPressed()
            }
        }
    }

    private fun navigateToCreateNewAccountScreen() {
        startActivity<CreateNewAccountActivity>()
    }

    private fun navigateToForgotPasswordScreen() {
        startActivity<ForgotPasswordActivity>()
    }

    private fun doLogin() {
        loginViewModel.login()
    }


}
