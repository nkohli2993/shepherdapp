package com.shepherd.app.ui.component.addNewMedication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.med_list.Medlist
import com.shepherd.app.databinding.FragmentAddNewMedicationBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.addNewMedication.adapter.AddMedicineListAdapter
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.view_model.AddMedicationViewModel
import com.shepherd.app.view_model.MyMedListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddNewMedicationFragment : BaseFragment<FragmentAddNewMedicationBinding>(),
    View.OnClickListener {

    private val addMedicationViewModel: AddMedicationViewModel by viewModels()

    private lateinit var fragmentAddNewMedicationBinding: FragmentAddNewMedicationBinding
    private var pageNumber = 1
    private val limit = 10
    private var addMedicineListAdapter: AddMedicineListAdapter? = null
    var currentPage: Int = 0
    var totalPage: Int = 0
    var total: Int = 0
    var pageCount: Int = 0
    var medLists: ArrayList<Medlist> = arrayListOf()
    private val medListViewModel: MyMedListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddNewMedicationBinding =
            FragmentAddNewMedicationBinding.inflate(inflater, container, false)

        return fragmentAddNewMedicationBinding.root
    }

    override fun onResume() {
        super.onResume()
        medListViewModel.getAllMedLists(pageNumber,limit)
    }
    override fun initViewBinding() {
        fragmentAddNewMedicationBinding.listener = this
        addMedicationViewModel.getAllMedLists(pageNumber, limit)

        setMedicineListAdapter()
        // Search Care Team Members
        fragmentAddNewMedicationBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {

                }

            }
        })
    }

    override fun observeViewModel() {

        // Observe Get All Med List Live Data
        addMedicationViewModel.getMedListResponseLiveData.observeEvent(this) {
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
                    it.data.payload.let { payload ->
                        medLists = payload?.medlists!!
                        total = payload.total!!
                        currentPage = payload.currentPage!!
                        totalPage = payload.totalPages!!

                    }

                    if (medLists.isNullOrEmpty()) return@observeEvent
                    addMedicineListAdapter?.addData(medLists)

                }
            }
        }
    }


    private fun setMedicineListAdapter() {
        addMedicineListAdapter = AddMedicineListAdapter(addMedicationViewModel)
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

