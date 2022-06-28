package com.app.shepherd.ui.component.addMember

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.care_team.CareRoles
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.FragmentAddMemberBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.addMember.adapter.AddMemberRoleAdapter
import com.app.shepherd.ui.component.addMember.adapter.RestrictionsModuleAdapter
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.setupSnackbar
import com.app.shepherd.utils.showToast
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
                    //addMemberRoleAdapter?.updateCareTeams(careTeams!!)

                    // binding.layoutCareTeam.visibility = View.VISIBLE
                    // binding.txtNoCareTeamFound.visibility = View.GONE
                }

                is DataResult.Failure -> {
                    //handleAPIFailure(it.message, it.errorCode)

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
            R.id.imageViewBack -> {
                backPress()
            }
            /*  R.id.buttonInvite -> {
                  //backPress()
                  startActivity(Intent(requireContext(), HomeActivity::class.java))
              }*/
//            R.id.textViewRole -> {
//                manageRoleViewVisibility()
//            }
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

