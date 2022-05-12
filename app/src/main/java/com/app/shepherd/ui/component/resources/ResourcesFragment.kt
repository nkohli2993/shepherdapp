package com.app.shepherd.ui.component.resources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.FragmentMessagesBinding
import com.app.shepherd.databinding.FragmentResourcesBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.messages.adapter.DiscussionGroupsAdapter
import com.app.shepherd.ui.component.resources.adapter.MedicalHistoryAdapter
import com.app.shepherd.ui.component.resources.adapter.MedicalHistoryTopicsAdapter
import com.app.shepherd.ui.component.resources.adapter.TopicsAdapter
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class ResourcesFragment : BaseFragment<FragmentMessagesBinding>() {

    private val resourcesViewModel: ResourcesViewModel by viewModels()

    private lateinit var fragmentResourcesBinding: FragmentResourcesBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentResourcesBinding =
            FragmentResourcesBinding.inflate(inflater, container, false)

        return fragmentResourcesBinding.root
    }

    override fun initViewBinding() {
        setTopicsAdapter()
        setMedicalHistoryTopicsAdapter()
        setMedicalHistoryAdapter()

    }

    override fun observeViewModel() {
        observe(resourcesViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(resourcesViewModel.showSnackBar)
        observeToast(resourcesViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { resourcesViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentResourcesBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentResourcesBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setTopicsAdapter() {
        val topicsAdapter = TopicsAdapter(resourcesViewModel)
        fragmentResourcesBinding.recyclerViewTopics.adapter = topicsAdapter
    }

    private fun setMedicalHistoryAdapter() {
        val medicalHistoryAdapter = MedicalHistoryAdapter(resourcesViewModel)
        fragmentResourcesBinding.recyclerViewMedicalHistory.adapter = medicalHistoryAdapter
    }

    private fun setMedicalHistoryTopicsAdapter() {
        val medicalHistoryTopicsAdapter = MedicalHistoryTopicsAdapter(resourcesViewModel)
        fragmentResourcesBinding.recyclerViewMedicalHistoryTopics.adapter =
            medicalHistoryTopicsAdapter

    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_resources
    }


}

