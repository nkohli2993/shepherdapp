package com.app.shepherd.ui.component.welcome

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import com.app.shepherd.R
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.dto.user_detail.Payload
import com.app.shepherd.databinding.ActivityWelcomeUserBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOne.AddLovedOneActivity
import com.app.shepherd.ui.component.joinCareTeam.JoinCareTeamActivity
import com.app.shepherd.ui.component.login.LoginActivity
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Prefs
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

                        // Save Payload to shared Pref
                        welcomeViewModel.savePayload(payload)

                        //Save User Role
                        /*payload?.userLovedOne?.get(0)?.careRoles?.name.let {
                            it?.let { it1 -> welcomeViewModel.saveUserRole(it1) }
                        }*/
                    }
                }
            }
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> navigateToLoginScreen()
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

    override fun onBackPressed() {
        //super.onBackPressed()
        navigateToLoginScreen()
    }


    private fun navigateToAddLovedOneScreen() {
        var payload: Payload? = null
        welcomeViewModel.getUserDetails()
        Handler(Looper.getMainLooper()).postDelayed({
            payload =
                Prefs.with(ShepherdApp.appContext)?.getObject(Const.PAYLOAD, Payload::class.java)

            if (payload?.isActive == true) {
                startActivityWithFinish<AddLovedOneActivity>()
            } else {
                val builder = AlertDialog.Builder(this)
                val dialog = builder.apply {
                    setTitle(getString(R.string.account_activation_required))
                    setMessage(getString(R.string.account_inactive_click_link_on_email))
                    setPositiveButton("OK") { _, _ ->
                        //navigateToLoginScreen()
                    }
                }.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            }
        }, 2000)
    }

    private fun navigateToJoinCareTeamScreen() {
        var payload: Payload? = null
        welcomeViewModel.getUserDetails()
        Handler(Looper.getMainLooper()).postDelayed({
            payload =
                Prefs.with(ShepherdApp.appContext)?.getObject(Const.PAYLOAD, Payload::class.java)

            if (payload?.isActive == true) {
                startActivityWithFinish<JoinCareTeamActivity>()
            } else {
                val builder = AlertDialog.Builder(this)
                val dialog = builder.apply {
                    setTitle(getString(R.string.account_activation_required))
                    setMessage(getString(R.string.account_inactive_click_link_on_email))
                    setPositiveButton("OK") { _, _ ->
                        //navigateToLoginScreen()
                    }
                }.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            }
        }, 2000)
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

