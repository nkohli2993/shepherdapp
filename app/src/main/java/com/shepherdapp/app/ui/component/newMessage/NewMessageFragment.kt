package com.shepherdapp.app.ui.component.newMessage

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.chat.ChatModel
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.databinding.FragmentNewMessageBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.newMessage.adapter.UsersAdapter
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.view_model.NewMessageViewModel
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
    private var selectedCareTeams: ArrayList<CareTeamModel>? = ArrayList()
    private var chatModelList: ArrayList<ChatModel>? = ArrayList()

    private var loggedInUser: UserProfile? = null
    private var usersAdapter: UsersAdapter? = null
    var currentPage: Int = 0
    var totalPage: Int = 0
    var total: Int = 0
    var groupName: String? = null
    private var TAG = "NewMessageFragment"


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
        chatModelList?.clear()
        fragmentNewMessageBinding.listener = this
        // Get Care Team Members
        newMessageViewModel.getCareTeamsByLovedOneId(pageNumber, limit, status)
        setUsersAdapter()
        fragmentNewMessageBinding.chkDiscussion.setOnCheckedChangeListener { compoundButton, isChecked ->
            usersAdapter?.selectUnselect(isChecked)
            if (isChecked) {
                fragmentNewMessageBinding.btnStartDiscussion.visibility = View.VISIBLE
            } else {
                fragmentNewMessageBinding.btnStartDiscussion.visibility = View.GONE
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

        // Get Login User's detail
        loggedInUser = Prefs.with(ShepherdApp.appContext)!!.getObject(
            Const.USER_DETAILS,
            UserProfile::class.java
        )
    }

    override fun observeViewModel() {
        observe(newMessageViewModel.loginLiveData, ::handleLoginResult)
        observeEvent(newMessageViewModel.openChatMessage, ::navigateToChat)
        observeEvent(newMessageViewModel.selectUser, ::selectUsers)

        observeSnackBarMessages(newMessageViewModel.showSnackBar)
        observeToast(newMessageViewModel.showToast)

        newMessageViewModel.careTeamsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    careTeams?.clear()
                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("CareTeams")
                        setMessage("No CareTeam Found")
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

    private fun selectUsers(singleEvent: SingleEvent<CareTeamModel>) {
        singleEvent.getContentIfNotHandled()?.let {
            val careTeam = it
            selectedCareTeams?.add(careTeam)
            /*   val loggedInUserName = loggedInUser?.firstname + " " + loggedInUser?.lastname
               val loggedInUserId = loggedInUser?.id
               for (i in selectedCareTeams?.indices!!) {
                   val selectedCareTeam = selectedCareTeams!![i]
                   val receiverName =
                       selectedCareTeam.user_id_details.firstname + " " + selectedCareTeam.user_id_details.lastname
                   val receiverID = selectedCareTeam.user_id_details.id
                   val receiverPicUrl = selectedCareTeam.user_id_details.profilePhoto
                   // Create Chat Model
                   val chatModel = ChatModel(
                       null,
                       loggedInUserId,
                       loggedInUserName,
                       receiverID,
                       receiverName,
                       receiverPicUrl,
                       null,
                       Chat.CHAT_GROUP
                   )
                   chatModelList?.add(chatModel)
               }*/
        }


    }

    private fun navigateToChat(singleEvent: SingleEvent<CareTeamModel>) {
        // Get Login User's detail
        /* val loggedInUser = Prefs.with(ShepherdApp.appContext)!!.getObject(
             Const.USER_DETAILS,
             UserProfile::class.java
         )*/
        chatModelList?.clear()
        val loggedInUserName = loggedInUser?.firstname + " " + loggedInUser?.lastname
        val loggedInUserId = loggedInUser?.id

        singleEvent.getContentIfNotHandled()?.let {
            val receiverName = it.user_id_details.firstname + " " + it.user_id_details.lastname
            val receiverID = it.user_id_details.id
            val receiverPicUrl = it.user_id_details.profilePhoto
            // Create Chat Model
            val chatModel = ChatModel(
                null,
                loggedInUserId,
                loggedInUserName,
                receiverID,
                receiverName,
                receiverPicUrl,
                null,
                Chat.CHAT_SINGLE
            )
            chatModelList?.add(chatModel)
            Log.d(TAG, "ChatModel : $chatModel ")
            findNavController().navigate(
                NewMessageFragmentDirections.actionNewMessageToChat(
                    "NewMessageFragment",
                    chatModelList?.toTypedArray()
                )
            )
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
            R.id.btnStartDiscussion -> {
                // Create a dialog to enter the Group name
//                showEnterGroupNameDialog()

                Log.d(TAG, "Selected Users :${selectedCareTeams?.size} ")
                Log.d(TAG, "Users :${selectedCareTeams} ")
                //                findNavController().navigate(R.id.action_new_message_to_chat)

                val loggedInUserName = loggedInUser?.firstname + " " + loggedInUser?.lastname
                val loggedInUserId = loggedInUser?.id
                for (i in selectedCareTeams?.indices!!) {
                    val selectedCareTeam = selectedCareTeams!![i]
                    val receiverName =
                        selectedCareTeam.user_id_details.firstname + " " + selectedCareTeam.user_id_details.lastname
                    val receiverID = selectedCareTeam.user_id_details.id
                    val receiverPicUrl = selectedCareTeam.user_id_details.profilePhoto
                    // Create Chat Model
                    val chatModel = ChatModel(
                        null,
                        loggedInUserId,
                        loggedInUserName,
                        receiverID,
                        receiverName,
                        receiverPicUrl,
                        null,
                        Chat.CHAT_GROUP
                    )
                    chatModelList?.add(chatModel)
                }
                Log.d(TAG, "GroupChatData :$chatModelList ")
                findNavController().navigate(
                    NewMessageFragmentDirections.actionNewMessageToChat(
                        "NewMessageFragment",
                        chatModelList?.toTypedArray()
                    )
                )
            }
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun showEnterGroupNameDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_enter_group_name)
        val edtGroupName = dialog.findViewById(R.id.edtGroupName) as EditText
        val btnOkay = dialog.findViewById(R.id.btnOkay) as TextView
        val btnCancel = dialog.findViewById(R.id.btnCancel) as TextView
        btnOkay.setOnClickListener {
            groupName = edtGroupName.text.toString().trim()
            if (groupName.isNullOrEmpty()) {
                showInfo(requireContext(), "Please enter Group Name")
            } else {
                Log.d(TAG, "Selected Users :${selectedCareTeams?.size} ")
                Log.d(TAG, "Users :${selectedCareTeams} ")
//                findNavController().navigate(R.id.action_new_message_to_chat)

                val loggedInUserName = loggedInUser?.firstname + " " + loggedInUser?.lastname
                val loggedInUserId = loggedInUser?.id
                for (i in selectedCareTeams?.indices!!) {
                    val selectedCareTeam = selectedCareTeams!![i]
                    val receiverName =
                        selectedCareTeam.user_id_details.firstname + " " + selectedCareTeam.user_id_details.lastname
                    val receiverID = selectedCareTeam.user_id_details.id
                    val receiverPicUrl = selectedCareTeam.user_id_details.profilePhoto
                    // Create Chat Model
                    val chatModel = ChatModel(
                        null,
                        loggedInUserId,
                        loggedInUserName,
                        receiverID,
                        receiverName,
                        receiverPicUrl,
                        null,
                        Chat.CHAT_GROUP,
                        groupName
                    )
                    chatModelList?.add(chatModel)
                }
                Log.d(TAG, "GroupChatData :$chatModelList ")
                findNavController().navigate(
                    NewMessageFragmentDirections.actionNewMessageToChat(
                        "NewMessageFragment",
                        chatModelList?.toTypedArray()
                    )
                )
                dialog.dismiss()
            }
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
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

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onStop() {
        super.onStop()
        // To fix the issue : When moving back from Chat Screen to User Listing Screen , toggle is always on
        fragmentNewMessageBinding.chkDiscussion.isChecked = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        selectedCareTeams?.clear()
    }
}

