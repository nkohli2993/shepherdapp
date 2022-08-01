package com.shepherd.app.ui.component.addNewMedication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.FragmentAddNewMedicationBinding
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.addNewMedication.adapter.AddMedicineListAdapter
import com.shepherd.app.utils.*
import com.google.android.material.snackbar.Snackbar
import com.shepherd.app.view_model.AddMedicationViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddNewMedicationFragment : BaseFragment<FragmentAddNewMedicationBinding>(),
    View.OnClickListener {

    private val addMedicationViewModel: AddMedicationViewModel by viewModels()

    private lateinit var fragmentAddNewMedicationBinding: FragmentAddNewMedicationBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddNewMedicationBinding =
            FragmentAddNewMedicationBinding.inflate(inflater, container, false)

        return fragmentAddNewMedicationBinding.root
    }

    override fun initViewBinding() {
        fragmentAddNewMedicationBinding.listener = this

        setMedicineListAdapter()
    }

    override fun observeViewModel() {
        observe(addMedicationViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(addMedicationViewModel.showSnackBar)
        observeToast(addMedicationViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { addMedicationViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentAddNewMedicationBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentAddNewMedicationBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setMedicineListAdapter() {
        val addMedicineListAdapter = AddMedicineListAdapter(addMedicationViewModel)
        fragmentAddNewMedicationBinding.recyclerViewMedicine.adapter = addMedicineListAdapter
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.btnNext -> {
                findNavController().navigate(R.id.action_add_new_medication_to_add_medication)
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_medication
    }


}

