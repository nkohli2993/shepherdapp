package com.app.shepherd.ui.component.eventDetails

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.FragmentAddMemberBinding
import com.app.shepherd.databinding.FragmentEventDetailsBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.eventDetails.adapter.EventChatAdapter
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class EventDetailsFragment : BaseFragment<FragmentAddMemberBinding>(),
    View.OnClickListener {

    private val eventDetailsViewModel: EventDetailsViewModel by viewModels()

    private lateinit var fragmentEventDetailsBinding: FragmentEventDetailsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentEventDetailsBinding =
            FragmentEventDetailsBinding.inflate(inflater, container, false)

        return fragmentEventDetailsBinding.root
    }

    override fun initViewBinding() {
        fragmentEventDetailsBinding.listener = this

        setEventChatAdapter()


    }

    override fun observeViewModel() {
        observe(eventDetailsViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(eventDetailsViewModel.showSnackBar)
        observeToast(eventDetailsViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { eventDetailsViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentEventDetailsBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentEventDetailsBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setEventChatAdapter() {
        val eventChatAdapter = EventChatAdapter(eventDetailsViewModel)
        fragmentEventDetailsBinding.recyclerViewEventChat.adapter = eventChatAdapter
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonReply -> {
                //backPress()
                startActivity(Intent(requireContext(), HomeActivity::class.java))
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_event_details
    }


}

