package com.app.shepherd.ui.component.newMessage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.FragmentNewMessageBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.myMedList.adapter.MyMedicationsAdapter
import com.app.shepherd.ui.component.myMedList.adapter.MyRemindersAdapter
import com.app.shepherd.ui.component.myMedList.adapter.SelectedDayMedicineAdapter
import com.app.shepherd.ui.component.newMessage.adapter.UsersAdapter
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class NewMessageFragment : BaseFragment<FragmentNewMessageBinding>(),
    View.OnClickListener {

    private val newMessageViewModel: NewMessageViewModel by viewModels()

    private lateinit var fragmentNewMessageBinding: FragmentNewMessageBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentNewMessageBinding =
            FragmentNewMessageBinding.inflate(inflater, container, false)

        return fragmentNewMessageBinding.root
    }

    override fun initViewBinding() {
        fragmentNewMessageBinding.listener = this

        setUsersAdapter()
    }

    override fun observeViewModel() {
        observe(newMessageViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(newMessageViewModel.showSnackBar)
        observeToast(newMessageViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { newMessageViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentNewMessageBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentNewMessageBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setUsersAdapter() {
        val usersAdapter = UsersAdapter(newMessageViewModel)
        fragmentNewMessageBinding.recyclerViewUsers.adapter = usersAdapter

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonSubmit -> {
                p0.findNavController().navigate(R.id.action_new_message_to_chat)
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_new_message
    }


}

