package com.shepherd.app.ui.component.addNewMedication

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.Medlist
import com.shepherd.app.databinding.FragmentAddNewMedicationBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.addNewMedication.adapter.AddMedicineListAdapter
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.AddMedicationViewModel
import com.shepherd.app.view_model.MyMedListViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Nikita kohli on 08-08-22
 */
@AndroidEntryPoint
class AddNewMedicationFragment : BaseFragment<FragmentAddNewMedicationBinding>(),
    View.OnClickListener {
    private lateinit var fragmentAddNewMedicationBinding: FragmentAddNewMedicationBinding
    private var isSearch: Boolean = false
    private val addMedicationViewModel: AddMedicationViewModel by viewModels()
    private var pageNumber = 1
    private val limit = 10
    private var addMedicineListAdapter: AddMedicineListAdapter? = null
    private var currentPage: Int = 0
    private var totalPage: Int = 0
    private var total: Int = 0
    var medLists: ArrayList<Medlist> = arrayListOf()
    private val medListViewModel: MyMedListViewModel by viewModels()
    private var selectedMedication: Medlist? = null
    private val TAG: String = "Add medlist"
    private var searchFlag = false
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
         medLists.clear()
        searchFlag = false
        /* medListViewModel.getAllMedLists(pageNumber, limit)*/
    }

    override fun initViewBinding() {
        fragmentAddNewMedicationBinding.listener = this
        addMedicationViewModel.getAllMedLists(pageNumber, limit)

        setMedicineListAdapter()
        fragmentAddNewMedicationBinding.llSearch.visibility = View.GONE
        // Search med list
        fragmentAddNewMedicationBinding.imgCancel.setOnClickListener {
            fragmentAddNewMedicationBinding.editTextSearch.setText("")
            pageNumber = 1
            isSearch = false
            medLists.clear()
            searchFlag = false
            addMedicationViewModel.getAllMedLists(pageNumber, limit)
        }


        fragmentAddNewMedicationBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty() && searchFlag) {
                    fragmentAddNewMedicationBinding.llSearch.visibility = View.GONE
                    pageNumber = 1
                    isSearch = false
                    searchFlag = false
                    medLists.clear()
                    addMedicationViewModel.getAllMedLists(pageNumber, limit)
                } else {
                    searchFlag = true
                    fragmentAddNewMedicationBinding.llSearch.visibility = View.VISIBLE
                    //Hit search api
                    addMedicationViewModel.searchMedList(
                        pageNumber,
                        limit,
                        s.toString()
                    )
                }

            }
        })
    }


    @SuppressLint("SetTextI18n")
    override fun observeViewModel() {
        observe(addMedicationViewModel.selectedMedicationDetail, ::selectedMedicationDetail)
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
                    addMedicineListAdapter?.addData(medLists, isSearch)
                }
            }
        }

        // Observe the response of get all searched medicine list
        addMedicationViewModel.searchMedListResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    Log.d(TAG, "Get Uploaded LockBox :${it.message} ")
                    // If searched string is not available in the database , it returns 404
                    fragmentAddNewMedicationBinding.recyclerViewMedicine.visibility = View.GONE
                    fragmentAddNewMedicationBinding.txtNoMedFound.visibility =
                        View.VISIBLE
                    fragmentAddNewMedicationBinding.txtNoMedFound.text =
                        "Searched MedList Not Found..."
                }
                is DataResult.Loading -> {
                    //  showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    medLists.clear()
                    it.data.payload.let { payload ->
                        medLists = it.data.payload?.medlists!!
                        total = payload!!.total!!
                        currentPage = payload.currentPage!!
                        totalPage = payload.totalPages!!
                    }
                    if (medLists.isNullOrEmpty()) {
                        fragmentAddNewMedicationBinding.recyclerViewMedicine.visibility = View.GONE
                        fragmentAddNewMedicationBinding.txtNoMedFound.visibility =
                            View.VISIBLE
                    } else {
                        fragmentAddNewMedicationBinding.recyclerViewMedicine.visibility =
                            View.VISIBLE
                        fragmentAddNewMedicationBinding.txtNoMedFound.visibility = View.GONE
                        medLists.let { it1 ->
                            addMedicineListAdapter?.addData(
                                it1,
                                true
                            )
                        }
                    }
                }
            }
        }

    }

    private fun selectedMedicationDetail(navigateEvent: SingleEvent<Int>) {
        navigateEvent.getContentIfNotHandled()?.let {
            searchFlag = false
            selectedMedication = medLists[it]
            findNavController().navigate(
                AddNewMedicationFragmentDirections.actionAddNewMedicationToAddMedication(
                    selectedMedication!!
                ))
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
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_medication
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addMedicineListAdapter?.clearData()
    }

}

