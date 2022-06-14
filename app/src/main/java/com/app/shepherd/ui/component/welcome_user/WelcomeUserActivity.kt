package com.app.shepherd.ui.component.welcome

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.app.shepherd.R
import com.app.shepherd.databinding.ActivityWelcomeUserBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOne.AddLovedOneActivity
import com.app.shepherd.ui.component.joinCareTeam.JoinCareTeamActivity
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.view_model.WelcomeUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_welcome_user.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class WelcomeUserActivity : BaseActivity(), View.OnClickListener {

    private val welcomeViewModel: WelcomeUserViewModel by viewModels()
    private lateinit var binding: ActivityWelcomeUserBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this

        // welcomeViewModel.getUser()
        welcomeViewModel.getUserDetails()
    }


    override fun initViewBinding() {
        binding = ActivityWelcomeUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        /* welcomeViewModel.loggedInUserLiveData.observeEvent(this) {
             val name = it?.firstname.toString().replaceFirstChar { it ->
                 it.uppercase()
             }

             //textViewTitle.text = "Hi " + "${it?.firstname.replaceFirstChar { it.uppercase() }}"
             textViewTitle.text = "Thanks, $name"
         }*/


        welcomeViewModel.userDetailsLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(this, it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    it.data.payload.let { payload ->
                        val firstName =
                            payload?.userProfiles?.firstname.toString()
                                .replaceFirstChar { name ->
                                    name.uppercase()
                                }

                        textViewTitle.text = "Thanks, $firstName"

                        // Save UserProfile info to SharedPreferences
                        welcomeViewModel.saveUser(payload?.userProfiles)
                    }
                }
            }
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> finishActivity()
            R.id.cardViewAddLovedOne -> navigateToAddLovedOneScreen()
            R.id.imageViewAddLovedOne -> navigateToAddLovedOneScreen()
            R.id.txtAdd -> navigateToAddLovedOneScreen()
            R.id.txtLovedOne -> navigateToAddLovedOneScreen()
            R.id.layoutAddLovedOne -> navigateToAddLovedOneScreen()
            R.id.cardViewCareTeam -> navigateToJoinCareTeamScreen()
            R.id.ivCareTeam -> navigateToJoinCareTeamScreen()
            R.id.txtJoin -> navigateToJoinCareTeamScreen()
            R.id.txtCareTeam -> navigateToJoinCareTeamScreen()
        }
    }


    private fun navigateToAddLovedOneScreen() {
        startActivity<AddLovedOneActivity>()
    }

    private fun navigateToJoinCareTeamScreen() {
        startActivity<JoinCareTeamActivity>()
    }

}

