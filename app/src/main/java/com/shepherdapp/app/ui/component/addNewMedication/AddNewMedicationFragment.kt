package com.shepherdapp.app.ui.component.addNewMedication

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.med_list.MedListData
import com.shepherdapp.app.data.dto.med_list.Medlist
import com.shepherdapp.app.databinding.FragmentAddNewMedicationBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.addNewMedication.adapter.AddMedicineListAdapter
import com.shepherdapp.app.utils.ClickType
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.view_model.AddMedicationViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Nikita kohli on 08-08-22
 */

const val TAG = "AddNewMedication"

@AndroidEntryPoint
class AddNewMedicationFragment : BaseFragment<FragmentAddNewMedicationBinding>(),
    View.OnClickListener {
    private lateinit var fragmentAddNewMedicationBinding: FragmentAddNewMedicationBinding
    private var isSearch: Boolean = false
    private var shouldSearch: Boolean = true
    private val addMedicationViewModel: AddMedicationViewModel by viewModels()
    private var pageNumber = 1
    private val limit = 15
    private var addMedicineListAdapter: AddMedicineListAdapter? = null
    private var currentPage: Int = 0
    private var totalPage: Int = 0
    private var total: Int = 0
    var medLists: ArrayList<Medlist> = arrayListOf()
    private var selectedMedication: Medlist? = null
    private var searchFlag = false
//    private var textWatcher: TextWatcher? = null

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
        Log.d(TAG, "onResume: ")
        fragmentAddNewMedicationBinding.editTextSearch.setText("")
        this.medLists.clear()
        pageNumber = 1
        isSearch = false
        searchFlag = false
        addMedicationViewModel.getAllMedLists(pageNumber, limit)
        shouldSearch = true
    }

    override fun initViewBinding() {
        fragmentAddNewMedicationBinding.listener = this
        addMedicationViewModel.getAllMedLists(pageNumber, limit)

//        fragmentAddNewMedicationBinding.editTextSearch.removeTextChangedListener(textWatcher)
        fragmentAddNewMedicationBinding.editTextSearch.setText("")
//        textWatcher = null

        setMedicineListAdapter()
        fragmentAddNewMedicationBinding.imgCancel.visibility = View.GONE

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
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {


            }

            override fun afterTextChanged(s: Editable) {
                if (!shouldSearch) return
                if (s.toString().isEmpty()) {
                    fragmentAddNewMedicationBinding.imgCancel.visibility = View.GONE
                    pageNumber = 1
                    isSearch = false
                    searchFlag = false
                    medLists.clear()
                    addMedicationViewModel.getAllMedLists(pageNumber, limit)
                } else {
                    searchFlag = true
                    fragmentAddNewMedicationBinding.imgCancel.visibility = View.VISIBLE
                    pageNumber = 1
                    //Hit search api
                    addMedicationViewModel.searchMedList(
                        pageNumber,
                        limit,
                        s.toString()
                    )
                }
            }
        })

//        fragmentAddNewMedicationBinding.editTextSearch.addTextChangedListener(textWatcher)

        /*  fragmentAddNewMedicationBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
              override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

              }

              override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                  if (s.toString().isEmpty()) {
                      fragmentAddNewMedicationBinding.imgCancel.visibility = View.GONE
                      pageNumber = 1
                      isSearch = false
                      searchFlag = false
                      medLists.clear()
                      addMedicationViewModel.getAllMedLists(pageNumber, limit)
                  } else {
                      searchFlag = true
                      fragmentAddNewMedicationBinding.imgCancel.visibility = View.VISIBLE
                      pageNumber = 1
                      //Hit search api
                      addMedicationViewModel.searchMedList(
                          pageNumber,
                          limit,
                          s.toString()
                      )
                  }
              }

              override fun afterTextChanged(s: Editable?) {


              }
          })*/
    }


    @SuppressLint("SetTextI18n")
    override fun observeViewModel() {
        observe(addMedicationViewModel.selectedMedicationDetail, ::selectedMedicationDetail)
        // Observe Get All Med List Live Data
        addMedicationViewModel.getMedListResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    fragmentAddNewMedicationBinding.recyclerViewMedicine.visibility = View.GONE
                    fragmentAddNewMedicationBinding.txtNoMedFound.visibility =
                        View.VISIBLE
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    if (pageNumber == 1) {
                        medLists.clear()
                        addMedicineListAdapter = null
                        setMedicineListAdapter()
                    }
                    var medListsAdded: ArrayList<Medlist> = arrayListOf()
                    it.data.payload.let { payload ->
                        medListsAdded = payload?.medlists!!
                        total = payload.total!!
                        currentPage = payload.currentPage!!
                        totalPage = payload.totalPages!!
                        pageNumber = currentPage + 1
                    }
                    medLists.addAll(medListsAdded)
                    medLists = medLists.distinctBy {
                        it.id
                    } as ArrayList<Medlist>
                    if (medListsAdded.isEmpty()) return@observeEvent

                    if (medListsAdded.isEmpty()) {
                        fragmentAddNewMedicationBinding.recyclerViewMedicine.visibility = View.GONE
                        fragmentAddNewMedicationBinding.txtNoMedFound.visibility =
                            View.VISIBLE
                    } else {
                        fragmentAddNewMedicationBinding.recyclerViewMedicine.visibility =
                            View.VISIBLE
                        fragmentAddNewMedicationBinding.txtNoMedFound.visibility = View.GONE
                        medListsAdded.let { it1 ->
                            addMedicineListAdapter?.addData(
                                it1,
                                false
                            )
                        }
                    }
                }
            }
        }

        // Observe the response of get all searched medicine list
        addMedicationViewModel.searchMedListResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
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
                    if (medLists.isEmpty()) {
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

    private fun selectedMedicationDetail(navigateEvent: SingleEvent<MedListData>) {
        navigateEvent.getContentIfNotHandled()?.let {
            if (it.type == ClickType.View.value) {
                searchFlag = false
                Log.e("catch_exception", "position: $it medication ${medLists[it.position!!]}")
                selectedMedication = medLists[it.position!!]
                shouldSearch = false
                fragmentAddNewMedicationBinding.editTextSearch.setText("")
                findNavController().navigate(
                    AddNewMedicationFragmentDirections.actionAddNewMedicationToAddMedication(
                        selectedMedication!!
                    )
                )
            } else if (it.type == ClickType.Edit.value) {
                selectedMedication = medLists[it.position!!]
//                showToast("Edit clicked: Selected medicine is : $selectedMedication")
                val action =
                    AddNewMedicationFragmentDirections.actionNavAddNewMedicine(medicine = selectedMedication)
                findNavController().navigate(action)
            }
        }
    }


    private fun setMedicineListAdapter() {
        addMedicineListAdapter = AddMedicineListAdapter(addMedicationViewModel)
        fragmentAddNewMedicationBinding.recyclerViewMedicine.adapter = addMedicineListAdapter
        handleMedicationPagination()
    }

    private fun handleMedicationPagination() {
        var isScrolling: Boolean
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisiblesItems: Int
        fragmentAddNewMedicationBinding.recyclerViewMedicine.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    isScrolling = true
                    visibleItemCount = recyclerView.layoutManager!!.childCount
                    totalItemCount = recyclerView.layoutManager!!.itemCount
                    pastVisiblesItems =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (isScrolling && visibleItemCount + pastVisiblesItems >= totalItemCount && (currentPage < totalPage)) {
                        isScrolling = false
//                        currentPage++
//                        pageNumber++
                        addMedicationViewModel.getAllMedLists(pageNumber, limit)
                    }
                }
            }
        })
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.btnNext -> {
            }
            R.id.tvNew -> {
                val action =
                    AddNewMedicationFragmentDirections.actionNavAddNewMedicine(medicine = null)
                findNavController().navigate(action)
//                findNavController().navigate(R.id.action_nav_add_new_medicine)
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_medication
    }

    override fun onDestroyView() {
//        textWatcher = null
        addMedicineListAdapter?.clearData()
        fragmentAddNewMedicationBinding.editTextSearch.setText("")
//        fragmentAddNewMedicationBinding.editTextSearch.removeTextChangedListener(textWatcher)
        Log.d(TAG, "onDestroyView: ")
        pageNumber = 1
        super.onDestroyView()
    }
}

