package com.shepherdapp.app.ui.component.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shepherdapp.app.R
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.dto.notification.Data
import com.shepherdapp.app.data.dto.notification.read_notifications.ReadNotificationRequestModel
import com.shepherdapp.app.databinding.FragmentNotificationsBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.notifications.adapter.NotificationsAdapter
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.view_model.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */

const val TAG = "NotificationsFragment"

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>(), View.OnClickListener {

    private val notificationsViewModel: NotificationsViewModel by viewModels()

    private lateinit var fragmentNotificationsBinding: FragmentNotificationsBinding
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var notificationsAdapter: NotificationsAdapter? = null
    private var currentPage: Int = 0
    private var totalPage: Int = 0
    private var total: Int = 0
    private var isPagination = false


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
        fragmentNotificationsBinding.listener = this
        // Get User's Notifications
        notificationsViewModel.getNotifications(pageNumber, limit)
//        fragmentNotificationsBinding.ivBack.setOnClickListener {
//            backPress()
//        }
        setNotificationsAdapter()
    }

    override fun observeViewModel() {
//        observe(notificationsViewModel.loginLiveData, ::handleLoginResult)
//        observeSnackBarMessages(notificationsViewModel.showSnackBar)
        observeToast(notificationsViewModel.showToast)

        observeEvent(notificationsViewModel.selectedNotificationLiveData, ::selectedNotification)


        // Observe notification live data
        notificationsViewModel.notificationResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    fragmentNotificationsBinding.txtNoResultFound.visibility = View.VISIBLE
                    fragmentNotificationsBinding.recyclerViewNotifications.visibility = View.GONE
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val data = it.data.payload?.data
                    Log.d(TAG, "payload :$data")
                    data?.let { it1 -> notificationsAdapter?.addData(it1, isPagination) }

                    total = it.data.payload?.total!!
                    currentPage = it.data.payload?.currentPage!!
                    totalPage = it.data.payload?.totalPages!!
                    pageNumber = currentPage + 1
                }
            }
        }

        // Observe read notification live data
        notificationsViewModel.readNotificationResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    fragmentNotificationsBinding.txtNoResultFound.visibility = View.VISIBLE
                    fragmentNotificationsBinding.recyclerViewNotifications.visibility = View.GONE
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val payload = it.data.payload
                    Log.d(TAG, "observeViewModel: payload $payload")
                    pageNumber = 1
                    isPagination = false
                    notificationsViewModel.getNotifications(pageNumber, limit)
                }
            }
        }

        // Observe clear notification live data
        notificationsViewModel.clearNotificationResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    fragmentNotificationsBinding.txtNoResultFound.visibility = View.VISIBLE
                    fragmentNotificationsBinding.recyclerViewNotifications.visibility = View.GONE
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val payload = it.data.payload
                    Log.d(TAG, "observeViewModel: payload $payload")
                    pageNumber = 1
                    notificationsViewModel.getNotifications(pageNumber, limit)
                }
            }
        }
    }

    private fun selectedNotification(singleEvent: SingleEvent<Data>) {
        singleEvent.getContentIfNotHandled()?.let {
            // If notification is unread
            if (it.isRead == false) {
                notificationsViewModel.readNotifications(ReadNotificationRequestModel(it.notificationId))
            }

            // Handles Redirection of Notifications
            val notificationType = it.notification?.eventName

            when (notificationType) {
                Const.NotificationAction.CARE_TEAM_INVITE -> {
                    findNavController().navigate(R.id.action_nav_notifications_to_nav_invitation)
                }
                Const.NotificationAction.LOCK_BOX_CREATED -> {
                    findNavController().navigate(R.id.action_nav_notifications_to_nav_lock_box)
                }
                Const.NotificationAction.LOCK_BOX_UPDATED -> {
                    findNavController().navigate(R.id.action_nav_notifications_to_nav_lock_box)
                }
                Const.NotificationAction.MEDICATION_CREATED -> {
                    findNavController().navigate(R.id.action_nav_notifications_to_nav_my_medlist)
                }
                Const.NotificationAction.MEDICATION_UPDATED -> {
                    findNavController().navigate(R.id.action_nav_notifications_to_nav_my_medlist)
                }
                Const.NotificationAction.CARE_POINT_CREATED -> {
                    findNavController().navigate(R.id.action_nav_notifications_to_nav_care_points)

                }
            }
        }
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
        notificationsAdapter = NotificationsAdapter(notificationsViewModel)
        fragmentNotificationsBinding.recyclerViewNotifications.adapter = notificationsAdapter
        handlePagination()
    }

    private fun handlePagination() {
        var isScrolling: Boolean
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisiblesItems: Int
        fragmentNotificationsBinding.recyclerViewNotifications.addOnScrollListener(object :
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
                        isPagination = true
                        notificationsViewModel.getNotifications(pageNumber, limit)
                    }
                }
            }
        })
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_notifications
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.txtClearAll -> {
                notificationsViewModel.clearNotifications()
            }
        }
    }
}

