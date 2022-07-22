package com.app.shepherd.ui.component.lockBox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.shepherd.R
import com.app.shepherd.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.app.shepherd.databinding.FragmentLockboxBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.lockBox.adapter.OtherDocumentsAdapter
import com.app.shepherd.ui.component.lockBox.adapter.RecommendedDocumentsAdapter
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.view_model.LockBoxViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class LockBoxFragment : BaseFragment<FragmentLockboxBinding>(),
    View.OnClickListener {

    private val lockBoxViewModel: LockBoxViewModel by viewModels()
    private lateinit var fragmentLockboxBinding: FragmentLockboxBinding
    private var recommendedDocumentsAdapter: RecommendedDocumentsAdapter? = null

    private val pageNumber = 1
    private val limit = 10

    var lockBoxTypes: ArrayList<LockBoxTypes>? = arrayListOf()


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
        lockBoxViewModel.getAllLockBoxTypes(pageNumber, limit)
        setRecommendedDocumentsAdapter()
        setOtherDocumentsAdapter()
    }

    override fun observeViewModel() {
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
    }


    private fun setRecommendedDocumentsAdapter() {
        recommendedDocumentsAdapter = RecommendedDocumentsAdapter(lockBoxViewModel)
        fragmentLockboxBinding.rvRecommendedDoc.adapter = recommendedDocumentsAdapter

    }

    private fun setOtherDocumentsAdapter() {
        val otherDocumentsAdapter = OtherDocumentsAdapter(lockBoxViewModel)
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

