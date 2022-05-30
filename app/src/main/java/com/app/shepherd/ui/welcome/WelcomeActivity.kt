package com.app.shepherd.ui.component.welcome

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.ActWelcomeBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOne.AddLovedOneActivity
import com.app.shepherd.ui.component.joinCareTeam.JoinCareTeamActivity
import com.app.shepherd.ui.component.login.LoginActivity
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.observe
import com.app.shepherd.utils.setupSnackbar
import com.app.shepherd.utils.showToast
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
            /* R.id.imageViewBack -> {
                 finishActivity()
             }
             R.id.imageViewAddLovedOne -> {
                 navigateToAddLovedOneScreen()
             }
             R.id.imageViewJoinTeam -> {
                 navigateToJoinCareTeamScreen()
             }*/

            R.id.btnLogin -> {
                navigateToLogin()
            }

            R.id.btnCreateAccount -> {

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
    }

}

