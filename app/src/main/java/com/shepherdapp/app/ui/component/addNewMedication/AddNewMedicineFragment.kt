package com.shepherdapp.app.ui.component.addNewMedication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.med_list.Medlist
import com.shepherdapp.app.data.dto.med_list.add_med_list.AddMedListRequestModel
import com.shepherdapp.app.data.dto.medical_conditions.AddMedicalConditionRequestModel
import com.shepherdapp.app.databinding.FragmentAddNewMedicationBinding
import com.shepherdapp.app.databinding.FragmentAddNewMedicineBinding
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.addNewMedication.adapter.AddMedicineListAdapter
import com.shepherdapp.app.view_model.AddMedicationViewModel


class AddNewMedicineFragment : BaseFragment<FragmentAddNewMedicineBinding>(),
    View.OnClickListener {
    private lateinit var fragmentAddNewMedicineBinding: FragmentAddNewMedicineBinding
    private val addMedicationViewModel: AddMedicationViewModel by viewModels()
    private var medicationName: String? = null
    private var description: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddNewMedicineBinding =
            FragmentAddNewMedicineBinding.inflate(inflater, container, false)
        return fragmentAddNewMedicineBinding.root
    }

    override fun initViewBinding() {
        fragmentAddNewMedicineBinding.listener = this

    }

    override fun observeViewModel() {

    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_medicine
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
            }
            R.id.btnSubmit -> {
                if (isValid) {
                    if (binding.medicineNameET.text.toString().trim().isNotEmpty()) {
                        medicationName = binding.medicineNameET.text.toString().trim()
                    }
                    if (binding.etDescription.text.toString().trim().isNotEmpty()) {
                        description = binding.etDescription.text.toString().trim()
                    }
                    addMedicationViewModel.addNewMedlistMedicine(
                        AddMedListRequestModel(medicationName, description, "user")
                    )
                }
            }
        }
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

}