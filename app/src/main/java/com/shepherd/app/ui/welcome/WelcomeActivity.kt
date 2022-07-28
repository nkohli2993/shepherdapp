package com.shepherd.app.ui.welcome

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.shepherd.app.R
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.ActWelcomeBinding
import com.shepherd.app.ui.base.BaseActivity
import com.shepherd.app.ui.component.addLovedOne.AddLovedOneActivity
import com.shepherd.app.ui.component.createAccount.CreateNewAccountActivity
import com.shepherd.app.ui.component.joinCareTeam.JoinCareTeamActivity
import com.shepherd.app.ui.component.login.LoginActivity
import com.shepherd.app.ui.component.welcome.WelcomeViewModel
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.observe
import com.shepherd.app.utils.setupSnackbar
import com.shepherd.app.utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Deepak Rattan on 25-05-22
 */
@AndroidEntryPoint
class WelcomeActivity : BaseActivity(), View.OnClickListener {

    private val welcomeViewModel: WelcomeViewModel by viewModels()
    private lateinit var binding: ActWelcomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this
    }


    override fun initViewBinding() {
        binding = ActWelcomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        observe(welcomeViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(welcomeViewModel.showSnackBar)
        observeToast(welcomeViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
        when (status) {
            is Resource.Loading -> {
            }
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { welcomeViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnLogin -> {
                navigateToLogin()
            }

            R.id.btnCreateAccount -> {
                navigateToSignUp()
            }
        }
    }


    private fun navigateToAddLovedOneScreen() {
        startActivity<AddLovedOneActivity>()
    }

    private fun navigateToJoinCareTeamScreen() {
        startActivity<JoinCareTeamActivity>()
    }

    private fun navigateToLogin() {
        startActivity<LoginActivity>()
        /* val intent = Intent(this, LoginActivity::class.java)
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
         startActivity(intent)
         finish()*/
        //startActivityWithFinish<LoginActivity>()
    }

    private fun navigateToSignUp() {
        startActivity<CreateNewAccountActivity>()
        /* val intent = Intent(this, CreateNewAccountActivity::class.java)
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
         startActivity(intent)
         finish()*/
        //startActivityWithFinish<CreateNewAccountActivity>()
    }

}

