package com.app.shepherd.ui.component.createAccount

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.ActivityCreateAccountBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOne.AddLovedOneActivity
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
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class CreateAccountActivity : BaseActivity(), View.OnClickListener {

    private val createAccountViewModel: CreateAccountViewModel by viewModels()
    private lateinit var binding: ActivityCreateAccountBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolBar.listener = this
        binding.listener = this

        setPhoneNumberFormat()
    }


    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        observe(createAccountViewModel.loginLiveData, ::handleLoginResult)
        observe(selectedFile, ::handleSelectedImage)
        observeSnackBarMessages(createAccountViewModel.showSnackBar)
        observeToast(createAccountViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { createAccountViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleSelectedImage(file: File?) {
        if (file != null && file.exists()) {
            textViewUploadPhoto.toGone()
            cardViewImage.setCardBackgroundColor(this.colorList(R.color.colorWhite))
            imageViewLovedOne.loadImageCentreCrop(R.drawable.ic_outline_person, file)
            imageViewLovedOne.scaleType = ImageView.ScaleType.FIT_XY
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
            R.id.imageViewBack -> {
                finishActivity()
            }
            R.id.buttonCreateAccount -> {
                navigateToWelcomeScreen()
            }
            R.id.imageViewLovedOne -> {
                openImagePicker()
            }
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

