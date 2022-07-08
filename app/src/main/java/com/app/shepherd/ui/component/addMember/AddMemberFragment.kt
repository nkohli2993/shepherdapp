package com.app.shepherd.ui.component.addMember

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.add_new_member_care_team.AddNewMemberCareTeamRequestModel
import com.app.shepherd.data.dto.care_team.CareRoles
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.user.UserProfiles
import com.app.shepherd.databinding.FragmentAddMemberBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.addMember.adapter.AddMemberRoleAdapter
import com.app.shepherd.ui.component.addMember.adapter.RestrictionsModuleAdapter
import com.app.shepherd.utils.*
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.extensions.showSuccess
import com.app.shepherd.view_model.AddMemberViewModel
import com.google.android.material.snackbar.Snackbar
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
    private var selectedCareRole: CareRoles? = null

    private var careRoles: ArrayList<CareRoles>? = ArrayList()
    private var addMemberRoleAdapter: AddMemberRoleAdapter? = null
    private var TAG = "AddMemberFragment"
    private var selectedModule: String = ""

    private val isValid: Boolean
        get() {
            when {
                fragmentAddMemberBinding.edtEmail.text.toString().isEmpty() -> {
                    fragmentAddMemberBinding.edtEmail.error =
                        getString(R.string.please_enter_email_id)
                    fragmentAddMemberBinding.edtEmail.requestFocus()
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

        setRestrictionModuleAdapter()
    }

    override fun observeViewModel() {

        addMemberViewModel.careTeamRolesResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    careRoles = it.data.payload.careroles
                    if (careRoles.isNullOrEmpty()) return@observeEvent
                    setRoleAdapter()
                }

                is DataResult.Failure -> {
                    hideLoading()
                    // it.message?.let { showError(this, it.toString()) }
                    //binding.layoutCareTeam.visibility = View.GONE
                    //binding.txtNoCareTeamFound.visibility = View.VISIBLE
                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("Care Teams")
                        setMessage("No Care Team Found")
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
                    //it.data.message?.let { it1 -> showSuccess(requireContext(), it1) }
                    showSuccess(
                        requireContext(),
                        "Request sent to the member for joining care team successfully..."
                    )
                    backPress()
                }
            }
        }

    }


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {

    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentAddMemberBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentAddMemberBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
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
        // fragmentAddMemberBinding.recyclerViewMemberRole.adapter = addMemberRoleAdapter
    }

    private fun setRestrictionModuleAdapter() {
        val restrictionsModuleAdapter = RestrictionsModuleAdapter(addMemberViewModel)
//        fragmentAddMemberBinding.recyclerViewModules.adapter = restrictionsModuleAdapter

/*       fragmentAddMemberBinding.recyclerViewModules.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )*/

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
            }

            R.id.btnInvitation -> {
                val email = fragmentAddMemberBinding.edtEmail.text.toString().trim()
                val roleID = selectedCareRole?.id
                // Get loggedInUserID
                val loggedInUser = Prefs.with(ShepherdApp.appContext)!!.getObject(
                    Const.USER_DETAILS,
                    UserProfiles::class.java
                )

                val loggedInUserID = addMemberViewModel.getLovedOneUUId()
                Log.d(TAG, "loggedInUserID : $loggedInUserID")

                // Get LovedOneId
                val lovedOneId = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_ID)
                Log.d(TAG, "LovedOneID : $lovedOneId")

                // Checked the selected state of Care Team
                val isCareTeamEnabled = fragmentAddMemberBinding.switchCareTeam.isChecked

                // Checked the selected state of Care Team
                val isLockBoxEnabled = fragmentAddMemberBinding.switchLockBox.isChecked

                // Checked the selected state of Care Team
                val isMyMedListEnabled = fragmentAddMemberBinding.switchMyMedList.isChecked

                // Checked the selected state of Care Team
                val isResourcesEnabled = fragmentAddMemberBinding.switchResources.isChecked
                selectedModule = ""
                if (isCareTeamEnabled) {
                    selectedModule += Modules.CareTeam.value.toString() + ","
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

                if (isValid) {
                    val addNewMemberRequestModel = AddNewMemberCareTeamRequestModel(
                        loggedInUserID,
                        null,
                        email,
                        lovedOneId,
                        roleID,
                        selectedModule
                    )

                    addMemberViewModel.addNewMemberCareTeam(addNewMemberRequestModel)
                }
            }
            /*  R.id.buttonInvite -> {
                  //backPress()
                  startActivity(Intent(requireContext(), HomeActivity::class.java))
              }*/
            /* R.id.textViewRole -> {
                 manageRoleViewVisibility()
             }*/
        }
    }

    private fun manageRoleViewVisibility() {
        /* if (recyclerViewMemberRole.visibility == View.VISIBLE) {
             recyclerViewMemberRole.toGone()
             textViewRole.setCompoundDrawablesWithIntrinsicBounds(
                 0,
                 0,
                 R.drawable.ic_arrow_drop_down,
                 0
             );
         } else {
             recyclerViewMemberRole.toVisible()
             textViewRole.setCompoundDrawablesWithIntrinsicBounds(
                 0,
                 0,
                 R.drawable.ic_arrow_drop_up,
                 0
             );
         }*/

    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_member
    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        when (parent?.id) {
            R.id.relationship_spinner -> {
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

