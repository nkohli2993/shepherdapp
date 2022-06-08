package com.app.shepherd.ui.component.addLovedOneCondition

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.shepherd.R
import com.app.shepherd.data.dto.medical_conditions.Conditions
import com.app.shepherd.databinding.ActivityAddLovedOneConditionBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.addLovedOneCondition.adapter.AddLovedOneConditionAdapter
import com.app.shepherd.ui.component.welcome.WelcomeActivity
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.view_model.AddLovedOneConditionViewModel
import dagger.hilt.android.AndroidEntryPoint


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolBarNew.listener = this
        binding.listener = this

        binding.recyclerViewCondition.layoutManager = LinearLayoutManager(this)

        // Get Medical conditions
        addLovedOneConditionViewModel.getMedicalConditions(pageNumber, limit)

        binding.imgCancel.setOnClickListener { binding.editTextSearch.setText("") }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if(s.isEmpty()) {
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
        addLovedOneConditionViewModel.medicalConditionResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    conditions = it.data.payload?.conditions
                    addLovedOneConditionAdapter = conditions?.let { it1 ->
                        AddLovedOneConditionAdapter(addLovedOneConditionViewModel,
                            it1
                        )
                    }
                    addLovedOneConditionAdapter?.setClickListener(this)
                    binding.recyclerViewCondition.adapter = addLovedOneConditionAdapter
                }

                is DataResult.Failure -> {
                    hideLoading()
                    it.errorCode?.let { showError(this, it.toString()) }

                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                finishActivity()
            }
            R.id.buttonFinish -> {
                navigateToWelcomeScreen()
            }
        }
    }


    private fun navigateToWelcomeScreen() {
        startActivityWithFinishAffinity<WelcomeActivity>()
    }

    override fun itemSelected(conditions: Conditions) {

        if (selectedConditions?.isEmpty() == true)
            conditions.let { selectedConditions?.add(it) }
        else if (conditions.isSelected == true) selectedConditions?.add(conditions)
        else if(conditions.isSelected == false && selectedConditions?.contains(conditions) == true)
            selectedConditions?.remove(conditions)
    }

}

