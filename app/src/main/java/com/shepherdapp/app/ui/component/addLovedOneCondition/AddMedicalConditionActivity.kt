package com.shepherdapp.app.ui.component.addLovedOneCondition

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.medical_conditions.AddMedicalConditionRequestModel
import com.shepherdapp.app.databinding.ActivityAddMedicalConditionBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.AddLovedOneConditionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMedicalConditionActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddMedicalConditionBinding
    private val addLovedOneConditionViewModel: AddLovedOneConditionViewModel by viewModels()
    private var medicalConditionName: String? = null
    private var description: String? = null
    override fun observeViewModel() {
        addLovedOneConditionViewModel.addedConditionsResponseLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    onBackPressed()
                }
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(this, it) }
                }
            }
        }

    }

    override fun initViewBinding() {
        binding = ActivityAddMedicalConditionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this
    }

    private val isValid: Boolean
        get() {
            when {
                binding.medicineNameET.text.toString().isEmpty() -> {
                    binding.medicineNameET.error =
                        getString(R.string.please_enter_medical_condition)
                    binding.medicineNameET.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                finishActivity()
            }
            R.id.btnSubmit -> {
                if (isValid) {
                    if (binding.medicineNameET.text.toString().trim().isNotEmpty()) {
                        medicalConditionName = binding.medicineNameET.text.toString().trim()
                    }
                    if (binding.etDescription.text.toString().trim().isNotEmpty()) {
                        description = binding.etDescription.text.toString().trim()
                    }
                    addLovedOneConditionViewModel.addMedicalConditions(
                        AddMedicalConditionRequestModel(medicalConditionName, description, "user")
                    )
                }
            }
        }
    }
}