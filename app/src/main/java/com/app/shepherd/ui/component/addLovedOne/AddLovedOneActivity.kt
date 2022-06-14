package com.app.shepherd.ui.component.addLovedOne

import android.app.DatePickerDialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.app.shepherd.BuildConfig
import com.app.shepherd.R
import com.app.shepherd.data.dto.relation.Relation
import com.app.shepherd.databinding.ActivityAddLovedOneBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOne.adapter.RelationshipsAdapter
import com.app.shepherd.ui.component.addLovedOneCondition.AddLovedOneConditionActivity
import com.app.shepherd.utils.extensions.isValidEmail
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.extensions.showInfo
import com.app.shepherd.utils.extensions.showSuccess
import com.app.shepherd.utils.loadImageCentreCrop
import com.app.shepherd.utils.observe
import com.app.shepherd.view_model.AddLovedOneViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_loved_one.*
import java.io.File
import java.time.LocalDate
import java.time.Period
import java.util.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddLovedOneActivity : BaseActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private val addLovedOneViewModel: AddLovedOneViewModel by viewModels()
    private lateinit var binding: ActivityAddLovedOneBinding
    private var selectedRelationship: Relation? = null
    private var mYear = 0
    private var mMonth: Int = 0
    private var relationshipsAdapter: RelationshipsAdapter? = null

    private var mDay: Int = 0
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var placeLatLng: LatLng? = null
    private var placeAddress: String? = null
    private var placeId: String? = null
    private var relations: ArrayList<Relation>? = ArrayList()
    private var lovedOnePicUrl: String? = null
    private var phoneCode: String? = null
    private var dob: String? = null
    private var TAG: String = "AddLovedOneActivity"


    // Handle Validation
    private val isValid: Boolean
        get() {
            when {
                binding.edtFirstName.text.toString().isEmpty() -> {
                    binding.edtFirstName.error = getString(R.string.please_enter_first_name)
                    binding.edtFirstName.requestFocus()
                }
                binding.edtLastName.text.toString().isEmpty() -> {
                    binding.edtLastName.error = getString(R.string.please_enter_last_name)
                    binding.edtLastName.requestFocus()
                }
                binding.editTextEmail.text.toString().isEmpty() -> {
                    binding.editTextEmail.error = getString(R.string.please_enter_email_id)
                    binding.editTextEmail.requestFocus()
                }
                !binding.editTextEmail.text.toString().isValidEmail() -> {
                    binding.editTextEmail.error = getString(R.string.please_enter_valid_email_id)
                    binding.editTextEmail.requestFocus()
                }
                binding.edtPhoneNumber.text.toString().isEmpty() -> {
                    binding.edtPhoneNumber.error = getString(R.string.enter_phone_number)
                    binding.edtPhoneNumber.requestFocus()
                }
                dob.isNullOrEmpty() -> {
                    showInfo(this, "Please enter date of birth")
                }
                binding.edtAddress.text.toString().isEmpty() -> {
                    binding.edtAddress.error = getString(R.string.enter_address)
                    binding.edtAddress.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }


    private var navLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 10101) onPlaceSelected(result.data)
        }

    override fun initViewBinding() {
        binding = ActivityAddLovedOneBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.ccp.setOnCountryChangeListener { this.phoneCode = it.phoneCode }

        binding.listener = this
        binding.relationshipSpinner.onItemSelectedListener = this

        addLovedOneViewModel.getRelations(pageNumber, limit)
    }

    override fun observeViewModel() {
        observe(selectedFile, ::handleSelectedImage)

        //observe relation live data
        addLovedOneViewModel.relationResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val payload = it.data.payload
                    payload?.relations?.let { it1 -> relations?.addAll(it1) }


                    relationshipsAdapter =
                        relations?.let { relation ->
                            RelationshipsAdapter(
                                this,
                                R.layout.vehicle_spinner_drop_view_item,
                                relation
                            )
                        }

                    binding.relationshipSpinner.adapter = relationshipsAdapter

                    val relation = relationshipsAdapter?.getItem(0)
                    if (relation != null) selectedRelationship = relation
                }

                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(this, it.toString()) }

                }
            }
        }

        // Observe the response of upload image api
        addLovedOneViewModel.uploadImageLiveData.observeEvent(this) {
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
                    it.data.let { it1 ->
                        it1.message?.let { it2 -> showSuccess(this, it2) }
                        lovedOnePicUrl = it1.payload?.profilePhoto
                    }
                }
            }
        }

        // Observe the response of create loved one api
        addLovedOneViewModel.createLovedOneLiveData.observeEvent(this) {
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
                    it.data.let { it1 ->
                        it1.message?.let { it2 -> showSuccess(this, it2) }
                        navigateToAddLovedOneConditionScreen()
                    }
                }
            }
        }
    }

    private fun handleSelectedImage(file: File?) {
        if (file != null && file.exists()) {
            addLovedOneViewModel.imageFile = file
            addLovedOneViewModel.uploadImage(file)
            imgLoveOne.loadImageCentreCrop(R.drawable.ic_outline_person, file)
            imgLoveOne.scaleType = ImageView.ScaleType.FIT_XY
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.imageViewLovedOne -> {
                openImagePicker()
            }
            R.id.edtDay -> {
                initDatePicker()
            }
            R.id.edtMonth -> {
                initDatePicker()
            }
            R.id.edtYear -> {
                initDatePicker()
            }
            R.id.btnContinue -> {
                if (isValid) {
                    val email = binding.editTextEmail.text.toString().trim()
                    val firstName = binding.edtFirstName.text.toString().trim()
                    val lastName = binding.edtLastName.text.toString().trim()
                    val relationId = selectedRelationship?.id
                    val phoneCode = ccp.selectedCountryCode
                    val phoneNumber = binding.edtPhoneNumber.text.toString().trim()

                    addLovedOneViewModel.createLovedOne(
                        email,
                        firstName,
                        lastName,
                        relationId,
                        phoneCode,
                        dob,
                        placeId,
                        phoneNumber,
                        BuildConfig.BASE_URL + lovedOnePicUrl
                    )
                }

                /* val email = binding.editTextEmail.text.toString().trim()
                 val firstName = binding.edtFirstName.text.toString().trim()
                 val lastName = binding.edtLastName.text.toString().trim()
                 val relationId = selectedRelationship?.id
                 val phoneCode = ccp.selectedCountryCode
                 val phoneNumber = binding.edtPhoneNumber.text.toString().trim()

                 addLovedOneViewModel.createLovedOne(
                     email,
                     firstName,
                     lastName,
                     relationId,
                     phoneCode,
                     dob,
                     placeId,
                     phoneNumber,
                     BuildConfig.BASE_URL + lovedOnePicUrl
                 )*/


                // navigateToAddLovedOneConditionScreen()
            }
            R.id.layoutAddress -> {
                navLauncher.launch(Intent(this, SearchPlacesActivity::class.java))
            }
            R.id.edtAddress -> {
                navLauncher.launch(Intent(this, SearchPlacesActivity::class.java))
            }
            R.id.relationshipSpinnerLayout -> {
                binding.relationshipSpinner.performClick()
            }
            R.id.relationship_spinner -> {
                binding.relationshipSpinner.performClick()
            }
            R.id.spinner_down_arrow_image -> {
                binding.relationshipSpinner.performClick()
            }
            R.id.imgUploadLovedOnePic -> {
                openImagePicker()
            }
        }
    }

    private fun onPlaceSelected(data: Intent?) {
        placeAddress = data?.getStringExtra("placeName")
        placeId = data?.getStringExtra("placeId")
        val lat = data?.getDoubleExtra("latitude", 0.0)
        val lng = data?.getDoubleExtra("longitude", 0.0)
        placeLatLng = lat?.let { lng?.let { it1 -> LatLng(it, it1) } }
        binding.edtAddress.text = placeAddress
    }


    private fun initDatePicker() {
        val calendar = Calendar.getInstance()
        mYear = calendar[Calendar.YEAR]
        mMonth = calendar[Calendar.MONTH]
        mDay = calendar[Calendar.DAY_OF_MONTH]


        //show dialog
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                edtDay.text = dayOfMonth.toString()
                edtMonth.text = (month + 1).toString()
                edtYear.text = year.toString()
                dob = year.toString() + "-" + (month + 1).toString() + "-" + dayOfMonth.toString()
                Log.d(TAG, "DateOfBirth: $dob")
            },
            mYear,
            mMonth,
            mDay
        )
        // set maximum date to be selected as today
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis;
        datePickerDialog.show()
    }


    private fun navigateToAddLovedOneConditionScreen() {
        startActivity<AddLovedOneConditionActivity>()
    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        when (parent?.id) {
            R.id.relationship_spinner -> {
                if (position > 0) selectState(position)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        selectState(0)
    }

    private fun selectState(position: Int) {
        val stateData = relationshipsAdapter?.getItem(position)
        if (stateData != null) selectedRelationship = stateData
    }
}

