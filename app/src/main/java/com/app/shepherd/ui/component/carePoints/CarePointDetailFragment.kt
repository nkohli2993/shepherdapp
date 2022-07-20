package com.app.shepherd.ui.component.carePoints

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.app.shepherd.R
import com.app.shepherd.data.dto.added_events.*
import com.app.shepherd.data.dto.dashboard.Payload
import com.app.shepherd.databinding.FragmentAddMemberBinding
import com.app.shepherd.databinding.FragmentCarePointDetailBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.carePoints.adapter.CarePointEventCommentAdapter
import com.app.shepherd.ui.component.carePoints.adapter.CarePointsEventAdapter
import com.app.shepherd.ui.component.memberDetails.MemberDetailsFragmentArgs
import com.app.shepherd.utils.extensions.isBlank
import com.app.shepherd.utils.observe
import com.app.shepherd.view_model.CreatedCarePointsViewModel
import java.text.SimpleDateFormat
import java.util.*


class CarePointDetailFragment : BaseFragment<FragmentCarePointDetailBinding>(),
    View.OnClickListener {
    private lateinit var fragmentCarePointDetailBinding: FragmentCarePointDetailBinding
    private var commentAdapter: CarePointEventCommentAdapter? = null
    private val args: CarePointDetailFragmentArgs by navArgs()
    private val carePointsViewModel: CreatedCarePointsViewModel by viewModels()
    private var id: Int? = null
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
        carePointsViewModel.getCarePointsDetailId(id ?: 0)
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
                    showToast("Comment added successfully")
                }
                is DataResult.Failure -> {
                    hideLoading()
                }
            }
        }

    }

    private fun initCarePointDetailViews(payload: AddedEventModel) {
        fragmentCarePointDetailBinding.let {
            it.tvTitleCarePoint.text = payload.name
            it.tvLocation.text = payload.location
            val carePointDate = SimpleDateFormat("yyyy-MM-dd").parse(payload.date)
            it.tvDate.text = SimpleDateFormat("EEE, MMM dd").format(carePointDate)
            if (payload.time != null) {
                val carePointTime = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                    payload.date.plus(" ").plus(payload.time?.replace(" ", ""))
                )
                it.tvTime.text = SimpleDateFormat("hh:mm a").format(carePointTime!!)
            }
            it.tvNotes.text = payload.notes

            //set comment added count  adapter
            val carePointsEventAdapter = CarePointsEventAdapter(payload.event_comments)
            fragmentCarePointDetailBinding.recyclerViewEvents.adapter = carePointsEventAdapter

            //set comment adapter added in list

            commentAdapter = CarePointEventCommentAdapter(payload.event_comments)
            fragmentCarePointDetailBinding.recyclerViewEvents.adapter = commentAdapter
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
                        )
                        carePointsViewModel.addEventCommentCarePoint(addEventComment)
                    }
                }
            }
        }
    }

}