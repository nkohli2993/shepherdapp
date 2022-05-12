package com.app.shepherd.ui.component.addMedicationDetails

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.FragmentAddMedicationDetailsBinding
import com.app.shepherd.databinding.FragmentAddNewMedicationBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.addMedicationDetails.adapter.AddMedicationDaysAdapter
import com.app.shepherd.ui.component.addMedicationDetails.adapter.AddMedicationDoseAdapter
import com.app.shepherd.ui.component.addMedicationDetails.adapter.AddMedicationFrequencyAdapter
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_medication_details.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddMedicationDetailsFragment : BaseFragment<FragmentAddNewMedicationBinding>(),
    View.OnClickListener {

    private val addMedicationDetailViewModel: AddMedicationDetailViewModel by viewModels()

    private lateinit var fragmentAddMedicationDetailsBinding: FragmentAddMedicationDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddMedicationDetailsBinding =
            FragmentAddMedicationDetailsBinding.inflate(inflater, container, false)

        return fragmentAddMedicationDetailsBinding.root
    }

    override fun initViewBinding() {
        fragmentAddMedicationDetailsBinding.listener = this

        initTimePicker()

        setDoseAdapter()
        setFrequencyAdapter()
        setDaysAdapter()
    }

    override fun observeViewModel() {
        observe(addMedicationDetailViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(addMedicationDetailViewModel.showSnackBar)
        observeToast(addMedicationDetailViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { addMedicationDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentAddMedicationDetailsBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentAddMedicationDetailsBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setDoseAdapter() {
        val addMedicationDoseAdapter = AddMedicationDoseAdapter(addMedicationDetailViewModel)
        fragmentAddMedicationDetailsBinding.recyclerViewDose.adapter = addMedicationDoseAdapter

    }

    private fun setFrequencyAdapter() {
        val addMedicationFrequencyAdapter =
            AddMedicationFrequencyAdapter(addMedicationDetailViewModel)
        fragmentAddMedicationDetailsBinding.recyclerViewFrequency.adapter =
            addMedicationFrequencyAdapter
    }

    private fun setDaysAdapter() {
        val addMedicationDaysAdapter = AddMedicationDaysAdapter(addMedicationDetailViewModel)
        fragmentAddMedicationDetailsBinding.recyclerViewDays.adapter = addMedicationDaysAdapter
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                backPress()
            }
            R.id.buttonAddNewMedication -> {
                startActivity(Intent(requireContext(), HomeActivity::class.java))
            }
            R.id.textViewDose -> {
                manageVisibility(fragmentAddMedicationDetailsBinding.recyclerViewDose)
            }
            R.id.textViewFrequency -> {
                manageVisibility(fragmentAddMedicationDetailsBinding.recyclerViewFrequency)
            }
            R.id.textViewDays -> {
                manageVisibility(fragmentAddMedicationDetailsBinding.recyclerViewDays)
            }
        }
    }

    private fun manageVisibility(recyclerView: RecyclerView) {
        if (recyclerView.isVisible)
            recyclerView.toGone()
        else
            recyclerView.toVisible()

    }


    private fun initTimePicker() {
        textViewSelectTime.timePicker(
            requireContext()
        )
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_medication_details
    }


}

