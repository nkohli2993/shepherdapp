package com.shepherdapp.app.ui.component.addLovedOneCondition

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.medical_conditions.Conditions
import com.shepherdapp.app.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.shepherdapp.app.data.dto.medical_conditions.UpdateMedicalConditionRequestModel
import com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions.Payload
import com.shepherdapp.app.databinding.ActivityAddLovedOneConditionBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.addLovedOneCondition.adapter.AddLovedOneConditionAdapter
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.Status
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.AddLovedOneConditionViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddLovedOneConditionActivity : BaseActivity(), View.OnClickListener,
    AddLovedOneConditionAdapter.ItemSelectedListener {

    private lateinit var binding: ActivityAddLovedOneConditionBinding
    private val addLovedOneConditionViewModel: AddLovedOneConditionViewModel by viewModels()
    private var pageNumber: Int = 1
    private var limit: Int = 20
    private var conditions: ArrayList<Conditions> = ArrayList()
    private var selectedConditions: ArrayList<Conditions> = ArrayList()
    private var searchedConditions: ArrayList<Conditions> = ArrayList()
    private var addLovedOneConditionAdapter: AddLovedOneConditionAdapter? = null
    private var medicalConditionsLovedOneArray: ArrayList<MedicalConditionsLovedOneRequestModel> =
        ArrayList()
    private var loveOneId: String? = null
    private var addedConditionPayload: ArrayList<Payload> = arrayListOf()
    private var conditionIDs: List<Int?>? = null
    private var isLoading = false
    private var isSearched = false
    private var currentPage: Int = 0
    private var totalPage: Int = 0
    private var total: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this
        setConditionAdapter()
        binding.recyclerViewCondition.layoutManager = LinearLayoutManager(this)

        binding.imgCancel.setOnClickListener {
            binding.editTextSearch.setText("")
            isSearched = false
            binding.txtNoResultFound.visibility = View.GONE
            binding.recyclerViewCondition.visibility = View.VISIBLE
            hideLoading()
        }

        binding.editTextSearch.doAfterTextChanged { search ->
            if (search != null) {
                if (search.isEmpty()) {
                    isSearched = false
                    conditions.let { addLovedOneConditionAdapter?.updateConditions(it) }
                    binding.imgCancel.visibility = View.GONE
                    binding.txtNoResultFound.visibility = View.GONE
                    binding.recyclerViewCondition.visibility = View.VISIBLE
                    hideLoading()
                }
                if (search.isNotEmpty()) {
                    binding.imgCancel.visibility = View.VISIBLE
                    searchedConditions.clear()
                    isSearched = true
                    pageNumber = 1
                    conditions.forEach {
                        if (it.name?.contains(search.toString(), true) == true) {
                            searchedConditions.add(it)
                        }
                    }
                    binding.txtNoResultFound.visibility = View.GONE
                    binding.recyclerViewCondition.visibility = View.VISIBLE
                    if (searchedConditions.isEmpty()) {
                        binding.txtNoResultFound.visibility = View.VISIBLE
                        binding.recyclerViewCondition.visibility = View.GONE
                    }
                    searchedConditions.let { addLovedOneConditionAdapter?.updateConditions(it) }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        conditions.clear()
        addLovedOneConditionAdapter = null
        pageNumber = 1
        binding.editTextSearch.setText("")
        //check if intent has loved used uuid
        if (intent.hasExtra("love_one_id")) {
            loveOneId = intent.getStringExtra("love_one_id")
            isLoading = false
            loveOneId?.let { addLovedOneConditionViewModel.getLovedOneMedicalConditions(it) }
        } else {
            isLoading = true
            callAllMedicalCondition()
        }
    }

    override fun initViewBinding() {
        binding = ActivityAddLovedOneConditionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    @SuppressLint("SetTextI18n")
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
                        binding.buttonFinish.text = getString(R.string.add)
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
                    } else {
                        binding.txtMedicalCondition.text =
                            getString(R.string.edit_medical_conditions)
                        binding.buttonFinish.text = getString(R.string.save_changes)
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
                    isLoading = false
                    var medicalConditions: ArrayList<Conditions> = ArrayList()
                    if (pageNumber == 1) {
                        conditions.clear()
                        addLovedOneConditionAdapter = null
                        setConditionAdapter()
                    }
                    medicalConditions.clear()
                    if (it.data.payload?.conditions != null) {
                        it.data.payload!!.let { payload ->
                            conditions.addAll(payload.conditions)
                            medicalConditions = payload.conditions
                            total = payload.total!!
                            currentPage = payload.currentPage!!
                            totalPage = payload.totalPages!!
                        }
                    }
                    for (i in conditions.indices) {
                        for (j in addedConditionPayload.indices) {
                            if (conditions[i].id == addedConditionPayload[j].conditionId) {
                                conditions[i].isSelected = true
                                conditions[i].isAlreadySelected = 1
                                conditions[i].addConditionId = addedConditionPayload[j].id
                            }
                        }
                    }
                    conditions.let { it1 -> addLovedOneConditionAdapter?.updateConditions(it1) }
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
        //Observe the response of updating loved one's medical conditions api
        addLovedOneConditionViewModel.updateConditionsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    onBackPressed()
                }
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(this, it.toString()) }
                }
            }
        }
    }

    private fun setConditionAdapter() {
        addLovedOneConditionAdapter = conditions.let { it1 ->
            AddLovedOneConditionAdapter(
                addLovedOneConditionViewModel,
                it1
            )
        }
        addLovedOneConditionAdapter?.setClickListener(this)
        binding.recyclerViewCondition.adapter = addLovedOneConditionAdapter
        handleAddedLovedOneConditionPagination()
    }

    private fun callAllMedicalCondition() {
        addLovedOneConditionViewModel.getMedicalConditions(pageNumber, limit)
    }

    private fun handleAddedLovedOneConditionPagination() {
        var isScrolling: Boolean
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisiblesItems: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > oldScrollY) {
                    isScrolling = true
                    visibleItemCount =
                        binding.recyclerViewCondition.layoutManager!!.childCount
                    totalItemCount =
                        binding.recyclerViewCondition.layoutManager!!.itemCount
                    pastVisiblesItems =
                        (binding.recyclerViewCondition.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (isScrolling && visibleItemCount + pastVisiblesItems >= totalItemCount && (currentPage < totalPage)) {
                        isScrolling = false
                        currentPage++
                        pageNumber++
                        isLoading = true
                        callAllMedicalCondition()
                    }
                }
            })

        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvNew -> {
                val intent = Intent(this, AddMedicalConditionActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            R.id.ivBack -> {
                finishActivity()
            }
            R.id.buttonFinish -> {
                // Get LovedOne UUID from shared Pref
                val lovedOneUUID =
                    Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")
                if (medicalConditionsLovedOneArray.isNotEmpty()) {
                    medicalConditionsLovedOneArray.clear()
                }
                when (intent.getStringExtra("source")) {
                    Const.MEDICAL_CONDITION -> {
                        if (addedConditionPayload.size <= 0) {
                            // api to add medical condition
                            addMedicalConditons(loveOneId)
                        } else {
                            //check if not medical condition is selected

                            selectedConditions.clear()
                            for (i in conditions) {
                                if (i.isSelected) {
                                    selectedConditions.add(i)
                                }
                            }
                            when {
                                selectedConditions.size <= 0 -> {
                                    showError(this, "Please select at least one medical condition.")
                                }
                                else -> {
                                    // api to update medical condition
                                    val deleteId: ArrayList<Int> = arrayListOf()
                                    val newAdded: ArrayList<MedicalConditionsLovedOneRequestModel> =
                                        arrayListOf()
                                    for (i in conditions) {
                                        if (i.isSelected && i.isAlreadySelected == 0) {
                                            newAdded.add(
                                                MedicalConditionsLovedOneRequestModel(
                                                    i.id,
                                                    loveOneId
                                                )
                                            )
                                        }
                                        if (i.isAlreadySelected == 2 && !i.isSelected) {
                                            deleteId.add(i.addConditionId!!)
                                        }
                                    }
                                    addLovedOneConditionViewModel.updateMedicalConditions(
                                        UpdateMedicalConditionRequestModel(newAdded, deleteId)
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        addMedicalConditons(lovedOneUUID)
                    }
                }
            }
        }
    }

    private fun addMedicalConditons(
        lovedOneUUID: String?
    ) {
        selectedConditions.clear()
        for (i in conditions) {
            if (i.isSelected && i.isAlreadySelected == 0) {
                selectedConditions.add(i)
            }
        }
        val ids = selectedConditions.map {
            it.id
        }

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
            showError(this, getString(R.string.please_select_at_least_one_condition))
        }

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

    override fun itemSelected(position: Int) {

        if (isSearched) {
            if (searchedConditions[position].isAlreadySelected == 1 && searchedConditions[position].isSelected) {
                searchedConditions[position].isAlreadySelected = 2
            }
            if (searchedConditions[position].isAlreadySelected == 2 && !searchedConditions[position].isSelected) {
                searchedConditions[position].isAlreadySelected = 1
            }
            searchedConditions[position].isSelected = !searchedConditions[position].isSelected
            searchedConditions.let { addLovedOneConditionAdapter?.updateConditions(it) }
        } else {
            if (conditions[position].isAlreadySelected == 1 && conditions[position].isSelected) {
                conditions[position].isAlreadySelected = 2
            }
            if (conditions[position].isAlreadySelected == 2 && !conditions[position].isSelected) {
                conditions[position].isAlreadySelected = 1
            }
            conditions[position].isSelected = !conditions[position].isSelected
            conditions.let { addLovedOneConditionAdapter?.updateConditions(it) }
        }
        if (isSearched) {
            for (i in conditions) {
                var found = false
                for (j in searchedConditions) {
                    if (i.id == j.id) {
                        if (j.isSelected) {
                            found = true
                            break
                        }
                    }
                }
                i.isSelected = false
                if (found) {
                    if (i.isAlreadySelected == 1 && i.isSelected) {
                        i.isAlreadySelected = 2
                    }
                    if (i.isAlreadySelected == 2 && !i.isSelected) {
                        i.isAlreadySelected = 1
                    }
                    i.isSelected = true
                }
            }
        }
    }
}

