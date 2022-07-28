package com.shepherd.app.ui.component.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.shepherd.app.R
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.FragmentNotificationsBinding
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.notifications.adapter.NotificationsAdapter
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.observe
import com.shepherd.app.utils.setupSnackbar
import com.shepherd.app.utils.showToast
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

