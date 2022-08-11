package com.shepherd.app.ui.component.newMessage

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.shepherd.app.R
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.FragmentNewMessageBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.newMessage.adapter.UsersAdapter
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.observe
import com.shepherd.app.utils.setupSnackbar
import com.shepherd.app.utils.showToast
import com.shepherd.app.view_model.NewMessageViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class NewMessageFragment : BaseFragment<FragmentNewMessageBinding>(),
    View.OnClickListener {

    private val newMessageViewModel: NewMessageViewModel by viewModels()

    private lateinit var fragmentNewMessageBinding: FragmentNewMessageBinding
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 1
    private var careTeams: ArrayList<CareTeamModel>? = ArrayList()
    private var usersAdapter: UsersAdapter? = null


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
        // Get Care Team Members
        newMessageViewModel.getCareTeamsByLovedOneId(pageNumber, limit, status)
        setUsersAdapter()
    }

    override fun observeViewModel() {
        observe(newMessageViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(newMessageViewModel.showSnackBar)
        observeToast(newMessageViewModel.showToast)

        newMessageViewModel.careTeamsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    careTeams?.clear()
                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("Care Teams")
                        setMessage("No Care Team Found")
                        setPositiveButton("OK") { _, _ ->
                            // navigateToDashboardScreen()
                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    careTeams = it.data.payload.data
                    if (careTeams.isNullOrEmpty()) return@observeEvent
                    usersAdapter?.addData(careTeams!!)
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
        usersAdapter = UsersAdapter(newMessageViewModel)
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

