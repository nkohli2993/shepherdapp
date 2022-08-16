package com.shepherd.app.ui.component.addLovedOneCondition

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shepherd.app.R
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.care_team.CareCondition
import com.shepherd.app.data.dto.medical_conditions.Conditions
import com.shepherd.app.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.shepherd.app.databinding.ActivityAddLovedOneConditionBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseActivity
import com.shepherd.app.ui.component.addLovedOneCondition.adapter.AddLovedOneConditionAdapter
import com.shepherd.app.ui.component.home.HomeActivity
import com.shepherd.app.ui.welcome.WelcomeActivity
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Prefs
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.view_model.AddLovedOneConditionViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.shepherd.app.data.dto.medical_conditions.get_loved_one_medical_conditions.Payload

/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddLovedOneConditionActivity : BaseActivity(), View.OnClickListener,
    AddLovedOneConditionAdapter.ItemSelectedListener {

    private val addLovedOneConditionViewModel: AddLovedOneConditionViewModel by viewModels()
    private lateinit var binding: ActivityAddLovedOneConditionBinding
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var conditions: ArrayList<Conditions>? = ArrayList()
    private var selectedConditions: ArrayList<Conditions>? = ArrayList()
    private var searchedConditions: ArrayList<Conditions>? = ArrayList()
    private var addLovedOneConditionAdapter: AddLovedOneConditionAdapter? = null
    private var medicalConditionsLovedOneArray: ArrayList<MedicalConditionsLovedOneRequestModel> =
        ArrayList()
    private var lovedOneID: Int? = null
    private var TAG = "AddLovedOneConditionActivity"
    private var loveOneId: String? = null
    private var careConditions: ArrayList<CareCondition>? = null
    private var addedConditionPayload: ArrayList<Payload> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this
        // Get Medical conditions
        addLovedOneConditionViewModel.getMedicalConditions(pageNumber, limit)
        //check if intent has loved used uuid
        if (intent.hasExtra("love_one_id")) {
            loveOneId = intent.getStringExtra("love_one_id")
            Log.d(TAG, "LovedOneUUID: $lovedOneID")
            careConditions = intent.getParcelableArrayListExtra("care_conditions")
            //Get loved one's medical conditions
            Handler(Looper.getMainLooper()).postDelayed({
                loveOneId?.let { addLovedOneConditionViewModel.getLovedOneMedicalConditions(it) }
            }, 1000)
        }

        binding.recyclerViewCondition.layoutManager = LinearLayoutManager(this)

        binding.imgCancel.setOnClickListener {
            binding.editTextSearch.setText("")
            binding.txtNoResultFound.visibility = View.GONE
            binding.recyclerViewCondition.visibility = View.VISIBLE
        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.isEmpty()) {
                        conditions?.let { addLovedOneConditionAdapter?.updateConditions(it) }
                        binding.imgCancel.visibility = View.GONE

                    }
                    if (s.isNotEmpty()) {
                        binding.imgCancel.visibility = View.VISIBLE
                        searchedConditions?.clear()
                        conditions?.forEach {
                            if (it.name?.startsWith(s, true) == true) {
                                searchedConditions?.add(it)
                            }
                        }
                        if (searchedConditions.isNullOrEmpty()) {
                            binding.txtNoResultFound.visibility = View.VISIBLE
                            binding.recyclerViewCondition.visibility = View.GONE
                        }
                        searchedConditions?.let { addLovedOneConditionAdapter?.updateConditions(it) }
                    }
                }
            }
        })
    }


    override fun initViewBinding() {
        binding = ActivityAddLovedOneConditionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        //Observe the response of get all medical conditions api
        addLovedOneConditionViewModel.medicalConditionResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    conditions = it.data.payload?.conditions
                    if (careConditions != null) { // to check already added care conditions
                        for (i in conditions!!) {
                            for (j in careConditions!!) {
                                if (j.id == i.id) {
                                    i.isSelected = true
                                }
                            }
                        }
                    }
                    addLovedOneConditionAdapter = conditions?.let { it1 ->
                        AddLovedOneConditionAdapter(
                            addLovedOneConditionViewModel,
                            it1
                        )
                    }
                    addLovedOneConditionAdapter?.setClickListener(this)
                    binding.recyclerViewCondition.adapter = addLovedOneConditionAdapter

                }

                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(this, it.toString()) }

                }
            }
        }
        //Observe the response of adding loved one's medical conditions api
        addLovedOneConditionViewModel.userConditionsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    //it.data.message?.let { it1 -> showSuccess(this, it1) }
                    val builder = AlertDialog.Builder(this)
                    val dialog = builder.apply {
                        setTitle("Medical Conditions")
                        setMessage("Medical Conditions added successfully...")
                        setPositiveButton("OK") { _, _ ->
                            navigateToHomeScreen()
                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }

                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(this, it.toString()) }

                }
            }
        }
        //Observe the response of get loved one's medical conditions api
        addLovedOneConditionViewModel.lovedOneMedicalConditionResponseLiveData.observeEvent(this) {
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
                    addedConditionPayload = it.data.payload
                    val conditionIDs = addedConditionPayload.map {
                        it.conditions?.id
                    }
                    if (addedConditionPayload.size <= 0) {
                        // show popup for no medical conditions
                        val builder = AlertDialog.Builder(this)
                        val dialog = builder.apply {
                            setMessage("No medical condition added, Please add medical condition for loved one.")
                            setPositiveButton("Add") { _, _ ->

                            }
                            setNegativeButton("Cancel") { _, _ ->
                                finishActivity()
                            }
                        }.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                        return@observeEvent
                    }
                    for (i in conditions?.indices!!) {
                        for (j in conditionIDs.indices) {
                            if (conditions!![i].id == conditionIDs[j]) {
                                conditions!![i].isSelected = true
                            }
                        }
                    }
                    Log.d(TAG, "conditions updated :$conditions ")
                    conditions?.let { it1 -> addLovedOneConditionAdapter?.updateConditions(it1) }
                }
            }
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                finishActivity()
            }
            R.id.buttonFinish -> {
                //navigateToWelcomeScreen()
                // Get LovedOne UUID from shared Pref
                val lovedOneUUID =
                    Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

                val ids = selectedConditions?.map {
                    it.id
                }

                if (medicalConditionsLovedOneArray.isNotEmpty()) {
                    medicalConditionsLovedOneArray.clear()
                }
                when (intent.getStringExtra("source")) {
                    Const.MEDICAL_CONDITION -> {
                        if (addedConditionPayload.size <= 0) {
                            // api to add medical condition
                            addMedicalConditons(ids, loveOneId)
                        } else {
                            // api to update medical condition
                            showError(this, "Update medical condition")
                        }
                    }
                    else -> {
                        addMedicalConditons(ids, lovedOneUUID)
                    }
                }


            }
        }
    }

    private fun addMedicalConditons(
        ids: List<Int?>?,
        lovedOneUUID: String?
    ) {
        if (ids != null) {
            for (i in ids.indices) {
                medicalConditionsLovedOneArray.add(MedicalConditionsLovedOneRequestModel(i?.let {
                    ids[it]
                }, lovedOneUUID))
            }
        }

        if (medicalConditionsLovedOneArray.size != 0) {
            addLovedOneConditionViewModel.createMedicalConditions(
                medicalConditionsLovedOneArray
            )
        } else {
            showError(this, "Please select at least once medical condition...")
        }

    }


    private fun navigateToWelcomeScreen() {
        startActivityWithFinishAffinity<WelcomeActivity>()
    }

    private fun navigateToHomeScreen() {
        if (intent.getStringExtra("source") == Const.ADD_LOVE_ONE) {
            onBackPressed()
        } else if (intent.getStringExtra("source") == Const.MEDICAL_CONDITION) {
            finishActivity()
        } else {
            startActivityWithFinish<HomeActivity>()
        }
    }

    override fun itemSelected(conditions: Conditions) {

        if (selectedConditions?.isEmpty() == true)
            conditions.let { selectedConditions?.add(it) }
        else if (conditions.isSelected == true) selectedConditions?.add(conditions)
        else if (conditions.isSelected == false && selectedConditions?.contains(conditions) == true)
            selectedConditions?.remove(conditions)
    }

}

