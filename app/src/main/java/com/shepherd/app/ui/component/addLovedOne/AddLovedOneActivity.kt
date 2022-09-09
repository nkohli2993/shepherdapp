package com.shepherd.app.ui.component.addLovedOne

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.model.LatLng
import com.shepherd.app.BuildConfig
import com.shepherd.app.R
import com.shepherd.app.constants.ApiConstants
import com.shepherd.app.data.dto.relation.Relation
import com.shepherd.app.data.dto.user.Payload
import com.shepherd.app.databinding.ActivityAddLovedOneBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseActivity
import com.shepherd.app.ui.component.addLovedOne.adapter.RelationshipsAdapter
import com.shepherd.app.ui.component.addLovedOneCondition.AddLovedOneConditionActivity
import com.shepherd.app.utils.extensions.*
import com.shepherd.app.utils.loadImageCentreCrop
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.AddLovedOneViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_loved_one.*
import java.io.File
import java.text.SimpleDateFormat
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
    private var monthSelected: String = ""
    private var monthIdSelected: Int? = null
    private var dateSelected: String = ""
    private var yearSelected: String = ""
    private val daysAr: ArrayList<String> = arrayListOf()
    private var mYear = 0
    private var mMonth: Int = 0
    private var relationshipsAdapter: RelationshipsAdapter? = null

    private var isEditLovedOne = false
    private var date: String? = null
    private var month: String? = null
    private var year: String? = null
    private var relationName: String? = null
    private var relationSelected: Relation? = null
    private var lovedOneId: Int? = null
    private var lovedOneUUID: String? = null
    private var userProfileId: Int? = null


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
    private var email: String? = null
    private var phoneNumber: String? = null
    private var completeURLProfilePic: String? = null
    private var lovedOneID: String? = null
    private var customAddress: String? = null
    private var lastName: String? = null
    private var alMonths = arrayListOf(
        "Month",
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    )
    private val alDays = arrayListOf(
        "Day",
    )

    // Handle Validation
    private val isValid: Boolean
        get() {
            when {
                binding.edtFirstName.text.toString().isEmpty() -> {
                    binding.edtFirstName.error = getString(R.string.please_enter_first_name)
                    binding.edtFirstName.requestFocus()
                }
                binding.editTextEmail.text.toString()
                    .isNotEmpty() && !binding.editTextEmail.text.toString().isValidEmail() -> {
                    binding.editTextEmail.error = getString(R.string.please_enter_valid_email_id)
                    binding.editTextEmail.requestFocus()
                }
                monthSelected.isEmpty() || dateSelected.isEmpty() || yearSelected.isEmpty() -> {
                    showInfo(this, "Please enter date of birth")
                }
                monthSelected == "Month" || dateSelected == "Date" || yearSelected == "Year" -> {
                    showInfo(this, "Please enter date of birth")
                }
                selectedRelationship?.id == -1 -> {
                    showInfo(this, getString(R.string.please_select_relationship))
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

    @SuppressLint("SimpleDateFormat")
    override fun initViewBinding() {
        binding = ActivityAddLovedOneBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (intent.hasExtra("source")) {
            val data = intent.getStringExtra("source")
            if (data.equals("Loved One Profile")) {
                val payload = intent.getParcelableExtra<Payload>("payload")
                Log.d(TAG, "initViewBinding: $payload")
                isEditLovedOne = true

                binding.txtLovedOne.text = "Edit Loved One Details"
                binding.txtLittleBit.visibility = View.GONE
                if (!payload?.userProfiles?.profilePhoto.isNullOrEmpty()) {
                    Picasso.get().load(payload?.userProfiles?.profilePhoto)
                        .placeholder(R.drawable.ic_defalut_profile_pic).into(binding.imgLoveOne)
                }
                binding.edtFirstName.setText(payload?.userProfiles?.firstname)
                binding.edtLastName.setText(payload?.userProfiles?.lastname)
                binding.editTextEmail.setText(payload?.email)

                binding.edtAddress.text = payload?.userLocation?.formattedAddress

                binding.edtCustomAddress.setText(payload?.userProfiles?.address)
                binding.btnContinue.text = "Save Changes"

                // Get phone number
                /* val phone = careteam?.love_user_id_details?.phone?.split("-")
                 val countryCode = phone?.get(0)?.toInt()
                 val phoneNumber = phone?.get(1)*/

                binding.edtPhoneNumber.setText(payload?.userProfiles?.phoneNumber)
                payload?.userProfiles?.phoneCode?.toInt()
                    ?.let { binding.ccp.setCountryForPhoneCode(it) }

                // Get Date of Birth
                val dob = payload?.userProfiles?.dob.convertISOTimeToDate()
                if (!dob.isNullOrEmpty()) {
                    val birthDate = dob.split("-")
                    date = birthDate[0]
                    month = birthDate[1]
                    year = birthDate[2]
                }

                //Get Relationship
                relationSelected = payload?.relationship?.relation
                relationName = payload?.relationship?.relation?.name

                //Get LovedOne id
                lovedOneId = payload?.userProfiles?.id
                lovedOneUUID = payload?.uniqueUUID

                userProfileId = payload?.userProfiles?.id

            }
        }
        binding.ccp.setOnCountryChangeListener { this.phoneCode = it.phoneCode }

        binding.listener = this
        binding.relationshipSpinner.onItemSelectedListener = this

        addLovedOneViewModel.getRelations(pageNumber, limit)

        val monthAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                alMonths
            )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.monthSpinner.adapter = monthAdapter
        if (isEditLovedOne) {
            val monthPos =
                monthAdapter.getPosition(month?.toInt()?.let { monthAdapter.getItem(it) })
            binding.monthSpinner.setSelection(monthPos)
        }

        val dayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                alDays
            )
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.daySpinner.adapter = dayAdapter

        /* if (isEditLovedOne) {

             val datePos = dayAdapter.getPosition(date)
             binding.daySpinner.setSelection(datePos)
         }*/

        var yearAr: ArrayList<String> = arrayListOf()

        val currentYear = SimpleDateFormat("yyyy").format(Calendar.getInstance().time)
        for (i in currentYear.toInt() - 100 until currentYear.toInt() + 1) {
            yearAr.add(i.toString())
        }
        yearAr.reverse()
        yearAr.add(0, "Year")
        setYearAdapter(yearAr)

        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //add days to day list
                monthSelected = p0?.getItemAtPosition(p2) as String
                monthIdSelected = p2
                if (yearSelected.isEmpty()) {
                    yearSelected = SimpleDateFormat("yyyy").format(Calendar.getInstance().time)
                }
                if (monthIdSelected == 0) {
                    monthIdSelected =
                        SimpleDateFormat("MM").format(Calendar.getInstance().time).toInt()
                }
                calculateDays(monthIdSelected!!, yearSelected)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //add days to day list
                yearSelected = p0?.getItemAtPosition(p2) as String
                if (yearSelected == "Year") {
                    yearSelected = SimpleDateFormat("yyyy").format(Calendar.getInstance().time)
                }
                if (monthIdSelected == null) {
                    monthIdSelected =
                        SimpleDateFormat("MM").format(Calendar.getInstance().time).toInt()
                }

                calculateDays(monthIdSelected!!, yearSelected)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        binding.daySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //add days to day list
                dateSelected = p0?.getItemAtPosition(p2) as String
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun calculateDays(month: Int, year: String) {
        daysAr.clear()
        for (i in 0 until getDayCount(month.toString(), year)) {
            daysAr.add((i + 1).toString())
        }
        daysAr.add(0, "Date")
        setDateAdapter(daysAr)
        val dayCount = getDayCount(month.toString(), year)

        if (dateSelected == "" || dateSelected == "Date" || dateSelected.toInt() > dayCount) {
            binding.daySpinner.setSelection(0)
        } else {
            binding.daySpinner.setSelection(dateSelected.toInt())
        }
    }

    private fun setDateAdapter(daysAr: ArrayList<String>) {
        val dayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                daysAr
            )
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.daySpinner.adapter = dayAdapter
        if (isEditLovedOne) {
            dateSelected = date.toString()
            val datePos = dayAdapter.getPosition(date?.toInt()?.let { dayAdapter.getItem(it) })
            binding.daySpinner.setSelection(datePos)
        }
    }

    private fun setYearAdapter(yearAr: ArrayList<String>) {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                yearAr
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.yearSpinner.adapter = adapter

        if (isEditLovedOne) {
            val yearPos = adapter.getPosition(year)
            binding.yearSpinner.setSelection(yearPos)
        }
    }

    private fun getDayCount(month: String, year: String): Int {
        val days = when (month) {
            "1", "3", "5", "7", "8", "10", "12", "01", "03", "05", "07", "08" -> {
                31
            }
            "4", "6", "9", "11", "04", "06", "09" -> {
                30
            }
            else -> {
                when {
                    year.toInt() % 400 == 0 || year.toInt() % 4 == 0 -> 29
                    else -> 28
                }
            }
        }
        return days
    }

    override fun observeViewModel() {
        observe(selectedFile, ::handleSelectedImage)

        //observe relation live data
        addLovedOneViewModel.relationResponseLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val payload = result.data.payload
                    payload?.relations?.let { it1 -> relations?.addAll(it1) }

                    relations?.add(0, Relation(-1, "Select Relationship"))
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

                    if (isEditLovedOne) {
                        val relationPos = relationshipsAdapter?.getPosition(relationSelected)
                        relationPos?.let { binding.relationshipSpinner.setSelection(it) }
                        selectedRelationship = relationSelected
                    }
                }

                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(this, it) }
                }
            }
        }

        // Observe the response of upload image api
        addLovedOneViewModel.uploadImageLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(this, it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    result.data.let { it1 ->
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
                    it.message?.let { showError(this, it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    it.data.let { it1 ->
                        it1.message?.let { it2 -> showSuccess(this, it2) }
                        lovedOneID = it1.payload.id

                        // Save Loved One Id to sharedPref
                        addLovedOneViewModel.saveLovedOneId(lovedOneID)

                        // Saved LovedOne UUID to Shared Pref
                        it1.payload.uniqueUuid?.let { it2 ->
                            addLovedOneViewModel.saveLovedOneUUID(
                                it2
                            )
                        }
                        navigateToAddLovedOneConditionScreen()
                    }
                }
            }
        }
        // Observe Edit Loved One api response
        addLovedOneViewModel.editLovedOneLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(this, it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val payload = it.data.payload
                    Log.d(TAG, "observeViewModel: $payload")
                    Handler(Looper.getMainLooper()).postDelayed({
                        // Your Code
                        finishActivity()
                    }, 1000)
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
                if (!checkPermission()) {
                    requestPermission()
                } else {
                    openImagePicker()
                }
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
                    email = binding.editTextEmail.text.toString().trim()
                    if (email.isNullOrEmpty()) {
                        email = null
                    }
                    val firstName = binding.edtFirstName.text.toString().trim()
                    lastName = binding.edtLastName.text.toString().trim()
                    val relationId = selectedRelationship?.id
                    phoneCode = ccp.selectedCountryCode
                    phoneNumber = binding.edtPhoneNumber.text.toString().trim()

                    if (phoneNumber.isNullOrEmpty()) {
                        phoneCode = null
                        phoneNumber = null
                    }

                    if (lovedOnePicUrl.isNullOrEmpty()) {
                        lovedOnePicUrl = null
                        completeURLProfilePic = null
                    } else {
                        completeURLProfilePic = BuildConfig.BASE_URL_USER + lovedOnePicUrl
//                        completeURLProfilePic = ApiConstants.BASE_URL_USER + lovedOnePicUrl
                    }

                    customAddress = binding.edtCustomAddress.text.toString()
                    if (customAddress.isNullOrEmpty()) {
                        customAddress = null
                    }

                    if (lastName.isNullOrEmpty()) {
                        lastName = null
                    }
                    dob =
                        yearSelected + "-" + (if (monthIdSelected!! < 10) "0$monthIdSelected" else monthIdSelected!!.toString()) + "-" + (if (dateSelected.toInt() < 10) "0$dateSelected" else dateSelected)

                    if (isEditLovedOne) {
                        lovedOneUUID?.let {
                            addLovedOneViewModel.editLovedOne(
                                email,
                                firstName,
                                lastName,
                                relationId,
                                phoneCode,
                                dob,
                                placeId,
                                customAddress,
                                phoneNumber,
                                completeURLProfilePic,
                                it
                            )
                        }
                    } else {
                        addLovedOneViewModel.createLovedOne(
                            email,
                            firstName,
                            lastName,
                            relationId,
                            phoneCode,
                            dob,
                            placeId,
                            customAddress,
                            phoneNumber,
                            completeURLProfilePic
                        )
                    }
                }

            }
            R.id.layoutAddress -> {
                val intent = Intent(this, SearchPlacesActivity::class.java)
                intent.putExtra("search_type", "loved_one_address")
                navLauncher.launch(intent)
            }
            R.id.edtAddress -> {
                val intent = Intent(this, SearchPlacesActivity::class.java)
                intent.putExtra("search_type", "loved_one_address")
                navLauncher.launch(intent)
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
            R.id.imgUploadLovedOnePic, R.id.imgLoveOne -> {
                if (!checkPermission()) {
                    requestPermission()
                } else {
                    openImagePicker()
                }
            }
            R.id.ivInfo -> {
                showPopUp()
            }
        }
    }

    private fun showPopUp() {
        val dialog = AlertDialog.Builder(this)
        val layoutView = layoutInflater.inflate(R.layout.dialog_loved_one_info_pop_up, null)
        val okayBtn = layoutView.findViewById(R.id.btnOkay) as Button
        dialog.setView(layoutView)
        dialog.setCancelable(true)
        val alertDialog = dialog.create()
        alertDialog.window?.setBackgroundDrawable(
            InsetDrawable(
                ColorDrawable(Color.TRANSPARENT),
                20
            )
        )
        alertDialog.show()
        okayBtn.setOnClickListener { alertDialog.dismiss() }
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

        val months = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        //show dialog
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                edtDay.text = dayOfMonth.toString()
                edtMonth.text = months[month]
                edtYear.text = year.toString()
                dob = year.toString() + "-" + (month + 1).toString() + "-" + dayOfMonth.toString()
//                dob = year.toString() + "-" + months[month] + "-" + dayOfMonth.toString()
                Log.d(TAG, "DateOfBirth: $dob")
            },
            mYear,
            mMonth,
            mDay
        )
        // set maximum date to be selected as today
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }


    private fun navigateToAddLovedOneConditionScreen() {
        // addLovedOneViewModel.saveLovedOneId(lovedOneID)
        val intent = Intent(this, AddLovedOneConditionActivity::class.java)
        intent.putExtra("source", getIntent().getStringExtra("source"))
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
        //startActivityWithFinish<AddLovedOneConditionActivity>()
    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        when (parent?.id) {
            R.id.relationship_spinner -> {
                if (position > 0) selectState(position) else selectState(0)
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

