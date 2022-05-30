package com.app.shepherd.ui.component.login

import android.os.Bundle
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
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.ui.component.resetPassword.ResetPasswordActivity
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this
        binding.viewModel = loginViewModel
    }

    override fun initViewBinding() {
        binding = ActivityLoginNewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    override fun observeViewModel() {
//        observe(loginViewModel.loginLiveData, ::handleLoginResult)
//        observeSnackBarMessages(loginViewModel.showSnackBar)
//        observeToast(loginViewModel.showToast)


//        observe(loginViewModel.loginResponseLiveData,::handleLoginResult)

        loginViewModel.loginResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    it.data.message?.let { it1 -> showSuccess(this, it1) }
                    navigateToHomeScreen()
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

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            /* R.id.buttonLogin -> {
                 //  doLogin()
                 navigateToHomeScreen()
             }
             R.id.textViewResetPassword -> {
                 navigateToResetPasswordScreen()
             }*/

            R.id.txtForgotPassword -> {

            }
            R.id.btnLogin -> {
                doLogin()


            }
            R.id.txtCreateAccount -> {

            }
        }
    }

    private fun doLogin() {
        loginViewModel.login()
    }

    private fun navigateToResetPasswordScreen() {
        startActivity<ResetPasswordActivity>()
    }

}
