package com.app.shepherd.ui.component.addNewMedication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.FragmentAddNewMedicationBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.addNewMedication.adapter.AddMedicineListAdapter
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
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
            R.id.imageViewBack -> {
                backPress()
            }
            R.id.buttonAddNewMedication -> {
                findNavController().navigate(R.id.action_add_new_medication_to_add_medication_details)
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_medication
    }


}

