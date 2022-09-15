package com.shepherdapp.app.ui.component.carePoints

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.added_events.AddedEventModel
import com.shepherdapp.app.data.dto.added_events.EventCommentUserDetailModel
import com.shepherdapp.app.data.dto.added_events.UserAssigneDetail
import com.shepherdapp.app.data.dto.added_events.UserAssigneeModel
import com.shepherdapp.app.data.dto.chat.ChatModel
import com.shepherdapp.app.data.dto.chat.ChatUserDetail
import com.shepherdapp.app.data.dto.chat.MessageGroupData
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.databinding.FragmentCarePointDetailBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.carePoints.adapter.CarePointEventCommentAdapter
import com.shepherdapp.app.ui.component.carePoints.adapter.CarePointsEventAdapter
import com.shepherdapp.app.utils.Chat
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.extensions.hideKeyboard
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.view_model.CreatedCarePointsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Nikita Kohli on 22-07-22
 */
@AndroidEntryPoint
@SuppressLint("SimpleDateFormat", "ClickableViewAccessibility")
class CarePointDetailFragment : BaseFragment<FragmentCarePointDetailBinding>(),
    View.OnClickListener {
    private lateinit var fragmentCarePointDetailBinding: FragmentCarePointDetailBinding
    private var commentAdapter: CarePointEventCommentAdapter? = null
    private val args: CarePointDetailFragmentArgs by navArgs()
    private var commentList: ArrayList<EventCommentUserDetailModel> = ArrayList()
    private val carePointsViewModel: CreatedCarePointsViewModel by viewModels()
    private var eventDetail: AddedEventModel? = null
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var chatModelList: ArrayList<ChatModel>? = ArrayList()
    private var chatUserDetailList: ArrayList<ChatUserDetail>? = ArrayList()
    private var allMsgLoaded: Boolean = false
    private var msgGroupList: ArrayList<MessageGroupData> = ArrayList()
    private var chatModel: ChatModel? = null
    private var isAssignerDetailRequired: Boolean = false
    private var isChatOff: Boolean = false
    private var eventName: String? = null
    private var eventId: Int? = null


    private var TAG = "CarePointDetailFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCarePointDetailBinding =
            FragmentCarePointDetailBinding.inflate(inflater, container, false)
        return fragmentCarePointDetailBinding.root
    }


    override fun initViewBinding() {
        fragmentCarePointDetailBinding.listener = this
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // Get event Detail from Care Point Fragment
        eventDetail = args.eventDetail

        // Get Login User's detail
        val loggedInUser = Prefs.with(ShepherdApp.appContext)!!.getObject(
            Const.USER_DETAILS,
            UserProfile::class.java
        )

        val loggedInUserId = loggedInUser?.userId
        val loggedInUserName = loggedInUser?.firstname + " " + loggedInUser?.lastname

        Log.d(TAG, "onEventSelected: $eventDetail ")

        when (eventDetail?.user_assignes?.size) {
            1 -> {
                if (isListContainMethod(eventDetail?.user_assignes!!)) {
                    if (loggedInUserId == eventDetail?.createdByDetails?.id) {
                        chatOff()
                        isChatOff = true
                    } else {
                        chatOn()
                        isAssignerDetailRequired = true
                    }
//                    chatOn()
//                    isAssignerDetailRequired = true
                } else if (loggedInUserId == eventDetail?.createdByDetails?.id) {
                    // Check if the loggedIn user is the assigner
                    // It means two user are there for the care point(event) ,one is assignee and other is the assigner,
                    // make the visibility of editTextMessage and sendCommentIV Visible
                    chatOn()
                } else {
                    chatOff()
                    isChatOff = true
                }
            }
            else -> {
                // Check the possibility of chat
                // editTextMessage and sendCommentIV is visible if the loggedIn user is one of the assignee of the event or loggedIn user is the assigner
                if (eventDetail?.user_assignes?.let { isListContainMethod(it) } == true || (loggedInUserId == eventDetail?.createdByDetails?.id)) {
                    chatOn()
                } else {
                    chatOff()
                    isChatOff = true
                }
            }
        }


        eventName = eventDetail?.name
        eventId = eventDetail?.id
        eventDetail?.user_assignes?.forEach {
            val receiverName = it.user_details.firstname + " " + it.user_details.lastname
            val receiverID = it.user_details.id
            val receiverPicUrl = it.user_details.profilePhoto
            val documentID = null
            val chatType = Chat.CHAT_GROUP

            // Create Chat Model
            val chatModel = ChatModel(
                documentID,
                loggedInUserId,
                loggedInUserName,
                receiverID,
                receiverName,
                receiverPicUrl,
                null,
                chatType,
                eventName,
                eventId
            )
            chatModelList?.add(chatModel)
        }
        if (isAssignerDetailRequired) {
            val receiverName =
                eventDetail?.createdByDetails?.firstname + " " + eventDetail?.createdByDetails?.lastname
            val receiverID = eventDetail?.createdByDetails?.id
            val receiverPicUrl = eventDetail?.createdByDetails?.profilePhoto
            val documentID = null
            val chatType = Chat.CHAT_GROUP

            // Create Chat Model
            val chatModel = ChatModel(
                documentID,
                loggedInUserId,
                loggedInUserName,
                receiverID,
                receiverName,
                receiverPicUrl,
                null,
                chatType,
                eventName,
                eventId
            )
            chatModelList?.add(chatModel)
        }

        chatModelList?.forEach {
            val chatUserDetail = it.toChatUserDetail()
            chatUserDetailList?.add(chatUserDetail)
        }
        if (!isChatOff) {
            // Set User Detail
            carePointsViewModel.setToUserDetail(
                Chat.CHAT_GROUP,
                chatUserDetailList,
                eventName,
                eventId
            )
            // Load Chat
            loadChat()
            //  initScrollListener()
        }

        setCommentAdapter()
        if (eventDetail != null) {
            initCarePointDetailViews(eventDetail!!)
        }

//        carePointsViewModel.getCarePointsEventCommentsId(pageNumber, limit, eventDetail?.id ?: 0)
        fragmentCarePointDetailBinding.tvNotes.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    fun chatOn() {
        fragmentCarePointDetailBinding.editTextMessage.visibility = View.VISIBLE
        fragmentCarePointDetailBinding.sendCommentIV.visibility = View.VISIBLE
    }

    fun chatOff() {
        fragmentCarePointDetailBinding.editTextMessage.visibility = View.GONE
        fragmentCarePointDetailBinding.sendCommentIV.visibility = View.GONE
    }


    private fun initScrollListener() {

        fragmentCarePointDetailBinding.recyclerViewChat.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(-1) && !allMsgLoaded) {
                    loadPreviousChat()
                }
            }
        })

    }

    private fun loadPreviousChat() {
        carePointsViewModel.getPreviousMessages()
    }

    private fun loadChat() {
        carePointsViewModel.getChatMessages()
            .observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {

                    when (it) {
                        is DataResult.Loading -> {
                            showLoading("")
                        }
                        is DataResult.Failure -> {
                            hideLoading()
                            allMsgLoaded = true
                            showError(requireContext(), it.exception?.message ?: "")
                        }
                        is DataResult.Success -> {
                            hideLoading()
                            msgGroupList.clear()
                            msgGroupList.addAll(it.data.groupList)
                            Log.d(TAG, "loadChat: $msgGroupList")
                            /* chatAdapter?.addData(
                                 msgGroupList,
                                 chatViewModel.loggedInUser.id.toString()
                             )*/
                            setAdapter(it.data.scrollToBottom ?: false)
                        }
                    }
                }
            }
    }

    private fun setAdapter(scrollToBottom: Boolean) {
        commentAdapter?.addData(msgGroupList)

        if (scrollToBottom) {
            Handler().postDelayed({
                (fragmentCarePointDetailBinding.recyclerViewChat.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                    0,
                    0
                )

            }, 200)
        }

        /* fragmentCarePointDetailBinding.recyclerViewChat.postDelayed({
             fragmentCarePointDetailBinding.recyclerViewChat.adapter?.itemCount?.minus(1)?.let {
                 fragmentCarePointDetailBinding.recyclerViewChat.scrollToPosition(
                     it
                 )
             }
         }, 1000)*/
    }

    private fun ChatModel.toChatUserDetail(): ChatUserDetail {
        return ChatUserDetail(
            id = this.receiverID.toString() ?: "",
            name = this.receiverName ?: "",
            imageUrl = this.receiverPicUrl ?: ""
        )
    }

    private fun setCommentAdapter() {
        //set comment adapter added in list
        commentAdapter = CarePointEventCommentAdapter(carePointsViewModel)
        fragmentCarePointDetailBinding.recyclerViewChat.adapter = commentAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observeViewModel() {
        carePointsViewModel.carePointsResponseDetailLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    initCarePointDetailViews(it.data.payload)

                }
                is DataResult.Failure -> {
                    hideLoading()
                }
            }
        }
        carePointsViewModel.addedCarePointCommentLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    commentList.add(
                        commentList.size,
                        EventCommentUserDetailModel(
                            event_id = eventDetail?.id ?: 0,
                            comment = fragmentCarePointDetailBinding.editTextMessage.text.toString(),
                            created_at = it.data.payload.created_at,
                            user_details = UserAssigneDetail(
                                id = carePointsViewModel.getUserDetail()?.userId,
                                firstname = carePointsViewModel.getUserDetail()?.firstname,
                                lastname = carePointsViewModel.getUserDetail()?.lastname,
                                profilePhoto = carePointsViewModel.getUserDetail()?.profilePhoto
                            )
                        )
                    )
                    commentAdapter!!.notifyItemInserted(commentList.size)
                    commentAdapter!!.notifyDataSetChanged()
                    fragmentCarePointDetailBinding.editTextMessage.setText("")
                    showToast("Comment added successfully")
                    fragmentCarePointDetailBinding.recyclerViewChat.smoothScrollToPosition(
                        commentList.size
                    )
                    //add to comment list


                }
                is DataResult.Failure -> {
                    hideLoading()
                    showToast("Unable to add comment, please try again later!")
                }
            }
        }
        /* carePointsViewModel.addedCarePointDetailCommentsLiveData.observeEvent(this) {
             when (it) {
                 is DataResult.Loading -> {
                     showLoading("")
                 }
                 is DataResult.Success -> {
                     hideLoading()
                     // show comments on
                     commentList = it.data.payload.data
                     if (commentList.isEmpty()) return@observeEvent
                     commentAdapter?.updateAddedComment(commentList)
                     fragmentCarePointDetailBinding.recyclerViewChat.smoothScrollToPosition(
                         commentList.size
                     )
                 }
                 is DataResult.Failure -> {
                     hideLoading()
                 }
             }
         }*/

        /*carePointsViewModel.isMessageSendLiveData.observeEvent(this) {
            if (it) {
                carePointsViewModel.findChatId()
            }
        }*/

        // To fix : If two users (A and B) entered the chat screen simultaneously for the first time.
        // If user A sends message, then the chat screen of A is updated but of B is not updated .
        /* carePointsViewModel.noChatDataFoundLiveData.observeEvent(this) {
             if (it) {
                 Handler(Looper.getMainLooper()).postDelayed({
                     *//* // Set User Detail
                     carePointsViewModel.setToUserDetail(
                         Chat.CHAT_GROUP,
                         chatUserDetailList,
                         eventName,
                         eventId
                     )*//*
                    carePointsViewModel.findChatId()
                }, 10000)
            }
        }*/

    }

    @SuppressLint("SimpleDateFormat")
    private fun initCarePointDetailViews(payload: AddedEventModel) {
        fragmentCarePointDetailBinding.let {
            it.llImageWrapper.visibility = View.VISIBLE
            it.tvTitleCarePoint.text = payload.name
            it.tvLocation.text = payload.location
            val carePointDate = SimpleDateFormat("yyyy-MM-dd").parse(payload.date!!)!!
            it.tvDate.text = SimpleDateFormat("EEE, MMM dd, yyyy").format(carePointDate)
            if (payload.time != null) {
                val carePointTime = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                    payload.date.plus(" ").plus(payload.time?.replace(" ", ""))
                )
                it.tvTime.text = SimpleDateFormat("hh:mm a").format(carePointTime!!)
            }


            //set comment added count adapter
            val carePointsEventAdapter = CarePointsEventAdapter(payload.user_assignes)
            fragmentCarePointDetailBinding.recyclerViewEventAssigne.adapter = carePointsEventAdapter

            //set user detail
            if (!payload.createdByDetails?.profilePhoto.isNullOrEmpty()) {
                Picasso.get().load(payload.createdByDetails?.profilePhoto)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(fragmentCarePointDetailBinding.imageViewUser)
            }

            // Set Name
            var name: String? = null
            name = if (!payload.createdByDetails?.firstname.isNullOrEmpty()) {
                if (!payload.createdByDetails?.lastname.isNullOrEmpty()) {
                    payload.createdByDetails?.firstname + " " + payload.createdByDetails?.lastname
                } else {
                    payload.createdByDetails?.firstname
                }
            } else {
                "Name Not Available"
            }

            fragmentCarePointDetailBinding.tvUsername.text = name

            /*fragmentCarePointDetailBinding.tvUsername.text =
                payload.createdByDetails?.firstname.plus(" ")
                    .plus(if (payload.createdByDetails?.lastname == null) "" else payload.createdByDetails?.lastname)*/

            // show created on time
            val dateTime = (payload.created_at ?: "").replace(".000Z", "").replace("T", " ")
            val commentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                dateTime
            )
            val df =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            df.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date =
                df.parse(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(commentTime!!))!!
            df.timeZone = TimeZone.getDefault()
            it.tvPostedDate.text = SimpleDateFormat("EEE, MMM dd").format(date)
            if ((payload.notes ?: "").length > 50) {
                setNotesClickForLong(payload.notes!!, true, it.tvNotes)
            } else {
                it.tvNotes.text = payload.notes
            }
            if ((payload.location ?: "").length > 50) {
                setNotesClickForLong(payload.location!!, true, it.tvLocation)
            } else {
                it.tvLocation.text = payload.location
            }
            if (isChatOff) {
                it.editTextMessage.visibility = View.GONE
                it.sendCommentIV.visibility = View.GONE
            } else {
                it.editTextMessage.visibility = View.VISIBLE
                it.sendCommentIV.visibility = View.VISIBLE
            }


            /* when (payload.user_assignes.size) {
                 1 -> {
                     it.editTextMessage.visibility = View.GONE
                     it.sendCommentIV.visibility = View.GONE
                 }
                 else -> {
                     it.editTextMessage.visibility = View.VISIBLE
                     it.sendCommentIV.visibility = View.VISIBLE
                     if (!isListContainMethod(payload.user_assignes)) {
                         it.editTextMessage.visibility = View.GONE
                         it.sendCommentIV.visibility = View.GONE
                     }
                 }
             }*/
        }
    }

    private fun isListContainMethod(arraylist: ArrayList<UserAssigneeModel>): Boolean {
        for (str in arraylist) {
            if (str.user_details.id == carePointsViewModel.getUserDetail()?.userId!!) {
                return true
            }
        }
        return false
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_care_point_detail
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
            }
            R.id.sendCommentIV -> {
                /* when {
                     fragmentCarePointDetailBinding.editTextMessage.isBlank() || fragmentCarePointDetailBinding.editTextMessage.text.toString()
                         .isEmpty() -> {
                         // do nothing for empty comment field
                     }
                     else -> {
                         val addEventComment = EventCommentModel(
                             event_id = eventDetail?.id ?: 0,
                             comment = fragmentCarePointDetailBinding.editTextMessage.text.toString()
                                 .trim()
                         )
                         carePointsViewModel.addEventCommentCarePoint(addEventComment)
                     }
                 }*/

                val message = fragmentCarePointDetailBinding.editTextMessage.text.toString().trim()
                if (message.isNullOrEmpty()) {
                    showInfo(requireContext(), "Please enter message...")
                } else {
                    chatModel?.chatType = Chat.CHAT_GROUP
                    chatModel?.message = message
                    Log.d(TAG, "Send Message :$chatModel ")
//                    chatModel?.let { chatViewModel.sendMessage(it) }
                    carePointsViewModel.getAndSaveMessageData(Chat.MESSAGE_TEXT, message = message)
                    fragmentCarePointDetailBinding.editTextMessage.text?.clear()
                    hideKeyboard()
                }
            }

        }
    }

    private fun setNotesClickForLong(
        notes: String,
        isSpanned: Boolean,
        textView: AppCompatTextView
    ) {
        val showNotes = if (isSpanned) {
            notes.substring(0, 50).plus(" ... View more.")
        } else {
            notes.plus(" ... View less.")
        }
        val ss = SpannableString(showNotes)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(p0: View) {
                //click to show whole note
                setNotesClickForLong(notes, !isSpanned, textView)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
                ds.color = ContextCompat.getColor(requireContext(), R.color._399282)
                ds.linkColor = ContextCompat.getColor(requireContext(), R.color._399282)
            }
        }
        if (showNotes.length <= 65) {
            ss.setSpan(clickableSpan, 50, 65, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            ss.setSpan(
                clickableSpan,
                showNotes.length - 15,
                showNotes.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.GREEN
    }

}