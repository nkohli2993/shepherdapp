package com.shepherd.app.ui.component.loved_one

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.user.Payload
import com.shepherd.app.databinding.FragmentLovedOneProfileBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.addLovedOne.AddLovedOneActivity
import com.shepherd.app.ui.component.addLovedOneCondition.AddLovedOneConditionActivity
import com.shepherd.app.ui.component.loved_one.adapter.LovedOneMedicalConditionAdapter
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.extensions.convertISOTimeToDate
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.view_model.LovedOneMedicalConditionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LovedOneProfileFragment : BaseFragment<FragmentLovedOneProfileBinding>(),
    View.OnClickListener {

    private val args: LovedOneProfileFragmentArgs by navArgs()
    private lateinit var fragmentLovedOneProfileBinding: FragmentLovedOneProfileBinding
    private val lovedOneMedicalConditionViewModel: LovedOneMedicalConditionViewModel by viewModels()
    private var lovedOneMedicalConditionAdapter: LovedOneMedicalConditionAdapter? = null

    private var careTeamModel: CareTeamModel? = null

    private var payload: Payload? = null
    private var TAG = "LovedOneProfileFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLovedOneProfileBinding =
            FragmentLovedOneProfileBinding.inflate(
                inflater,
                container,
                false
            )
        return fragmentLovedOneProfileBinding.root
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun observeViewModel() {
        // Observe Loved One's Medical Condition live data
        lovedOneMedicalConditionViewModel.lovedOneMedicalConditionResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    // hideLoading()
                    it.message?.let { showError(requireContext(), it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val payload = it.data.payload
                    val filteredPayload = payload.filter { payload ->
                        payload.conditions != null
                    }
                    if (filteredPayload.isEmpty()) {
                        // No medical conditions found
                        fragmentLovedOneProfileBinding.txtNoMedicalConditions.visibility =
                            View.VISIBLE
                        fragmentLovedOneProfileBinding.rvLovedOneMedicalConditions.visibility =
                            View.GONE
                    } else {
                        fragmentLovedOneProfileBinding.txtNoMedicalConditions.visibility =
                            View.GONE
                        fragmentLovedOneProfileBinding.rvLovedOneMedicalConditions.visibility =
                            View.VISIBLE

                        lovedOneMedicalConditionAdapter?.addData(payload)

                    }
                }
            }
        }

        // Observer user details api live data
        lovedOneMedicalConditionViewModel.lovedOneDetailsLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(requireContext(), it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    payload = it.data.payload
                    fragmentLovedOneProfileBinding.data = payload
                    Log.d(TAG, "LovedOneDetailWithRelation: $payload")

                    // Set DOB
                    if (payload?.userProfiles?.dob.isNullOrEmpty()) {
                        fragmentLovedOneProfileBinding.txtDOB.text = "No Date Of Birth Available"
                    } else {
                        fragmentLovedOneProfileBinding.txtDOB.text =
                            payload?.userProfiles?.dob.convertISOTimeToDate()
                    }

                    // Set Phone Number
                    if (payload?.userProfiles?.phoneCode.isNullOrEmpty() && payload?.userProfiles?.phoneNumber.isNullOrEmpty()) {
                        fragmentLovedOneProfileBinding.txtPhone.text = "No Phone Number Available"
                    } else {
                        fragmentLovedOneProfileBinding.txtPhone.text =
                            payload?.userProfiles?.phoneCode + " " + payload?.userProfiles?.phoneNumber
                    }

                    // Set Name
                    var name: String? = null
                    name = if (!payload?.userProfiles?.firstname.isNullOrEmpty()) {
                        if (!payload?.userProfiles?.lastname.isNullOrEmpty()) {
                            payload?.userProfiles?.firstname + " " + payload?.userProfiles?.lastname
                        } else {
                            payload?.userProfiles?.firstname
                        }
                    } else {
                        "LovedOne's Name Not Available"
                    }

                    fragmentLovedOneProfileBinding.tvName.text = name

                    // Get Place
                    val place = payload?.userLocation?.formattedAddress
                    // Get address
                    val address = payload?.userProfiles?.address
                    if (!address.isNullOrEmpty()) {
                        fragmentLovedOneProfileBinding.txtAddress.text = address
                    } else if (!place.isNullOrEmpty()) {
                        fragmentLovedOneProfileBinding.txtAddress.text = place
                    } else {
                        fragmentLovedOneProfileBinding.txtAddress.text = "No Address Available"
                    }
                }
            }
        }
    }


    override fun initViewBinding() {
        careTeamModel = args.careTeamModel

        Log.d(TAG, "careTeamModel: $careTeamModel ")

        fragmentLovedOneProfileBinding.listener = this
//        fragmentLovedOneProfileBinding.data = careTeamModel

        setLovedOneMedicalConditionsAdapter()

        // Get LovedOne's medical conditions
        careTeamModel?.love_user_id?.let {
            lovedOneMedicalConditionViewModel.getLovedOneMedicalConditions(
                it
            )
        }

        // Get user profile
//        lovedOneMedicalConditionViewModel.getUserDetails(careTeamModel?.love_user_id_details?.userProfileId)

        // Get Loved One's detail with relation
        careTeamModel?.love_user_id_details?.uid?.let {
            lovedOneMedicalConditionViewModel.getLovedOneDetailsWithRelation(
                it
            )
        }

    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_loved_one_profile
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.editMedicalConditionIV -> {
                val intent = Intent(requireContext(), AddLovedOneConditionActivity::class.java)
                intent.putExtra("source", Const.MEDICAL_CONDITION)
                intent.putExtra("love_one_id", careTeamModel?.love_user_id)
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            R.id.ivEdit -> {
                val intent = Intent(requireContext(), AddLovedOneActivity::class.java)
                intent.putExtra("source", "Loved One Profile")
//                intent.putExtra("care_model", careTeamModel)
                intent.putExtra("payload", payload)
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
        }
    }


    private fun setLovedOneMedicalConditionsAdapter() {
        lovedOneMedicalConditionAdapter =
            LovedOneMedicalConditionAdapter(lovedOneMedicalConditionViewModel)
        fragmentLovedOneProfileBinding.rvLovedOneMedicalConditions.adapter =
            lovedOneMedicalConditionAdapter
    }

    override fun onResume() {
        super.onResume()
        // Get user profile
//        lovedOneMedicalConditionViewModel.getUserDetails(careTeamModel?.love_user_id_details?.userProfileId)

        // Get Loved One's detail with relation
        careTeamModel?.love_user_id_details?.uid?.let {
            lovedOneMedicalConditionViewModel.getLovedOneDetailsWithRelation(
                it
            )
        }

        // Get Loved One's Medical conditions
        careTeamModel?.love_user_id?.let {
            lovedOneMedicalConditionViewModel.getLovedOneMedicalConditions(
                it
            )
        }
    }

}