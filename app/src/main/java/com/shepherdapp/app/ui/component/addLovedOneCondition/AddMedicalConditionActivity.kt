package com.shepherdapp.app.ui.component.addLovedOneCondition

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.medical_conditions.AddMedicalConditionRequestModel
import com.shepherdapp.app.data.dto.medical_conditions.Conditions
import com.shepherdapp.app.databinding.ActivityAddMedicalConditionBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.utils.Type
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.AddLovedOneConditionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMedicalConditionActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddMedicalConditionBinding
    private val addLovedOneConditionViewModel: AddLovedOneConditionViewModel by viewModels()
    private var medicalConditionName: String? = null
    private var description: String? = null
    private var conditionName: String? = null
    private var conditionId: Int? = null
    private var conditionDesc: String? = null
    private var isEditCondition: Boolean = false

    override fun observeViewModel() {
        // Observe response of Add Loved One Medical Condition
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

        // Observe response of Edit Loved One Medical Condition
        addLovedOneConditionViewModel.editConditionResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(this, it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(this, it.data.message.toString())
                }
            }
        }

    }

    override fun initViewBinding() {
        binding = ActivityAddMedicalConditionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        when (intent.getStringExtra("type")) {
            Type.ADD.value -> {
                binding.tvMedList.text = getString(R.string.add_medical_condition)
                isEditCondition = false
            }
            Type.EDIT.value -> {
                binding.tvMedList.text = getString(R.string.edit_medical_condition)
                isEditCondition = true
                val condition = if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra("condition", Conditions::class.java)
                } else {
                    intent.getParcelableExtra("condition")
                }
                conditionName = condition?.name
                conditionDesc = condition?.description
                conditionId = condition?.id

                // Set condition name
                binding.medicineNameET.setText(conditionName)
                // Set description
                binding.etDescription.setText(conditionDesc)

                binding.btnSubmit.text = getString(R.string.save_changes)
            }
        }
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
                    // isEditCondition is true if we need to edit Medical Condition
                    if (isEditCondition) {
                        conditionId?.let {
                            addLovedOneConditionViewModel.editMedicalCondition(
                                AddMedicalConditionRequestModel(
                                    name = medicalConditionName,
                                    description = description,
                                    createdBy = "user"
                                ),
                                it
                            )
                        }

                    } else {
                        addLovedOneConditionViewModel.addMedicalConditions(
                            AddMedicalConditionRequestModel(
                                medicalConditionName,
                                description,
                                "user"
                            )
                        )
                    }


                }
            }
        }
    }
}