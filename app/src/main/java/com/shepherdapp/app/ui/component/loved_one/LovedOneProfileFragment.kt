package com.shepherdapp.app.ui.component.loved_one

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
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.added_events.ResultEventModel
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.user.Payload
import com.shepherdapp.app.databinding.FragmentLovedOneProfileBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.base.listeners.UpdateViewOfParentListener
import com.shepherdapp.app.ui.component.addLovedOne.AddLovedOneActivity
import com.shepherdapp.app.ui.component.addLovedOneCondition.AddLovedOneConditionActivity
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.loved_one.adapter.LovedOneMedicalConditionAdapter
import com.shepherdapp.app.utils.CareRole
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.extensions.convertISOTimeToDate
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.setImageFromUrl
import com.shepherdapp.app.view_model.LovedOneMedicalConditionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
/*
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

                    var payloadList: ArrayList<com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions.Payload> =
                        arrayListOf()
                    for (i in it.data.payload) {
                        if (i.conditionId != null) {
                            payloadList.add(i)
                        }
                    }

                    if (payloadList.isEmpty()) {
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

                        lovedOneMedicalConditionAdapter?.addData(payloadList)

                    }
                }
            }
        }
*/

        // Observer user details api live data
        lovedOneMedicalConditionViewModel.lovedOneDetailsLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    payload = result.data.payload
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
                            if ((payload?.userProfiles?.phoneCode
                                    ?: "").startsWith("+")
                            ) payload?.userProfiles?.phoneCode
                                ?: "" else "+${payload?.userProfiles?.phoneCode}" + " " + payload?.userProfiles?.phoneNumber
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
                        getString(R.string.loved_one_not_available)
                    }

                    fragmentLovedOneProfileBinding.imageViewUser.setImageFromUrl(payload?.userProfiles?.profilePhoto,
                    payload?.userProfiles?.firstname,payload?.userProfiles?.lastname)

                    fragmentLovedOneProfileBinding.tvName.text = name
                    val place = payload?.userLocation?.formattedAddress
                    //show selected address
                    if (!place.isNullOrEmpty()) {
                        fragmentLovedOneProfileBinding.txtAddress.text = place
                    } else {
                        fragmentLovedOneProfileBinding.txtAddress.text =
                            getString(R.string.no_address_avaialble)
                    }

                    // Get address
                    val address = payload?.userProfiles?.address
                    if (!address.isNullOrEmpty()) {
                        fragmentLovedOneProfileBinding.txtCustomAddress.text = address
                    } else if (!place.isNullOrEmpty()) {
                        fragmentLovedOneProfileBinding.txtCustomAddress.text = place
                    } else {
                        fragmentLovedOneProfileBinding.txtCustomAddress.text =
                            getString(R.string.no_address_avaialble)
                    }

                    // to get user conditions
                    val payloadList: ArrayList<com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions.Payload> =
                        arrayListOf()
                    for (i in payload!!.userConditions) {
                        if (i.conditionId != null) {
                            payloadList.add(i)
                        }
                    }

                    Collections.sort(
                        payloadList,
                        object :
                            Comparator<com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions.Payload?> {
                            var df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                            override fun compare(
                                o1: com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions.Payload?,
                                o2: com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions.Payload?
                            ): Int {
                                return try {
                                    o1!!.conditions?.name!!.compareTo(o2!!.conditions?.name!!)
                                } catch (e: Exception) {
                                    throw IllegalArgumentException(e)
                                }
                            }
                        })

                    if (payloadList.isEmpty()) {
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

                        lovedOneMedicalConditionAdapter?.addData(payloadList)

                    }

                }
            }
        }
    }


    override fun initViewBinding() {
        careTeamModel = args.careTeamModel

        Log.d(TAG, "careTeamModel: $careTeamModel ")

        fragmentLovedOneProfileBinding.listener = this

        setLovedOneMedicalConditionsAdapter()

        // Get LovedOne's medical conditions
/*
        careTeamModel?.love_user_id?.let {
            lovedOneMedicalConditionViewModel.getLovedOneMedicalConditions(
                it
            )
        }
*/
        // Get Loved One's detail with relation
        careTeamModel?.love_user_id_details?.uid?.let {
            lovedOneMedicalConditionViewModel.getLovedOneDetailsWithRelation(
                it
            )
        }

        // Check if loggedIn User is CareTeam Leader for selected lovedOne
        val lovedOneDetail = lovedOneMedicalConditionViewModel.getLovedOneDetail()
        val isCareTeamLeader = lovedOneMedicalConditionViewModel.isLoggedInUserCareTeamLeader()

        val isNewVisible =
            (lovedOneDetail?.careRoles?.slug == CareRole.CareTeamLead.slug) || (isCareTeamLeader == true)
        if(isNewVisible){
            fragmentLovedOneProfileBinding.ivEdit.visibility = View.VISIBLE
            fragmentLovedOneProfileBinding.editMedicalConditionIV.visibility = View.VISIBLE
        }else{
            fragmentLovedOneProfileBinding.ivEdit.visibility = View.INVISIBLE
            fragmentLovedOneProfileBinding.editMedicalConditionIV.visibility = View.INVISIBLE
        }

       /* val isNewVisible = when (lovedOneDetail?.careRoles?.slug) {
            CareRole.CareTeamLead.slug -> {
                fragmentLovedOneProfileBinding.ivEdit.visibility = View.VISIBLE
                fragmentLovedOneProfileBinding.editMedicalConditionIV.visibility = View.VISIBLE
                true
            }
            else -> {
                fragmentLovedOneProfileBinding.ivEdit.visibility = View.INVISIBLE
                fragmentLovedOneProfileBinding.editMedicalConditionIV.visibility = View.INVISIBLE
                false
            }
        }*/

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

        /* // Get Loved One's Medical conditions
         careTeamModel?.love_user_id?.let {
             lovedOneMedicalConditionViewModel.getLovedOneMedicalConditions(
                 it
             )
         }*/
    }

}