package com.app.shepherd.ui.component.lockBox

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.app.shepherd.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.app.shepherd.databinding.FragmentLockboxBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.lockBox.adapter.OtherDocumentsAdapter
import com.app.shepherd.ui.component.lockBox.adapter.RecommendedDocumentsAdapter
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.observe
import com.app.shepherd.view_model.LockBoxViewModel
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

