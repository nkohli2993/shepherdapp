package com.app.shepherd.ui.component.carePoints

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.app.shepherd.R
import com.app.shepherd.data.dto.added_events.*
import com.app.shepherd.databinding.FragmentCarePointDetailBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.carePoints.adapter.CarePointEventCommentAdapter
import com.app.shepherd.ui.component.carePoints.adapter.CarePointsEventAdapter
import com.app.shepherd.utils.extensions.isBlank
import com.app.shepherd.view_model.CreatedCarePointsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class CarePointDetailFragment : BaseFragment<FragmentCarePointDetailBinding>(),
    View.OnClickListener {
    private lateinit var fragmentCarePointDetailBinding: FragmentCarePointDetailBinding
    private var commentAdapter: CarePointEventCommentAdapter? = null
    private val args: CarePointDetailFragmentArgs by navArgs()
    private var commentList: ArrayList<EventCommentUserDetailModel> = ArrayList()
    private val carePointsViewModel: CreatedCarePointsViewModel by viewModels()
    private var id: Int? = null
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

    @SuppressLint("SimpleDateFormat")
    override fun initViewBinding() {
        fragmentCarePointDetailBinding.listener = this
        id = args.source

        setCommentAdapter()
        carePointsViewModel.getCarePointsDetailId(id ?: 0)
        carePointsViewModel.getCarePointsEventCommentsId(pageNumber, limit, id ?: 0)
    }

    private fun setCommentAdapter() {
        //set comment adapter added in list
        commentAdapter = CarePointEventCommentAdapter(commentList,carePointsViewModel)
        fragmentCarePointDetailBinding.recyclerViewChat.adapter = commentAdapter
    }

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
                            event_id = id ?: 0,
                            comment = fragmentCarePointDetailBinding.editTextMessage.text.toString(),
                            created_at = it.data.payload.created_at ,
                            user_details = UserDetailAssigneModel(
                                id = carePointsViewModel.getUserDetail()?.id,
                                user_profiles = UserAssigneDetail(
                                    id = carePointsViewModel.getUserDetail()?.id,
                                    firstname = carePointsViewModel.getUserDetail()?.firstname,
                                    lastname = carePointsViewModel.getUserDetail()?.lastname,
                                    profilePhoto = carePointsViewModel.getUserDetail()?.profilePhoto
                                )
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
                    if (commentList.isNullOrEmpty()) return@observeEvent
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
            Picasso.get().load(carePointsViewModel.getLovedUserDetail()?.profilePhoto)
                .placeholder(R.drawable.ic_defalut_profile_pic)
                .into(fragmentCarePointDetailBinding.imageViewUser)
            fragmentCarePointDetailBinding.tvUsername.text =
                carePointsViewModel.getLovedUserDetail()?.firstname.plus(" ")
                    .plus(carePointsViewModel.getLovedUserDetail()?.lastname)
            // shwo created on time
            val dateTime = (payload.created_at?:"").replace(".000Z","").replace("T"," ")
            val commentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                dateTime
            )
            val df =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            df.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date = df.parse(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(commentTime!!))!!
            df.timeZone = TimeZone.getDefault()
            it.tvPostedDate.text = SimpleDateFormat("EEE, MMM dd").format(date);
            if((payload.notes?:"").length>50){
                setNotesClickForLong(payload.notes!!,true)
            }
            else{
                it.tvNotes.text = payload.notes
            }

        }
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
                            event_id = id ?: 0,
                            comment = fragmentCarePointDetailBinding.editTextMessage.text.toString()
                                .trim()
                        )
                        carePointsViewModel.addEventCommentCarePoint(addEventComment)
                    }
                }
            }
        }
    }

    private fun setNotesClickForLong(notes:String,isSpanned:Boolean){
        val showNotes = if(isSpanned){
            notes.substring(0,50).plus(" ... View more.")
        }
        else{
            notes.plus(" ... View less.")
        }
        val ss = SpannableString(showNotes)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(p0: View) {
               //click to show whole note
                setNotesClickForLong(notes,!isSpanned)
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
            }
        }
        if(showNotes.length<=65){
            ss.setSpan(clickableSpan, 50, 65, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        else{
            ss.setSpan(clickableSpan, showNotes.length-15, showNotes.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        fragmentCarePointDetailBinding.tvNotes.text = ss
        fragmentCarePointDetailBinding.tvNotes.movementMethod = LinkMovementMethod.getInstance()
        fragmentCarePointDetailBinding.tvNotes.highlightColor = Color.GREEN
    }
}