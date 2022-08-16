package com.shepherd.app.ui.component.newMessage

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shepherd.app.R
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.FragmentNewMessageBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.newMessage.adapter.UsersAdapter
import com.shepherd.app.utils.*
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
    var currentPage: Int = 0
    var totalPage: Int = 0
    var total: Int = 0


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

        fragmentNewMessageBinding.chkDiscussion.setOnCheckedChangeListener { compoundButton, isChecked ->
            usersAdapter?.selectUnselect(isChecked)
            if (isChecked) {
                fragmentNewMessageBinding.buttonSubmit.visibility = View.VISIBLE
            } else {
                fragmentNewMessageBinding.buttonSubmit.visibility = View.GONE

            }
        }

        // Search
        fragmentNewMessageBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!s.isNullOrEmpty()) {
                    newMessageViewModel.searchCareTeamsByLovedOneId(
                        pageNumber,
                        limit,
                        status,
                        s.toString()
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

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
                    total = it.data.payload.total!!
                    currentPage = it.data.payload.currentPage!!
                    totalPage = it.data.payload.totalPages!!
                    if (careTeams.isNullOrEmpty()) return@observeEvent
                    // Get the UUID of loggedIn User
                    val loggedInUserUUID =
                        Prefs.with(ShepherdApp.appContext)!!.getString(Const.UUID, "")
                    // User list should not contain loggedIn User
                    val careTeamList = careTeams?.filterNot { careTeamModel ->
                        careTeamModel.user_id_details.uid == loggedInUserUUID
                    } as ArrayList
                    usersAdapter?.addData(careTeamList)
                }
            }
        }

        newMessageViewModel.searchCareTeamsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    careTeams?.clear()
                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("Users")
                        setMessage("No Users Found")
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
                    total = it.data.payload.total!!
                    currentPage = it.data.payload.currentPage!!
                    totalPage = it.data.payload.totalPages!!
                    if (careTeams.isNullOrEmpty()) return@observeEvent
                    val loggedInUserUUID =
                        Prefs.with(ShepherdApp.appContext)!!.getString(Const.UUID, "")
                    // User list should not contain loggedIn User
                    val careTeamList = careTeams?.filterNot { careTeamModel ->
                        careTeamModel.user_id_details.uid == loggedInUserUUID
                    } as ArrayList
                    usersAdapter?.addData(careTeamList)
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
        handlePagination()
    }

    private fun handlePagination() {
        var isScrolling = true
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisibleItems: Int
        fragmentNewMessageBinding.recyclerViewUsers.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    isScrolling = true
                    visibleItemCount = recyclerView.layoutManager!!.childCount
                    totalItemCount = recyclerView.layoutManager!!.itemCount
                    pastVisibleItems =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (isScrolling && visibleItemCount + pastVisibleItems >= totalItemCount && (currentPage < totalPage)) {
                        isScrolling = false
                        currentPage++
                        pageNumber++
                        newMessageViewModel.getCareTeamsByLovedOneId(pageNumber, limit, status)
                    }
                }
            }
        })
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonSubmit -> {
//                findNavController().navigate(R.id.action_new_message_to_chat)
            }
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_new_message
    }

    override fun onResume() {
        super.onResume()
        resetPageNumber()
        newMessageViewModel.getCareTeamsByLovedOneId(pageNumber, limit, status)
    }

    private fun resetPageNumber() {
        pageNumber = 1
    }

}

