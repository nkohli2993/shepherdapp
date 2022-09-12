package com.shepherdapp.app.ui.component.welcome

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.user_detail.Payload
import com.shepherdapp.app.databinding.ActivityWelcomeUserBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.addLovedOne.AddLovedOneActivity
import com.shepherdapp.app.ui.component.joinCareTeam.JoinCareTeamActivity
import com.shepherdapp.app.ui.component.login.LoginActivity
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.view_model.WelcomeUserViewModel
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
        setResendText(binding.emailResendTV.text.toString())
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
        welcomeViewModel.verificationResponseLiveData.observeEvent(this) {
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
                    showInfo(this, it.data.message.toString())
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
            R.id.btnResend -> welcomeViewModel.sendUserVerificationEmail()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        //navigateToLoginScreen()
        // Navigate to Home Screen of Android
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
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
                startActivity<JoinCareTeamActivity>()
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

    private fun setResendText(text: String) {
        val ss = SpannableString(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(p0: View) {
                // send resend verification email
                welcomeViewModel.sendUserVerificationEmail()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
                ds.color = ContextCompat.getColor(applicationContext, R.color._A26DCB)
                ds.linkColor = ContextCompat.getColor(applicationContext, R.color._A26DCB)
            }
        }
        ss.setSpan(clickableSpan, 26, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.emailResendTV.text = ss
        binding.emailResendTV.movementMethod = LinkMovementMethod.getInstance()
       // binding.emailResendTV.highlightColor = Color.GREEN
    }


}

