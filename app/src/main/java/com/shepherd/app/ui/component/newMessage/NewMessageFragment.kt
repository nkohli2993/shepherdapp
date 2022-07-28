package com.shepherd.app.ui.component.newMessage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.FragmentNewMessageBinding
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.newMessage.adapter.UsersAdapter
import com.shepherd.app.utils.*
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


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
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
                findNavController().navigate(R.id.action_new_message_to_chat)
            }
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_new_message
    }


}

