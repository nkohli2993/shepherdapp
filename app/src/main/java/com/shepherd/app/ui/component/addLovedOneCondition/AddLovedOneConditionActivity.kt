package com.shepherd.app.ui.component.addLovedOneCondition

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.shepherd.app.R
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.medical_conditions.Conditions
import com.shepherd.app.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.shepherd.app.data.dto.medical_conditions.UpdateMedicalConditionRequestModel
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
    private var loveOneId: String? = null
    private var addedConditionPayload: ArrayList<Payload> = arrayListOf()
    private var conditionIDs: List<Int?>? = null
    private var isLoading = false
    private var deletedIds: ArrayList<Int> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this

        //check if intent has loved used uuid
        if (intent.hasExtra("love_one_id")) {
            loveOneId = intent.getStringExtra("love_one_id")
            isLoading = false
            loveOneId?.let { addLovedOneConditionViewModel.getLovedOneMedicalConditions(it) }
        } else {
            isLoading = true
            callAllMedicalCondition()
        }

        binding.recyclerViewCondition.layoutManager = LinearLayoutManager(this)

        binding.imgCancel.setOnClickListener {
            binding.editTextSearch.setText("")
            binding.txtNoResultFound.visibility = View.GONE
            binding.recyclerViewCondition.visibility = View.VISIBLE
        }

        binding.editTextSearch.doAfterTextChanged { search ->
            if (search != null) {
                if (search.isEmpty()) {
                    conditions?.let { addLovedOneConditionAdapter?.updateConditions(it) }
                    binding.imgCancel.visibility = View.GONE
                    binding.txtNoResultFound.visibility = View.GONE
                    binding.recyclerViewCondition.visibility = View.VISIBLE

                }
                if (search.isNotEmpty()) {
                    binding.imgCancel.visibility = View.VISIBLE
                    searchedConditions?.clear()
                    conditions?.forEach {
                        if (it.name?.contains(search.toString(), true) == true) {
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
    }


    override fun initViewBinding() {
        binding = ActivityAddLovedOneConditionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun observeViewModel() {
        //Observe the response of get loved one's medical conditions api
        addLovedOneConditionViewModel.lovedOneMedicalConditionResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    // hideLoading()
                    it.message?.let { showError(this, it.toString()) }
                    callAllMedicalCondition()
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    //   hideLoading()
                    addedConditionPayload = it.data.payload
                    conditionIDs = addedConditionPayload.map {
                        it.conditionId
                    }
                    callAllMedicalCondition()
                    if (addedConditionPayload.size <= 0) {
                        // show popup for no medical conditions
                        val builder = AlertDialog.Builder(this)
                        val dialog = builder.apply {
                            setMessage(getString(R.string.no_medical_condition_added_for_loved_one))
                            setPositiveButton(getString(R.string.add)) { _, _ ->

                            }
                            setNegativeButton(getString(R.string.cancel)) { _, _ ->
                                finishActivity()
                            }
                        }.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                        return@observeEvent
                    }
                }
            }
        }
        //Observe the response of get all medical conditions api
        addLovedOneConditionViewModel.medicalConditionResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    if (isLoading) {
                        showLoading("")
                    }
                }
                is DataResult.Success -> {
                    hideLoading()
                    conditions = it.data.payload?.conditions

                    if (conditionIDs != null) {
                        for (i in conditions?.indices!!) {
                            for (j in addedConditionPayload.indices) {
                                if (conditions!![i].id == conditionIDs!![j]) {
                                    conditions!![i].isSelected = true
                                    conditions!![i].isAlreadySelected = true
                                }
                            }
                        }
                    }
                    conditions?.let { it1 -> addLovedOneConditionAdapter?.updateConditions(it1) }

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
                    it.message?.let { showError(this, it) }

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
                        setMessage(getString(R.string.medical_condition_added_successfully))
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


    }

    private fun callAllMedicalCondition() {
        addLovedOneConditionViewModel.getMedicalConditions(pageNumber, limit)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                finishActivity()
            }
            R.id.buttonFinish -> {
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
                          /*  // api to update medical condition
                            val deleteId :ArrayList<Int> = arrayListOf()
                            val newAdded :ArrayList<MedicalConditionsLovedOneRequestModel> = arrayListOf()
                            for (i in conditions!!) {
                                if (i.isSelected && i.isAlreadySelected == null) {
                                  newAdded.add(MedicalConditionsLovedOneRequestModel(i.id,loveOneId))
                                } else if (i.isAlreadySelected!= null && (!i.isSelected && !i.isAlreadySelected!!)) {
                                    deleteId.add(i.id!!)
                                }
                            }
                            addLovedOneConditionViewModel.updateMedicalConditions(
                                UpdateMedicalConditionRequestModel(newAdded,deleteId)
                            )*/
                            showError(this,"Not implemented")
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
                medicalConditionsLovedOneArray.add(MedicalConditionsLovedOneRequestModel(i.let {
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

    /*
        override fun itemSelected(conditions: Conditions) {
        when {
            selectedConditions?.isEmpty() == true -> conditions.let { selectedConditions?.add(it) }
            conditions.isSelected -> selectedConditions?.add(conditions)
            !conditions.isSelected && selectedConditions?.contains(conditions) == true -> selectedConditions?.remove(
                conditions
            )
        }

    }
*/
    override fun itemSelected(position: Int) {
        conditions!![position].isSelected = !conditions!![position].isSelected
        if (conditions!![position].isAlreadySelected != null) {
            conditions!![position].isAlreadySelected = !conditions!![position].isAlreadySelected!!
        }
        conditions?.let { addLovedOneConditionAdapter?.updateConditions(it) }
    }
}

