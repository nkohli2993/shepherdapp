package com.app.shepherd.ui.component.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.FragmentNotificationsBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.notifications.adapter.NotificationsAdapter
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.observe
import com.app.shepherd.utils.setupSnackbar
import com.app.shepherd.utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {

    private val notificationsViewModel: NotificationsViewModel by viewModels()

    private lateinit var fragmentNotificationsBinding: FragmentNotificationsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentNotificationsBinding =
            FragmentNotificationsBinding.inflate(inflater, container, false)

        return fragmentNotificationsBinding.root
    }

    override fun initViewBinding() {
        setNotificationsAdapter()
    }

    override fun observeViewModel() {
        observe(notificationsViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(notificationsViewModel.showSnackBar)
        observeToast(notificationsViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { notificationsViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentNotificationsBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentNotificationsBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setNotificationsAdapter() {
        val notificationsAdapter = NotificationsAdapter(notificationsViewModel)
        fragmentNotificationsBinding.recyclerViewNotifications.adapter = notificationsAdapter

    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_medlist
    }


}

