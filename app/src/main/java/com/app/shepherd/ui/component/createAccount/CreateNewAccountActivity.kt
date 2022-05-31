package com.app.shepherd.ui.component.createAccount

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.ActivityCreateAccountBinding
import com.app.shepherd.databinding.ActivityCreateNewAccountBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.welcome.WelcomeActivity
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_create_account.cardViewImage
import kotlinx.android.synthetic.main.activity_create_account.imageViewLovedOne
import kotlinx.android.synthetic.main.activity_create_account.textViewUploadPhoto
import java.io.File


/**
 * Created by Deepak Rattan on 31-05-22
 */
@AndroidEntryPoint
class CreateNewAccountActivity : BaseActivity(), View.OnClickListener {

    private val createAccountViewModel: CreateAccountViewModel by viewModels()
    private lateinit var binding: ActivityCreateNewAccountBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolBar.listener = this
        binding.listener = this

        //setPhoneNumberFormat()
    }


    override fun initViewBinding() {
        binding = ActivityCreateNewAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {

    }





    override fun onClick(p0: View?) {
        when (p0?.id) {

        }
    }

    private fun setPhoneNumberFormat() {
        editTextPhoneNumber.addTextChangedListener(
            PhoneTextFormatter(
                editTextPhoneNumber, getString(
                    R.string.us_phone_pattern
                )
            )
        )

    }


    private fun navigateToWelcomeScreen() {
        startActivity<WelcomeActivity>()
    }

}

