package com.app.shepherd.ui.component.carePoints

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.FragmentAddMemberBinding
import com.app.shepherd.databinding.FragmentCarePointsBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.addLovedOne.AddLovedOneActivity
import com.app.shepherd.ui.component.carePoints.adapter.CarePointsDayAdapter
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_new_event.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class CarePointsFragment : BaseFragment<FragmentAddMemberBinding>(),
    View.OnClickListener {

    private val carePointsViewModel: CarePointsViewModel by viewModels()

    private lateinit var fragmentCarePointsBinding: FragmentCarePointsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCarePointsBinding =
            FragmentCarePointsBinding.inflate(inflater, container, false)

        return fragmentCarePointsBinding.root
    }

    override fun initViewBinding() {
        fragmentCarePointsBinding.listener = this


        setCarePointsAdapter()
    }

    override fun observeViewModel() {
        observe(carePointsViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(carePointsViewModel.showSnackBar)
        observeToast(carePointsViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { carePointsViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentCarePointsBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentCarePointsBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setCarePointsAdapter() {
        val carePointsAdapter = CarePointsDayAdapter(carePointsViewModel)
        fragmentCarePointsBinding.recyclerViewEventDays.adapter = carePointsAdapter
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonAdd -> {
                backPress()
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_care_points
    }


}

