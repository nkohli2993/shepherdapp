package com.shepherdapp.app.ui.component.addMember

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamRequestModel
import com.shepherdapp.app.data.dto.care_team.CareTeamRoles
import com.shepherdapp.app.data.dto.user.UserProfiles
import com.shepherdapp.app.databinding.FragmentAddMemberBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.addMember.adapter.AddMemberRoleAdapter
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.AddMemberViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddMemberFragment : BaseFragment<FragmentAddMemberBinding>(),
    View.OnClickListener, AdapterView.OnItemSelectedListener {

    private val addMemberViewModel: AddMemberViewModel by viewModels()

    private lateinit var fragmentAddMemberBinding: FragmentAddMemberBinding

    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 1
    private var selectedCareRole: CareTeamRoles? = null

    private var careRoles: ArrayList<CareTeamRoles>? = ArrayList()
    private var addMemberRoleAdapter: AddMemberRoleAdapter? = null
    private var TAG = "AddMemberFragment"
    private var selectedModule: String = ""
    private val uploadLockBoxFilesPermission = "2.1"

    private val isValid: Boolean
        get() {
            when {
                fragmentAddMemberBinding.edtEmail.text.toString().isEmpty() -> {
                    fragmentAddMemberBinding.edtEmail.error =
                        getString(R.string.enter_email)
                    fragmentAddMemberBinding.edtEmail.requestFocus()
                }

                selectedCareRole?.id == null -> {
                    showError(requireContext(), "Please select any role...")
                }

                selectedModule.isEmpty() -> {
                    showError(requireContext(), "Please select atleast one permission")
                }

                fragmentAddMemberBinding.edtRelationShip.text.toString().trim().isEmpty() -> {
                    fragmentAddMemberBinding.edtRelationShip.setText("")
                    fragmentAddMemberBinding.edtRelationShip.error =
                        getString(R.string.enter_relationship)
                    fragmentAddMemberBinding.edtRelationShip.requestFocus()
                }

                else -> {
                    return true
                }
            }
            return false
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddMemberBinding =
            FragmentAddMemberBinding.inflate(inflater, container, false)

        return fragmentAddMemberBinding.root
    }

    override fun initViewBinding() {
        fragmentAddMemberBinding.listener = this

        // Get Care Team Roles by hitting api
        fragmentAddMemberBinding.spRoles.onItemSelectedListener = this
        addMemberViewModel.getCareTeamRoles(pageNumber, limit, status)

        fragmentAddMemberBinding.switchLockBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fragmentAddMemberBinding.line.visibility = View.VISIBLE
                fragmentAddMemberBinding.layoutUploadFiles.visibility = View.VISIBLE
            } else {
                fragmentAddMemberBinding.line.visibility = View.GONE
                fragmentAddMemberBinding.layoutUploadFiles.visibility = View.GONE
            }
        }
    }

    override fun observeViewModel() {

        addMemberViewModel.careTeamRolesResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }

                is DataResult.Success -> {
                    hideLoading()
                    val careRoleList = it.data.payload.careRoles

                    careRoles = careRoleList.filter { careTeamRole ->
                        careTeamRole.slug != CareRole.CareTeamLead.slug
                    } as ArrayList

                    if (careRoles.isNullOrEmpty()) return@observeEvent
                    val careRole =
                        CareTeamRoles(null, "Select Role", null, null, null, null, null, null)
                    careRoles!!.add(0, careRole)
                    setRoleAdapter()
                }

                is DataResult.Failure -> {
                    hideLoading()
                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("CareTeam")
                        setMessage("No CareTeam Found")
                        setPositiveButton("OK") { _, _ ->
                            // navigateToDashboardScreen()
                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
            }
        }

        addMemberViewModel.addNewMemberCareTeamResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { it1 -> showError(requireContext(), it1) }
                }

                is DataResult.Loading -> {
                    showLoading("")
                }

                is DataResult.Success -> {
                    hideLoading()
                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("Shepherd")
                        setMessage("Invitation sent successfully.")
                        setPositiveButton("OK") { _, _ ->
                            backPress()
                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
            }
        }

    }

    private fun setRoleAdapter() {
        addMemberRoleAdapter = context?.let {
            careRoles?.let { it1 ->
                AddMemberRoleAdapter(it, R.layout.vehicle_spinner_drop_view_item, it1)
            }
        }
        fragmentAddMemberBinding.spRoles.adapter = addMemberRoleAdapter

        val careRole = addMemberRoleAdapter?.getItem(0)
        if (careRole != null) selectedCareRole = careRole
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
            }

            R.id.spinner_down_arrow_image -> {
                fragmentAddMemberBinding.spRoles.performClick()
            }

            R.id.btnInvitation -> {
                val email = fragmentAddMemberBinding.edtEmail.text.toString().trim()
                val roleID = selectedCareRole?.id
                // Get loggedInUserID
                val loggedInUser = Prefs.with(ShepherdApp.appContext)!!.getObject(
                    Const.USER_DETAILS,
                    UserProfiles::class.java
                )

                val loggedInUserID = addMemberViewModel.getLoggedInUserUUID()
                Log.d(TAG, "loggedInUserID : $loggedInUserID")

                // Get LovedOneId
                val lovedOneUUID =
                    Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID)
                Log.d(TAG, "LovedOneID : $lovedOneUUID")

                // Checked the selected state of Care Team
                val isCarePointsEnabled = fragmentAddMemberBinding.switchCarePoints.isChecked

                // Checked the selected state of Care Team
                val isLockBoxEnabled = fragmentAddMemberBinding.switchLockBox.isChecked

                // Checked the selected state of Care Team
                val isMyMedListEnabled = fragmentAddMemberBinding.switchMyMedList.isChecked

                // Checked the selected state of Care Team
                val isResourcesEnabled = fragmentAddMemberBinding.switchResources.isChecked

                val isUploadFilesSelected = fragmentAddMemberBinding.chkUploadFiles.isChecked

                selectedModule = ""
                if (isCarePointsEnabled) {
                    selectedModule += Modules.CarePoints.value.toString() + ","
                }
                if (isLockBoxEnabled) {
                    selectedModule += Modules.LockBox.value.toString() + ","
                }
                if (isUploadFilesSelected) {
                    selectedModule += "$uploadLockBoxFilesPermission,"
                }
                if (isMyMedListEnabled) {
                    selectedModule += Modules.MedList.value.toString() + ","
                }
                if (isResourcesEnabled) {
                    selectedModule += Modules.Resources.value.toString() + ","
                }
                if (selectedModule.endsWith(",")) {
                    selectedModule = selectedModule.substring(0, selectedModule.length - 1)
                }
                if (isValid) {
                    val addNewMemberRequestModel = AddNewMemberCareTeamRequestModel(
                        loggedInUserID,
                        null,
                        email,
                        lovedOneUUID,
                        roleID,
                        selectedModule,
                        fragmentAddMemberBinding.edtRelationShip.text.toString()
                    )

                    addMemberViewModel.addNewMemberCareTeam(addNewMemberRequestModel)
                }
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_member
    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        when (parent?.id) {
            R.id.sp_roles -> {
                if (position > 0) selectCareRole(position)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        selectCareRole(0)
    }

    private fun selectCareRole(position: Int) {
        val careRole = addMemberRoleAdapter?.getItem(position)
        if (careRole != null) selectedCareRole = careRole
    }
}

