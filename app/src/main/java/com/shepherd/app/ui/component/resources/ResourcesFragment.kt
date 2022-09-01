package com.shepherd.app.ui.component.resources

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar
import com.shepherd.app.R
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.FragmentResourcesBinding
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.resources.adapter.MedicalHistoryTopicsAdapter
import com.shepherd.app.ui.component.resources.adapter.TopicsAdapter
import com.shepherd.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.resource.AllResourceData
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.view_model.ResourceViewModel
import kotlin.collections.ArrayList

/**
 * Created by Nikita 31/08/2022
 */
@AndroidEntryPoint
class ResourcesFragment : BaseFragment<FragmentResourcesBinding>() {

    private lateinit var fragmentResourcesBinding: FragmentResourcesBinding
    private val resourcesViewModel: ResourceViewModel by viewModels()
    private val TAG: String = "ResourceList"
    private var pageNumber = 1
    private val limit = 10
    private var currentPage: Int = 0
    private var totalPage: Int = 0
    private var total: Int = 0
    private var topicsAdapter: TopicsAdapter? = null
    private var medicalHistoryTopicsAdapter: MedicalHistoryTopicsAdapter? = null
    private var resourceList: ArrayList<AllResourceData> = arrayListOf()
    private var trendingResourceList: ArrayList<AllResourceData> = arrayListOf()
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
        resourcesViewModel.getAllResourceApi(
            pageNumber,
            limit,
            resourcesViewModel.getLovedOneUUId()!!
        )
        resourcesViewModel.getTrendingResourceApi(pageNumber, limit)
        setTopicsAdapter()
        setMedicalHistoryTopicsAdapter()
        setMedicalHistoryAdapter()

    }

    private fun handleResourcesPagination() {
        var isScrolling = true
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisiblesItems: Int
        fragmentResourcesBinding.recyclerViewTopics.addOnScrollListener(object :
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
                        resourcesViewModel.getAllResourceApi(
                            pageNumber,
                            limit,
                            resourcesViewModel.getLovedOneUUId()!!
                        )
                    }
                }
            }
        })
    }

    override fun observeViewModel() {

        observeEvent(resourcesViewModel.selectedResourceDetail, ::navigateToResourceItems)
        resourcesViewModel.resourceResponseLiveData.observeEvent(this) {
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
                    resourceList = it.data.payload!!.data
                    it.data.payload.let { payload ->
                        resourceList = payload!!.data
                        total = payload.total
                        currentPage = payload.currentPage
                        totalPage = payload.totalPages
                    }

                    if (resourceList.isEmpty()) return@observeEvent

                    if (resourceList.isEmpty()) {
                        fragmentResourcesBinding.recyclerViewMedicalHistoryTopics.visibility =
                            View.GONE

                    } else {
                        fragmentResourcesBinding.recyclerViewMedicalHistoryTopics.visibility =
                            View.VISIBLE
                        resourceList.let { it1 ->
                            medicalHistoryTopicsAdapter?.addData(
                                it1
                            )
                        }
                    }
                }
            }
        }
        resourcesViewModel.trendingResourceResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    //showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    trendingResourceList = it.data.payload!!.data

                    if (trendingResourceList.isEmpty()) return@observeEvent

                    if (trendingResourceList.isEmpty()) {
                        fragmentResourcesBinding.recyclerViewTopics.visibility = View.GONE

                    } else {
                        fragmentResourcesBinding.recyclerViewTopics.visibility =
                            View.VISIBLE
                        trendingResourceList.let { it1 ->
                            topicsAdapter?.addData(
                                it1
                            )
                        }
                    }
                }
            }
        }

    }

    private fun navigateToResourceItems(navigateEvent: SingleEvent<Int>) {
        navigateEvent.getContentIfNotHandled()?.let {
            Log.d(TAG, "openResopurceDetail: id :$it")
            findNavController().navigate(
                ResourcesFragmentDirections.actionNavResourceToResourceToResourceDetail(
                    it.toString()
                )
            )
        }

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

    private fun setTopicsAdapter() {
        topicsAdapter = TopicsAdapter(resourcesViewModel)
        fragmentResourcesBinding.recyclerViewTopics.adapter = topicsAdapter
    }

    private fun createTextView(text: String): View {
        val textView = TextView(requireContext())
        textView.text = text
        textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.gotham_book)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color._192032))
        textView.setPadding(20, 10, 10, 10)
        textView.setOnClickListener {
            //candle click
        }
        textView.compoundDrawablePadding = 10
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_round_cancel,
            0
        )
        textView.setBackgroundResource(R.drawable.shape_black_border)
        return textView
    }

    private fun setMedicalHistoryAdapter() {
        //set medical history names
/*
        for (locale in Locale.getAvailableLocales()) {
            val countryName: String = locale.displayCountry
            if (countryName.isNotEmpty()) {
                fragmentResourcesBinding.medicalHistory.addView(
                    createTextView(countryName),
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                )
            }
        }
*/
    }

    private fun setMedicalHistoryTopicsAdapter() {
        medicalHistoryTopicsAdapter = MedicalHistoryTopicsAdapter(resourcesViewModel)
        fragmentResourcesBinding.recyclerViewMedicalHistoryTopics.adapter =
            medicalHistoryTopicsAdapter
        handleResourcesPagination()
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_resources
    }


}

