package com.app.shepherd.ui.component.carePoints

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.app.shepherd.R
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.dto.added_events.*
import com.app.shepherd.databinding.FragmentCarePointDetailBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.carePoints.adapter.CarePointEventCommentAdapter
import com.app.shepherd.ui.component.carePoints.adapter.CarePointsEventAdapter
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Prefs
import com.app.shepherd.utils.extensions.isBlank
import com.app.shepherd.view_model.CreatedCarePointsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.ArrayList

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
        commentAdapter = CarePointEventCommentAdapter(commentList)
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
            it.tvNotes.text = payload.notes

            //set comment added count  adapter
            val carePointsEventAdapter = CarePointsEventAdapter(payload.user_assignes)
            fragmentCarePointDetailBinding.recyclerViewEventAssigne.adapter = carePointsEventAdapter

            //set user detail
            Picasso.get().load(carePointsViewModel.getUserDetail()?.profilePhoto)
                .placeholder(R.drawable.ic_defalut_profile_pic)
                .into(fragmentCarePointDetailBinding.imageViewUser)
            fragmentCarePointDetailBinding.tvUsername.text =
                carePointsViewModel.getUserDetail()?.firstname.plus(" ")
                    .plus(carePointsViewModel.getUserDetail()?.lastname)
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

}