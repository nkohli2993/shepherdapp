package com.shepherdapp.app.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar
import com.shepherdapp.app.R
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.databinding.ActWelcomeBinding
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.addLovedOne.AddLovedOneActivity
import com.shepherdapp.app.ui.component.createAccount.CreateNewAccountActivity
import com.shepherdapp.app.ui.component.joinCareTeam.JoinCareTeamActivity
import com.shepherdapp.app.ui.component.login.LoginActivity
import com.shepherdapp.app.ui.component.welcome.WelcomeViewModel
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.utils.setupSnackbar
import com.shepherdapp.app.utils.showToast
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
//        binding.tvDescription.movementMethod = LinkMovementMethod.getInstance()
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
//        startActivity<LoginActivity>()
        //startActivityWithFinish<LoginActivity>()

        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("source", "WelcomeActivity")
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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

