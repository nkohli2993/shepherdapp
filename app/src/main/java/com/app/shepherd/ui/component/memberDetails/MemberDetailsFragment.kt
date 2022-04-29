package com.app.shepherd.ui.component.memberDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.FragmentAddMemberBinding
import com.app.shepherd.databinding.FragmentMemberDetailsBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.memberDetails.adapter.MemberModulesAdapter
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class MemberDetailsFragment : BaseFragment<FragmentAddMemberBinding>(),
    View.OnClickListener {

    private val memberDetailsViewModel: MemberDetailsViewModel by viewModels()

    private lateinit var fragmentMemberDetailsBinding: FragmentMemberDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMemberDetailsBinding =
            FragmentMemberDetailsBinding.inflate(inflater, container, false)

        return fragmentMemberDetailsBinding.root
    }

    override fun initViewBinding() {
        fragmentMemberDetailsBinding.listener = this

        setRestrictionModuleAdapter()


    }

    override fun observeViewModel() {
        observe(memberDetailsViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(memberDetailsViewModel.showSnackBar)
        observeToast(memberDetailsViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { memberDetailsViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentMemberDetailsBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentMemberDetailsBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }



    private fun setRestrictionModuleAdapter() {
        val memberModulesAdapter = MemberModulesAdapter(memberDetailsViewModel)
        fragmentMemberDetailsBinding.recyclerViewModules.adapter = memberModulesAdapter

        fragmentMemberDetailsBinding.recyclerViewModules.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonSave -> {
                backPress()
            }
        }
    }




    override fun getLayoutRes(): Int {
        return R.layout.fragment_member_details
    }




}

