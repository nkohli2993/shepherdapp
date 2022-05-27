package com.app.shepherd.ui.component.login

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.google.android.material.snackbar.Snackbar
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.ActivityLoginBinding
import com.app.shepherd.databinding.ActivityLoginNewBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.ui.component.resetPassword.ResetPasswordActivity
import com.app.shepherd.utils.*
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
    }

    override fun initViewBinding() {
        binding = ActivityLoginNewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        observe(loginViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(loginViewModel.showSnackBar)
        observeToast(loginViewModel.showToast)
    }

    /*private fun doLogin() {
        loginViewModel.doLogin(
            this,
            binding.editTextEmail.text?.trim().toString(),
            binding.editTextPassword.text.toString()
        )
    }*/

    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {
                navigateToHomeScreen()
            }
            is Resource.DataError -> {
                status.errorCode?.let { loginViewModel.showToastMessage(it) }
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

            R.id.txtForgotPassword->{

            }
            R.id.btnLogin->{
                navigateToHomeScreen()
            }
            R.id.txtCreateAccount->{

            }
        }
    }

    private fun navigateToResetPasswordScreen() {
        startActivity<ResetPasswordActivity>()
    }

}
