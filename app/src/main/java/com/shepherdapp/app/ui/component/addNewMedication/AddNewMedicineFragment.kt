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
import com.shepherdapp.app.databinding.FragmentAddNewMedicineBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.AddMedicationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
        addMedicationViewModel.addMedicineResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    it.data.message?.let { it1 -> showSuccess(requireContext(), it1) }
                    backPress()
                }
            }
        }


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
                    if (fragmentAddNewMedicineBinding.medicineNameET.text.toString().trim().isNotEmpty()) {
                        medicationName = fragmentAddNewMedicineBinding.medicineNameET.text.toString().trim()
                    }
                    if (fragmentAddNewMedicineBinding.etDescription.text.toString().trim().isNotEmpty()) {
                        description = fragmentAddNewMedicineBinding.etDescription.text.toString().trim()
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
                fragmentAddNewMedicineBinding.medicineNameET.text.toString().isEmpty() -> {
                    fragmentAddNewMedicineBinding.medicineNameET.error =
                        getString(R.string.please_enter_medical_condition)
                    fragmentAddNewMedicineBinding.medicineNameET.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }

}