package com.app.shepherd.ui.component.carePoints

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.added_events.AddedEventModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.FragmentAddMemberBinding
import com.app.shepherd.databinding.FragmentCarePointsBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.carePoints.adapter.CarePointsDayAdapter
import com.app.shepherd.utils.*
import com.app.shepherd.view_model.CreatedCarePointsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class CarePointsFragment : BaseFragment<FragmentAddMemberBinding>(),
    View.OnClickListener {
    private lateinit var fragmentCarePointsBinding: FragmentCarePointsBinding
    private var carePoints: ArrayList<AddedEventModel>? = ArrayList()
    private var carePointsAdapter: CarePointsDayAdapter? = null
    private val carePointsViewModel: CreatedCarePointsViewModel by viewModels()
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var startDate:String = ""
    private var endDate:String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCarePointsBinding =
            FragmentCarePointsBinding.inflate(inflater, container, false)

        return fragmentCarePointsBinding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun initViewBinding() {
        fragmentCarePointsBinding.listener = this
        val calendar = Calendar.getInstance().time
        startDate = SimpleDateFormat("yyyy-MM-dd").format(calendar)
        endDate = SimpleDateFormat("yyyy-MM-dd").format(calendar)
        carePointsViewModel.getCarePointsByLovedOneId(pageNumber, limit, startDate, endDate)
        setCarePointsAdapter()

    }

    override fun observeViewModel() {
        observe(carePointsViewModel.openMemberDetails,::openCarePointDetails)
        carePointsViewModel.carePointsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    carePoints = it.data.payload.results
                    if (carePoints.isNullOrEmpty()) return@observeEvent
                    carePointsAdapter?.updateCarePoints(carePoints!!)
                }
                is DataResult.Failure -> {
                    hideLoading()
                    carePoints?.clear()
                    carePoints?.let { it1 -> carePointsAdapter?.updateCarePoints(it1) }

                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("Care Points")
                        setMessage("No Care point found")
                        setPositiveButton("OK") { _, _ ->
                            // navigateToDashboardScreen()
                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
            }
        }

    }

    private fun openCarePointDetails(navigateEvent: SingleEvent<AddedEventModel>) {
        navigateEvent.getContentIfNotHandled()?.let {
            findNavController().navigate(
                CarePointsFragmentDirections.actionCarePointsToChatFragment(
                    Const.CARE_POINT
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
                status.errorCode?.let { carePointsViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentCarePointsBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentCarePointsBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setCarePointsAdapter() {
        carePointsAdapter = CarePointsDayAdapter(carePointsViewModel,carePoints!!)
        fragmentCarePointsBinding.recyclerViewEventDays.adapter = carePointsAdapter
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
//            R.id.buttonAddNewTask -> {
//                p0.findNavController().navigate(R.id.action_care_points_to_add_new_task)
//            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_care_points
    }


}

