package com.shepherdapp.app.ui.component.memberDetails

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.added_events.UserAssigneDetail
import com.shepherdapp.app.data.dto.added_events.UserAssigneeModel
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.care_team.UpdateCareTeamMemberRequestModel
import com.shepherdapp.app.databinding.FragmentMemberDetailsBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Modules
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.extensions.*
import com.shepherdapp.app.utils.setImageFromUrl
import com.shepherdapp.app.view_model.MemberDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class MemberDetailsFragment : BaseFragment<FragmentMemberDetailsBinding>(),
    View.OnClickListener {

    private val memberDetailsViewModel: MemberDetailsViewModel by viewModels()
    private lateinit var fragmentMemberDetailsBinding: FragmentMemberDetailsBinding
    private var careTeam: CareTeamModel? = null
    private var selectedModule: String = ""
    private val logTag = "MemberDetailsFragment"
    private var id:String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMemberDetailsBinding =
            FragmentMemberDetailsBinding.inflate(inflater, container, false)

        return fragmentMemberDetailsBinding.root
    }

    override fun initViewBinding() {
        fragmentMemberDetailsBinding.listener = this
        id = arguments?.getString("id")
        try {
            (fragmentMemberDetailsBinding.txtEmailCare.text as Spannable).stripUnderlines()
            (fragmentMemberDetailsBinding.txtPhoneCare.text as Spannable).stripUnderlines()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Get Care Teams by lovedOne Id
        fragmentMemberDetailsBinding.scrollView.isVisible = false
        memberDetailsViewModel.getCareTeamsDetail(id!!)


    }

    @SuppressLint("SetTextI18n")
    private fun setDataValue() {
        fragmentMemberDetailsBinding.scrollView.isVisible = true
        // Set Email ID
        fragmentMemberDetailsBinding.txtRelationCare.text = careTeam?.relation_name ?: getString(R.string.relationship_not_available)
        careTeam?.user_id_details.let {
            // Set profile pic
            fragmentMemberDetailsBinding.imgCareTeamMember.setImageFromUrl(
                it?.profilePhoto,
                it?.firstname, it?.lastname
            )

            // Set Name
            fragmentMemberDetailsBinding.txtCareTeamMemberName.text =
                it?.firstname + " " + it?.lastname

            // Set Email ID
            fragmentMemberDetailsBinding.txtEmailCare.text = it?.email


            // Set Address
            fragmentMemberDetailsBinding.txtAddressCare.text =
                it?.address ?: getString(R.string.no_address_available)
            // Set Phone Number
            var phone = "+" + it?.phone
            val phoneArr = it?.phone?.split("-")
            Log.d(logTag, "phoneArr: $phoneArr $phone")
            val phoneCode = phoneArr?.get(0)
            val phoneNumber = phoneArr?.get(1)
            if(!phoneNumber.toString().lowercase().contains("null")) {
                val phoneWithHyphen = phoneNumber?.getStringWithHyphen(phoneNumber)
                val phoneNo = "$phoneCode $phoneWithHyphen"
                Log.d(logTag, "initView: $phoneNo")


                if (phoneNo.contains("+")) {
                    fragmentMemberDetailsBinding.txtPhoneCare.text =
                        phoneNo ?: "Phone Number Not Available"

                } else {
                    fragmentMemberDetailsBinding.txtPhoneCare.text =
                        "+" + phoneNo ?: getString(R.string.phone_number_not_available)

                }
            }
            else{
                fragmentMemberDetailsBinding.txtPhoneCare.text =
                    getString(R.string.phone_number_not_available)
            }



            phone = fragmentMemberDetailsBinding.txtPhoneCare.text.toString()

            if (phone != getString(R.string.phone_number_not_available)) {
                fragmentMemberDetailsBinding.txtPhoneCare.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color._399282
                    )
                )
            } else {
                fragmentMemberDetailsBinding.txtPhoneCare.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color._192032
                    )
                )
                fragmentMemberDetailsBinding.txtPhoneCare.setLinkTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color._192032
                    )
                )
            }

        }

        // if the loggedIn user is the Care Team Leader, then only show the Remove and Save Changes button
        val isLoggedInUserTeamLead =
            Prefs.with(ShepherdApp.appContext)?.getBoolean(Const.Is_LOGGED_IN_USER_TEAM_LEAD, false)
                ?: false
        if (isLoggedInUserTeamLead) {

            val loggedInUserUUID = memberDetailsViewModel.getLoggedInUserUUID()
            // Check if the uuiD of loggedIn user matches the uuid of care team member
            if (loggedInUserUUID == careTeam?.user_id_details?.uid) {
                hideButtons()
                makeSwitchesNonClickable()
            } else {
                showButtons()
            }

        } else {
            hideButtons()
            makeSwitchesNonClickable()

        }

        // Set role
        fragmentMemberDetailsBinding.txtCareTeamMemberDesignation.text = careTeam?.careRoles?.name

        //get permissions
        val permission = careTeam?.permission
        if (permission?.length == 1) {
            checkPermission(permission)
        } else {
            val perList = permission?.split(',')?.map { it.trim() }
            for (i in perList?.indices!!) {
                checkPermission(perList[i])
            }
        }
    }

    private fun makeSwitchesNonClickable() {
        // Make switches non clickable
        fragmentMemberDetailsBinding.switchCarePoints.isClickable = false
        fragmentMemberDetailsBinding.switchLockBox.isClickable = false
        fragmentMemberDetailsBinding.switchMyMedList.isClickable = false
        fragmentMemberDetailsBinding.switchResources.isClickable = false
    }

    private fun hideButtons() {
        fragmentMemberDetailsBinding.btnDelete.visibility = View.GONE
        fragmentMemberDetailsBinding.btnUpdate.visibility = View.GONE
        fragmentMemberDetailsBinding.ivEdit.visibility = View.GONE
//         hide cards according to permission
        fragmentMemberDetailsBinding.carPointCD.visibility = View.GONE
        fragmentMemberDetailsBinding.lockBoxCD.visibility = View.GONE
        fragmentMemberDetailsBinding.medlistCD.visibility = View.GONE
        fragmentMemberDetailsBinding.resourcesCD.visibility = View.GONE
    }

    private fun showButtons() {
        fragmentMemberDetailsBinding.btnDelete.visibility = View.VISIBLE
        fragmentMemberDetailsBinding.btnUpdate.visibility = View.VISIBLE
        fragmentMemberDetailsBinding.chatG.visibility = View.VISIBLE
    }


    private fun checkPermission(s: String) {
        when {
            Modules.CarePoints.value == s -> {
                fragmentMemberDetailsBinding.switchCarePoints.isChecked = true
                fragmentMemberDetailsBinding.carPointCD.visibility = View.VISIBLE
            }
            Modules.LockBox.value == s -> {
                fragmentMemberDetailsBinding.switchLockBox.isChecked = true
                fragmentMemberDetailsBinding.lockBoxCD.visibility = View.VISIBLE
            }
            Modules.MedList.value == s -> {
                fragmentMemberDetailsBinding.switchMyMedList.isChecked = true
                fragmentMemberDetailsBinding.medlistCD.visibility = View.VISIBLE
            }
            Modules.Resources.value == s -> {
                fragmentMemberDetailsBinding.switchResources.isChecked = true
                fragmentMemberDetailsBinding.resourcesCD.visibility = View.VISIBLE
            }
        }

    }

    override fun observeViewModel() {
        memberDetailsViewModel.deleteCareTeamMemberLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(requireContext(), it.data.message.toString())
                    backPress()
                }
            }
        }

        memberDetailsViewModel.updateCareTeamMemberLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    //showSuccess(requireContext(), it.data.message.toString())
                    showSuccess(requireContext(), "CareTeam Member updated successfully...")
                    backPress()
                }
            }
        }

        memberDetailsViewModel.careTeamsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    // Get Pending Invites
                    careTeam =  it.data.payload
                    setDataValue()
                }
                is DataResult.Failure -> {
                    hideLoading()

                }
            }
        }

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
            }
            R.id.ivEdit -> {
                val action =
                    MemberDetailsFragmentDirections.actionNavEditTeamMemberDetails(careTeam!!)
                findNavController().navigate(action)
            }
            R.id.ivChat -> {
                val detail = UserAssigneDetail(
                    careTeam!!.user_id_details!!.id,
                    careTeam!!.user_id_details!!.id,
                    careTeam!!.user_id_details!!.firstname,
                    careTeam!!.user_id_details!!.lastname,
                    careTeam!!.user_id_details!!.dob,
                    careTeam!!.user_id_details!!.address,
                    careTeam!!.user_id_details!!.phone,
                    careTeam!!.user_id_details!!.phone,
                    careTeam!!.user_id_details!!.profilePhoto,
                    null, null, "", "", ""
                )
                findNavController().navigate(
                    R.id.action_new_message_to_chat,
                    bundleOf("assignee_user" to detail,"room_id" to "")
                )
            }
            R.id.btnDelete -> {
                val builder = AlertDialog.Builder(requireContext())
                val dialog = builder.apply {
                    setTitle("Remove CareTeam Member")
                    setMessage("Are you sure you want to remove the careTeam member?")
                    setPositiveButton("Yes") { _, _ ->
                        // Do not remove the Care Team Lead
                        // Check the member id should not match with the uuid of loggedIn user and slug value should not match with care_team_lead
                        val loggedInUserUUID =
                            Prefs.with(ShepherdApp.appContext)!!.getString(Const.UUID, "")

                        if (loggedInUserUUID == careTeam?.love_user_id_details!!.uid /*&& CareRole.CareTeamLead.slug == careTeam?.careRoles?.slug*/) {
                            showError(requireContext(), "You can not remove the CareTeam Lead...")
                        } else {
                            careTeam?.id?.let { memberDetailsViewModel.deleteCareTeamMember(it) }
                        }
                    }
                    setNegativeButton("No") { _, _ ->

                    }
                }.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

            }
            R.id.btnUpdate -> {
                // Checked the selected state of Care Team
                val isCarePointsEnabled = fragmentMemberDetailsBinding.switchCarePoints.isChecked

                // Checked the selected state of Care Team
                val isLockBoxEnabled = fragmentMemberDetailsBinding.switchLockBox.isChecked

                // Checked the selected state of Care Team
                val isMyMedListEnabled = fragmentMemberDetailsBinding.switchMyMedList.isChecked

                // Checked the selected state of Care Team
                val isResourcesEnabled = fragmentMemberDetailsBinding.switchResources.isChecked

                selectedModule = ""
                if (isCarePointsEnabled) {
                    selectedModule += Modules.CarePoints.value.toString() + ","
                }
                if (isLockBoxEnabled) {
                    selectedModule += Modules.LockBox.value.toString() + ","
                }
                if (isMyMedListEnabled) {
                    selectedModule += Modules.MedList.value.toString() + ","
                }
                if (isResourcesEnabled) {
                    selectedModule += Modules.Resources.value.toString() + ","
                }
                Log.d(logTag, "onClick: selectedModule : $selectedModule")
                if (selectedModule.endsWith(",")) {
                    selectedModule = selectedModule.substring(0, selectedModule.length - 1)
                }
                Log.d(logTag, "onClick: selectedModule after removing last comma: $selectedModule")

                // Update Care Team Member Detail
                if (selectedModule.isEmpty()) {
                    showError(requireContext(), "Please select atleast one permission.")
                } else {
                    careTeam?.id?.let {
                        memberDetailsViewModel.updateCareTeamMember(
                            it,
                            UpdateCareTeamMemberRequestModel(selectedModule,careTeam?.relation_name)
                        )
                    }
                }

            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_member_details
    }


}

