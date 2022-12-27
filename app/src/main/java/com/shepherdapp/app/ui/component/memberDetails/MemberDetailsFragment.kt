package com.shepherdapp.app.ui.component.memberDetails

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.care_team.UpdateCareTeamMemberRequestModel
import com.shepherdapp.app.databinding.FragmentMemberDetailsBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.memberDetails.adapter.MemberModulesAdapter
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Modules
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.extensions.getStringWithHyphen
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.MemberDetailsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class MemberDetailsFragment : BaseFragment<FragmentMemberDetailsBinding>(),
    View.OnClickListener {

    private val memberDetailsViewModel: MemberDetailsViewModel by viewModels()

    private lateinit var fragmentMemberDetailsBinding: FragmentMemberDetailsBinding

    private val args: MemberDetailsFragmentArgs by navArgs()
    private var careTeam: CareTeamModel? = null
    private var selectedModule: String = ""
    private val TAG = "MemberDetailsFragment"


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

        setRestrictionModuleAdapter()
        careTeam = args.careTeam
        initView()

    }

    private fun initView() {
        careTeam?.user_id_details.let {
            // Set profile pic
            Picasso.get().load(it?.profilePhoto)
                .placeholder(R.drawable.ic_defalut_profile_pic)
                .into(fragmentMemberDetailsBinding.imgCareTeamMember)

            // Set Name
            fragmentMemberDetailsBinding.txtCareTeamMemberName.text =
                it?.firstname + " " + it?.lastname

            // Set Email ID
            fragmentMemberDetailsBinding.txtEmailCare.text = it?.email

            // Set Address
            fragmentMemberDetailsBinding.txtAddressCare.text =
                it?.address ?: "No address available"
            // Set Phone Number
            val phone = "+" + it?.phone
            val phoneArr = it?.phone?.split("-")
            val phoneCode = phoneArr?.get(0)
            val phoneNumber = phoneArr?.get(1)
            //val phoneWithHyphen = phoneNumber?.let { it1 -> getStringWithHyphen(it1) }
            val phoneWithHyphen = phoneNumber?.getStringWithHyphen(phoneNumber)
            val phoneNo = "$phoneCode $phoneWithHyphen"
            Log.d(TAG, "initView: $phoneNo")


            if (phoneNo.toString().contains("+")) {
                fragmentMemberDetailsBinding.txtPhoneCare.text =
                    phoneNo ?: "Phone Number Not Available"

            } else {
                fragmentMemberDetailsBinding.txtPhoneCare.text =
                    "+" + phoneNo ?: "Phone Number Not Available"

            }
        }

        // if the loggedIn user is the Care Team Leader, then only show the Remove and Save Changes button
        val isLoggedInUserTeamLead =
            Prefs.with(ShepherdApp.appContext)?.getBoolean(Const.Is_LOGGED_IN_USER_TEAM_LEAD, false)
                ?: false
        if (isLoggedInUserTeamLead) {
            /*val loggedInUserUUID =
                Prefs.with(ShepherdApp.appContext)!!.getString(Const.UUID, "")*/

            /*  if (loggedInUserUUID == careTeam?.user_id_details!!.uid){
                  fragmentMemberDetailsBinding.btnDelete.visibility = View.GONE
              }
              else{
                  fragmentMemberDetailsBinding.btnDelete.visibility = View.VISIBLE
              }*/

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
            checkPermission(permission.toInt())
        } else {
            val perList = permission?.split(',')?.map { it.trim() }
            for (i in perList?.indices!!) {
                checkPermission(perList[i].toInt())
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
    }

    private fun showButtons() {
        fragmentMemberDetailsBinding.btnDelete.visibility = View.VISIBLE
        fragmentMemberDetailsBinding.btnUpdate.visibility = View.VISIBLE
    }

    fun getStringWithHyphen(str: String): String {
        var resultant = ""
        resultant = if (str.length <= 3) str else "" + str[0] + str[1] + str[1]
        for (i in 3 until str.length) {
            if (i % 3 == 0) {
                resultant += "-" + str[i]
            } else {
                resultant += str[i]
            }
        }
        return resultant
    }

    private fun checkPermission(s: Int) {
        when {
            Modules.CarePoints.value == s -> {
                fragmentMemberDetailsBinding.switchCarePoints.isChecked = true
            }
            Modules.LockBox.value == s -> {
                fragmentMemberDetailsBinding.switchLockBox.isChecked = true
            }
            Modules.MedList.value == s -> {
                fragmentMemberDetailsBinding.switchMyMedList.isChecked = true
            }
            Modules.Resources.value == s -> {
                fragmentMemberDetailsBinding.switchResources.isChecked = true
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


    }

    private fun setRestrictionModuleAdapter() {
        val memberModulesAdapter = MemberModulesAdapter(memberDetailsViewModel)
//        fragmentMemberDetailsBinding.recyclerViewModules.adapter = memberModulesAdapter

        /* fragmentMemberDetailsBinding.recyclerViewModules.addItemDecoration(
             DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
         )*/

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
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
                Log.d(TAG, "onClick: selectedModule : $selectedModule")
                if (selectedModule.endsWith(",")) {
                    selectedModule = selectedModule.substring(0, selectedModule.length - 1)
                }
                Log.d(TAG, "onClick: selectedModule after removing last comma: $selectedModule")

                // Update Care Team Member Detail
                if (selectedModule.isEmpty()) {
                    showError(requireContext(), "Please select atleast one permission.")
                } else {
                    careTeam?.id?.let {
                        memberDetailsViewModel.updateCareTeamMember(
                            it,
                            UpdateCareTeamMemberRequestModel(selectedModule)
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

