package com.shepherdapp.app.ui.component.lockBox

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherdapp.app.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.shepherdapp.app.databinding.FragmentLockboxBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.lockBox.adapter.OtherDocumentsAdapter
import com.shepherdapp.app.ui.component.lockBox.adapter.RecommendedDocumentsAdapter
import com.shepherdapp.app.utils.ClickType
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.hideKeyboard
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.view_model.LockBoxViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class LockBoxFragment : BaseFragment<FragmentLockboxBinding>(),
    View.OnClickListener {


    private val TAG: String = "LockBoxFragment"
    private val lockBoxViewModel: LockBoxViewModel by viewModels()
    private lateinit var fragmentLockboxBinding: FragmentLockboxBinding
    private var recommendedDocumentsAdapter: RecommendedDocumentsAdapter? = null
    private var otherDocumentsAdapter: OtherDocumentsAdapter? = null
    private var pageNumber = 1
    private val limit = 20
    private var isSearch = false
    var lockBoxTypes: ArrayList<LockBoxTypes> = arrayListOf()
    var lockBoxList: ArrayList<LockBox>? = arrayListOf()
    var search: String? = null
    var currentPage: Int = 0
    var totalPage: Int = 0
    var total: Int = 0
    private var deletePostion: Int = -1


    private var parentActivityListener: ChildFragmentToActivityListener? = null

    private lateinit var homeActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            homeActivity = context
        }
        if (context is ChildFragmentToActivityListener) parentActivityListener = context
        else throw RuntimeException("$context must implement ChildFragmentToActivityListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLockboxBinding =
            FragmentLockboxBinding.inflate(inflater, container, false)
        return fragmentLockboxBinding.root
    }

    override fun initViewBinding() {
        fragmentLockboxBinding.listener = this
        lockBoxViewModel.getAllLockBoxTypes(pageNumber, limit, true)
        resetPageNumber()
        lockBoxList?.clear()
        lockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(pageNumber, limit)
        setRecommendedDocumentsAdapter()
        setOtherDocumentsAdapter()

        fragmentLockboxBinding.imgCancel.setOnClickListener {
            fragmentLockboxBinding.editTextSearch.setText("")
            fragmentLockboxBinding.layoutRecommendedDoc.visibility = View.VISIBLE
            resetPageNumber()
            isSearch = false
            lockBoxList!!.clear()

            lockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(pageNumber, limit)
        }

        fragmentLockboxBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                lengthBefore: Int,
                lengthAfter: Int
            ) {
                if (s.toString().isEmpty() && (lengthBefore == 0 && lengthAfter == 0)) {
                    fragmentLockboxBinding.imgCancel.visibility = View.GONE
                    fragmentLockboxBinding.layoutRecommendedDoc.visibility = View.VISIBLE
                } else if (s.toString().isEmpty() && (lengthBefore > 0 && lengthAfter >= 0)) {
                    fragmentLockboxBinding.imgCancel.visibility = View.GONE
                    fragmentLockboxBinding.layoutRecommendedDoc.visibility = View.VISIBLE
                    resetPageNumber()
                    isSearch = false
                    lockBoxList!!.clear()
                    lockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(pageNumber, limit)
                } else {
                    fragmentLockboxBinding.imgCancel.visibility = View.VISIBLE
                    fragmentLockboxBinding.layoutRecommendedDoc.visibility = View.GONE
                    //Hit search api
                    lockBoxViewModel.searchAllLockBoxUploadedDocumentsByLovedOneUUID(
                        pageNumber,
                        limit,
                        s.toString()
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        // Handle click of search edit text
        fragmentLockboxBinding.editTextSearch.setOnEditorActionListener(TextView.OnEditorActionListener { textView, actionID, keyEvent ->
            if (actionID == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                resetPageNumber()
                val searchData = fragmentLockboxBinding.editTextSearch.text.toString().trim()
                if (searchData.isEmpty()) {
                    fragmentLockboxBinding.imgCancel.visibility = View.GONE
                    fragmentLockboxBinding.layoutRecommendedDoc.visibility = View.VISIBLE
                    resetPageNumber()
                    isSearch = false
                    lockBoxList!!.clear()
                    lockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(pageNumber, limit)
                } else {
                    //Hit search api
                    lockBoxViewModel.searchAllLockBoxUploadedDocumentsByLovedOneUUID(
                        pageNumber,
                        limit,
                        searchData
                    )
                }
                return@OnEditorActionListener true
            }
            false
        })

    }

    private fun resetPageNumber() {
        pageNumber = 1
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observeViewModel() {
        observe(lockBoxViewModel.openUploadedDocDetail, ::openUploadedDocDetail)
        observe(lockBoxViewModel.createRecommendedLockBoxDocLiveData, ::createRecommendedLockBoxDoc)
        observe(lockBoxViewModel.viewRecommendedLockBoxDocLiveData, ::viewRecommendedLockBoxDoc)


        lockBoxViewModel.lockBoxTypeResponseLiveData.observeEvent(this) {
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
                    lockBoxTypes.clear()
                    if (it.data.payload?.lockBoxTypes != null || it.data.payload?.lockBoxTypes!!.size > 0) {
                        for (i in it.data.payload?.lockBoxTypes!!) {
                            i.isAdded = false
//                            if (i.lockbox != null && i.lockbox.size > 0 && i.name?.lowercase() != "other") {
                            if (i.lockbox != null && i.lockbox.size > 0) {
                                i.isAdded = true
                            }
                            lockBoxTypes.add(i)
                        }
                    }
                    if (lockBoxTypes.isEmpty()) return@observeEvent
                    recommendedDocumentsAdapter?.addData(lockBoxTypes)
                }
            }
        }
        lockBoxViewModel.deleteLockBoxDocResponseLiveData.observeEvent(this) {
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
                    showInfo(requireContext(), getString(R.string.lockbox_deleted_successfully))
                    //remove from list
                    lockBoxList!!.removeAt(deletePostion)
                    otherDocumentsAdapter!!.setList(lockBoxList!!)
                    otherDocumentsAdapter!!.notifyDataSetChanged()
                    if (lockBoxList.isNullOrEmpty()) {
                        fragmentLockboxBinding.rvOtherDocuments.visibility = View.GONE
                        fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility =
                            View.VISIBLE
                    } else {
                        fragmentLockboxBinding.rvOtherDocuments.visibility = View.VISIBLE
                        fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility = View.GONE
                    }

                }
            }
        }

        // Observe the response of get all uploaded lock box document by loved one uuid api
        lockBoxViewModel.getUploadedLockBoxDocResponseLiveData.observeEvent(this) { it ->
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    Log.d(TAG, "Get Uploaded LockBox :${it.message} ")
//                    it.message?.let { showError(requireContext(), it.toString()) }
                    fragmentLockboxBinding.rvOtherDocuments.visibility = View.GONE
                    fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility =
                        View.VISIBLE
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    lockBoxList?.clear()
                    if (pageNumber == 1) {
                        otherDocumentsAdapter = null
                        setOtherDocumentsAdapter()
                    }
                    it.data.payload.let {
                        lockBoxList = it?.lockBox
                        total = it?.total!!
                        currentPage = it.currentPage!!
                        totalPage = it.totalPages!!
                    }
                    if (lockBoxList.isNullOrEmpty()) {
                        fragmentLockboxBinding.rvOtherDocuments.visibility = View.GONE
                        fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility =
                            View.VISIBLE
                    } else {
                        fragmentLockboxBinding.rvOtherDocuments.visibility = View.VISIBLE
                        fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility = View.GONE
                        lockBoxList?.let { it1 -> otherDocumentsAdapter?.addData(it1, isSearch) }
                    }
                }
            }
        }

        // Observe the response of get all searched uploaded lock box document by loved one uuid api
        lockBoxViewModel.getSearchedUploadedLockBoxDocResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    Log.d(TAG, "Get Uploaded LockBox :${it.message} ")
                    // If searched string is not available in the database , it returns 404
                    fragmentLockboxBinding.rvOtherDocuments.visibility = View.GONE
                    fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility =
                        View.VISIBLE
                    fragmentLockboxBinding.recommendedTxt.visibility =
                        View.GONE
                    fragmentLockboxBinding.txtNoUploadedLockBoxFile.text =
                        getString(R.string.searched_lockbox_file_not_found)
                }
                is DataResult.Loading -> {
                    //  showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    lockBoxList?.clear()
                    lockBoxList = it.data.payload?.lockBox
                    it.data.payload.let { it1 ->
                        totalPage = it1?.totalPages!!
                        total = it1.total!!
                        currentPage = it1.currentPage!!
                    }
                    if (lockBoxList.isNullOrEmpty()) {
                        fragmentLockboxBinding.rvOtherDocuments.visibility = View.GONE
                        fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility =
                            View.VISIBLE
                    } else {
                        fragmentLockboxBinding.rvOtherDocuments.visibility = View.VISIBLE
                        fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility = View.GONE
                        fragmentLockboxBinding.recommendedTxt.visibility =
                            View.VISIBLE
                        lockBoxList?.let { it1 ->
                            otherDocumentsAdapter?.addData(
                                it1,
                                true
                            )
                        }
                    }
                }
            }
        }


    }

    private fun viewRecommendedLockBoxDoc(singleEvent: SingleEvent<LockBoxTypes>) {
        singleEvent.getContentIfNotHandled()?.let {
            //Get id of uploaded lock box
            val lockBoxId = it.lockbox.first().id
            val action =
                lockBoxId?.let { it1 -> LockBoxFragmentDirections.actionLockBoxToLockBoxDocInfo(it1) }
            action?.let { it1 -> findNavController().navigate(it1) }
        }
    }

    private fun createRecommendedLockBoxDoc(singleEvent: SingleEvent<LockBoxTypes>) {
        singleEvent.getContentIfNotHandled()?.let {
            // Sending LockBoxTypes object through safeArgs
            val action = LockBoxFragmentDirections.actionNavLockBoxToAddNewLockBoxFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun openUploadedDocDetail(navigateEvent: SingleEvent<LockBox>) {
        navigateEvent.getContentIfNotHandled()?.let {
            Log.d(TAG, "Uploaded Doc detail :$it")
            if (it.clickType == ClickType.View.value) {
                val lockBoxId = it.id
                val action = lockBoxId?.let { it1 ->
                    LockBoxFragmentDirections.actionLockBoxToLockBoxDocInfo(
                        it1
                    )
                }
                if (action != null) {
                    findNavController().navigate(action)
                }
            } else {
                //show delete dialog
                val builder = AlertDialog.Builder(requireContext())
                val dialog = builder.apply {
                    setTitle("Delete LockBox Document")
                    setMessage("Sure you want to delete this LockBox document")
                    setPositiveButton("Yes") { _, _ ->
                        deletePostion = it.deletePosition!!
                        lockBoxViewModel.deleteAddedLockBoxDocumentBYID(it.id!!)
                    }
                    setNegativeButton("Cancel") { _, _ ->

                    }
                }.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            }

        }
    }


    private fun setRecommendedDocumentsAdapter() {
        recommendedDocumentsAdapter = RecommendedDocumentsAdapter(lockBoxViewModel)
        fragmentLockboxBinding.rvRecommendedDoc.adapter = recommendedDocumentsAdapter
    }

    private fun setOtherDocumentsAdapter() {
        otherDocumentsAdapter = OtherDocumentsAdapter(lockBoxViewModel)
        fragmentLockboxBinding.rvOtherDocuments.adapter = otherDocumentsAdapter
        handleUploadedDocumentsPagination()
    }

    private fun handleUploadedDocumentsPagination() {
        var isScrolling = true
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisiblesItems: Int
        fragmentLockboxBinding.rvOtherDocuments.addOnScrollListener(object :
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
                        currentPage++
                        pageNumber++
                        lockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(
                            pageNumber,
                            limit
                        )
                    }
                }
            }
        })
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            /* R.id.buttonNewDocument -> {
                 p0.findNavController().navigate(R.id.action_lock_box_to_lock_box_doc_info)
             }*/
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_lockbox
    }

    override fun onResume() {
        parentActivityListener?.msgFromChildFragmentToActivity()
        super.onResume()

    }
}

