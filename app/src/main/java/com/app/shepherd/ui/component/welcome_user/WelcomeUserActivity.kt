package com.app.shepherd.ui.component.welcome

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.ActivityWelcomeUserBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOne.AddLovedOneActivity
import com.app.shepherd.ui.component.joinCareTeam.JoinCareTeamActivity
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class WelcomeUserActivity : BaseActivity(), View.OnClickListener {

    private val welcomeViewModel: WelcomeUserViewModel by viewModels()
    private lateinit var binding: ActivityWelcomeUserBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolBarNew.listener = this
        binding.listener = this
    }


    override fun initViewBinding() {
        binding = ActivityWelcomeUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
       /* observe(welcomeViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(welcomeViewModel.showSnackBar)
        observeToast(welcomeViewModel.showToast)*/
    }


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
        when (status) {
            is Resource.Loading -> {}
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


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                finishActivity()
            }
            R.id.cardViewAddLovedOne -> {
                navigateToAddLovedOneScreen()
            }
            R.id.cardViewCareTeam -> {
                navigateToJoinCareTeamScreen()
            }
        }
    }


    private fun navigateToAddLovedOneScreen() {
        startActivity<AddLovedOneActivity>()
    }

    private fun navigateToJoinCareTeamScreen() {
        startActivity<JoinCareTeamActivity>()
    }

}

