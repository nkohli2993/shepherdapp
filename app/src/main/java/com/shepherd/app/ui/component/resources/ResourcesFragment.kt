package com.shepherd.app.ui.component.resources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.shepherd.app.R
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.FragmentMessagesBinding
import com.shepherd.app.databinding.FragmentResourcesBinding
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.resources.adapter.MedicalHistoryAdapter
import com.shepherd.app.ui.component.resources.adapter.MedicalHistoryTopicsAdapter
import com.shepherd.app.ui.component.resources.adapter.TopicsAdapter
import com.shepherd.app.utils.*
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


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
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

