package com.app.shepherd.ui.component.addLovedOne

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.ActivityAddLovedOneBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOneCondition.AddLovedOneConditionActivity
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_loved_one.*
import java.io.File


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddLovedOneActivity : BaseActivity(), View.OnClickListener {

    private val addLovedOneViewModel: AddLovedOneViewModel by viewModels()
    private lateinit var binding: ActivityAddLovedOneBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolBar.listener = this
        binding.listener = this

        initDobPicker()
        setPhoneNumberFormat()
    }


    override fun initViewBinding() {
        binding = ActivityAddLovedOneBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        observe(addLovedOneViewModel.loginLiveData, ::handleLoginResult)
        observe(selectedFile, ::handleSelectedImage)
        observeSnackBarMessages(addLovedOneViewModel.showSnackBar)
        observeToast(addLovedOneViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {
            }
            is Resource.DataError -> {
                status.errorCode?.let { addLovedOneViewModel.showToastMessage(it) }
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
            R.id.buttonContinue -> {
                navigateToAddLovedOneConditionScreen()
            }
            R.id.imageViewLovedOne -> {
                openImagePicker()
            }
        }
    }

    private fun initDobPicker() {
        editTextDOB.datePicker(
            supportFragmentManager,
            AddLovedOneActivity::class.java.simpleName
        )
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


    private fun navigateToAddLovedOneConditionScreen() {
        startActivity<AddLovedOneConditionActivity>()
    }

}

