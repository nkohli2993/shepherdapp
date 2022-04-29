package com.app.shepherd.ui.component.addNewEvent

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
import com.app.shepherd.databinding.FragmentAddNewEventBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.addMember.adapter.AddMemberRoleAdapter
import com.app.shepherd.ui.component.addMember.adapter.RestrictionsModuleAdapter
import com.app.shepherd.ui.component.addNewEvent.adapter.AssignToEventAdapter
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddNewEventFragment : BaseFragment<FragmentAddMemberBinding>(),
    View.OnClickListener {

    private val addNewEventViewModel: AddNewEventViewModel by viewModels()

    private lateinit var fragmentAddNewEventBinding: FragmentAddNewEventBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddNewEventBinding =
            FragmentAddNewEventBinding.inflate(inflater, container, false)

        return fragmentAddNewEventBinding.root
    }

    override fun initViewBinding() {
        fragmentAddNewEventBinding.listener = this

        setAssignMembersAdapter()


    }

    override fun observeViewModel() {
        observe(addNewEventViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(addNewEventViewModel.showSnackBar)
        observeToast(addNewEventViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { addNewEventViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentAddNewEventBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentAddNewEventBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setAssignMembersAdapter() {
        val addMemberRoleAdapter = AssignToEventAdapter(addNewEventViewModel)
        fragmentAddNewEventBinding.recyclerViewMembers.adapter = addMemberRoleAdapter
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.textViewSelectDate -> {
                
            }
            R.id.buttonAdd -> {
                backPress()
            }
        }
    }




    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_event
    }




}

