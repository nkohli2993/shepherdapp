package com.shepherd.app.ui.component.lockBox

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
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherd.app.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.shepherd.app.databinding.FragmentLockboxBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.lockBox.adapter.OtherDocumentsAdapter
import com.shepherd.app.ui.component.lockBox.adapter.RecommendedDocumentsAdapter
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.LockBoxViewModel
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

    private val pageNumber = 1
    private val limit = 10

    var lockBoxTypes: ArrayList<LockBoxTypes>? = arrayListOf()
    var lockBoxList: ArrayList<LockBox>? = arrayListOf()
    var searchedLockBoxList: ArrayList<LockBox>? = arrayListOf()
    var search: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLockboxBinding =
            FragmentLockboxBinding.inflate(inflater, container, false)

        lockBoxViewModel.getAllLockBoxUploadedDocumentsByLovedOneUUID(pageNumber, limit)


        return fragmentLockboxBinding.root
    }

    override fun initViewBinding() {
        fragmentLockboxBinding.listener = this
        lockBoxViewModel.getAllLockBoxTypes(pageNumber, limit)
        setRecommendedDocumentsAdapter()
        setOtherDocumentsAdapter()
        fragmentLockboxBinding.cvRecommendedDocuments.setOnClickListener {
            val action = LockBoxFragmentDirections.actionNavLockBoxToAddNewLockBoxFragment(null)
            findNavController().navigate(action)
        }

        fragmentLockboxBinding.layoutRecommendedDoc.setOnClickListener {
            val action = LockBoxFragmentDirections.actionNavLockBoxToAddNewLockBoxFragment(null)
            findNavController().navigate(action)
        }

        fragmentLockboxBinding.imgCancel.setOnClickListener {
            fragmentLockboxBinding.editTextSearch.setText("")
        }

        fragmentLockboxBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.isNotEmpty()) {
                        fragmentLockboxBinding.imgCancel.visibility = View.VISIBLE
                        searchedLockBoxList?.clear()
                        searchedLockBoxList = lockBoxList?.filter {
                            it.name?.startsWith(
                                s,
                                true
                            ) == true
                        } as ArrayList<LockBox>

                        // Show No Uploaded Doc Found when Doc is available during search
                        if (searchedLockBoxList.isNullOrEmpty()) {
                            fragmentLockboxBinding.rvOtherDocuments.visibility = View.GONE
                            fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility =
                                View.VISIBLE
                        } else {
                            fragmentLockboxBinding.rvOtherDocuments.visibility = View.VISIBLE
                            fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility =
                                View.GONE
                        }

                        searchedLockBoxList.let {
                            it?.let { it1 -> otherDocumentsAdapter?.addData(it1) }
                        }
                    } else {
                        lockBoxList?.let { otherDocumentsAdapter?.addData(it) }
                        fragmentLockboxBinding.rvOtherDocuments.visibility = View.VISIBLE
                        fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility =
                            View.GONE
                    }
                }
            }

        })

    }

    override fun observeViewModel() {
        observe(lockBoxViewModel.openUploadedDocDetail, ::openUploadedDocDetail)
        observe(lockBoxViewModel.createRecommendedLockBoxDocLiveData, ::createRecommendedLockBoxDoc)


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
                    lockBoxTypes = it.data.payload?.lockBoxTypes
                    if (lockBoxTypes.isNullOrEmpty()) return@observeEvent
                    recommendedDocumentsAdapter?.addData(lockBoxTypes!!)
                }
            }
        }

        // Observe the response of get all uploaded lock box document by loved one uuid api
        lockBoxViewModel.getUploadedLockBoxDocResponseLiveData.observeEvent(this) {
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
                    lockBoxList = it.data.payload?.lockBox
                    if (lockBoxList.isNullOrEmpty()) {
                        fragmentLockboxBinding.rvOtherDocuments.visibility = View.GONE
                        fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility =
                            View.VISIBLE
                    } else {
                        fragmentLockboxBinding.rvOtherDocuments.visibility = View.VISIBLE
                        fragmentLockboxBinding.txtNoUploadedLockBoxFile.visibility = View.GONE
                        lockBoxList?.let { it1 -> otherDocumentsAdapter?.addData(it1) }
                    }
                }
            }
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
            // Sending CareTeam object through safeArgs
            val action = LockBoxFragmentDirections.actionLockBoxToLockBoxDocInfo(it)
            findNavController().navigate(action)
        }
    }


    private fun setRecommendedDocumentsAdapter() {
        recommendedDocumentsAdapter = RecommendedDocumentsAdapter(lockBoxViewModel)
        fragmentLockboxBinding.rvRecommendedDoc.adapter = recommendedDocumentsAdapter

    }

    private fun setOtherDocumentsAdapter() {
        otherDocumentsAdapter = OtherDocumentsAdapter(lockBoxViewModel)
        fragmentLockboxBinding.rvOtherDocuments.adapter = otherDocumentsAdapter

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

}

