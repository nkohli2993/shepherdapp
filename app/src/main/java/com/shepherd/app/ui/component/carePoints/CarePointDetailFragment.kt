package com.shepherd.app.ui.component.carePoints

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.data.dto.added_events.*
import com.shepherd.app.databinding.FragmentCarePointDetailBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.carePoints.adapter.CarePointEventCommentAdapter
import com.shepherd.app.ui.component.carePoints.adapter.CarePointsEventAdapter
import com.shepherd.app.utils.extensions.isBlank
import com.shepherd.app.view_model.CreatedCarePointsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Nikita kohli on 22-07-22
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
        eventDetail = args.eventDetail

        setCommentAdapter()
        if (eventDetail != null) {
            initCarePointDetailViews(eventDetail!!)
        }

        carePointsViewModel.getCarePointsEventCommentsId(pageNumber, limit, eventDetail?.id ?: 0)
        fragmentCarePointDetailBinding.tvNotes.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    private fun setCommentAdapter() {
        //set comment adapter added in list
        commentAdapter = CarePointEventCommentAdapter(commentList, carePointsViewModel)
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
        carePointsViewModel.addedCarePointDetailCommentsLiveData.observeEvent(this) {
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
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun initCarePointDetailViews(payload: AddedEventModel) {
        fragmentCarePointDetailBinding.let {
            it.llImageWrapper.visibility = View.VISIBLE
            it.tvTitleCarePoint.text = payload.name
            it.tvLocation.text = payload.location
            val carePointDate = SimpleDateFormat("yyyy-MM-dd").parse(payload.date!!)!!
            it.tvDate.text = SimpleDateFormat("EEE, MMM dd").format(carePointDate)
            if (payload.time != null) {
                val carePointTime = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                    payload.date.plus(" ").plus(payload.time?.replace(" ", ""))
                )
                it.tvTime.text = SimpleDateFormat("hh:mm a").format(carePointTime!!)
            }


            //set comment added count  adapter
            val carePointsEventAdapter = CarePointsEventAdapter(payload.user_assignes)
            fragmentCarePointDetailBinding.recyclerViewEventAssigne.adapter = carePointsEventAdapter

            //set user detail
            Picasso.get().load(payload.loved_one_user_id_details.profilePhoto)
                .placeholder(R.drawable.ic_defalut_profile_pic)
                .into(fragmentCarePointDetailBinding.imageViewUser)
            fragmentCarePointDetailBinding.tvUsername.text =
                payload.loved_one_user_id_details.firstname.plus(" ")
                    .plus(if(payload.loved_one_user_id_details.lastname == null) "" else payload.loved_one_user_id_details.lastname)
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

            it.editTextMessage.visibility = View.VISIBLE
            it.sendCommentIV.visibility = View.VISIBLE
            when (payload.user_assignes.size) {
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
            }
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
                when {
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
                setNotesClickForLong(notes, !isSpanned,textView)
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