package com.shepherdapp.app.ui.component.memberDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.care_team.UpdateCareTeamMemberRequestModel
import com.shepherdapp.app.databinding.FragmentEditMemberDetailBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.MemberDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMemberDetailFragment : BaseFragment<FragmentEditMemberDetailBinding>(),
    View.OnClickListener {
    private val memberDetailsViewModel: MemberDetailsViewModel by viewModels()
    private lateinit var fragmentEditMemberDetailBinding: FragmentEditMemberDetailBinding
    private val args: EditMemberDetailFragmentArgs by navArgs()
    private var careTeam: CareTeamModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentEditMemberDetailBinding =
            FragmentEditMemberDetailBinding.inflate(inflater, container, false)
        return fragmentEditMemberDetailBinding.root

    }

    override fun initViewBinding() {
        fragmentEditMemberDetailBinding.listener = this
        careTeam = args.careTeam
        initView()
    }

    private fun initView() {
        careTeam?.let { careMemberDetail ->
            fragmentEditMemberDetailBinding.edtRole.text = careMemberDetail.careRoles?.name
            fragmentEditMemberDetailBinding.edtRelationShip.setText(careMemberDetail.relation_name)
            careTeam?.user_id_details.let { memberUserDetail ->
                fragmentEditMemberDetailBinding.edtEmail.text = memberUserDetail!!.email ?: ""
                fragmentEditMemberDetailBinding.edtRelationShip.setText(
                    memberUserDetail.relation ?: ""
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fragmentEditMemberDetailBinding.edtRelationShip.setText(careTeam?.relation_name ?: "N/A")
    }

    override fun observeViewModel() {
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

    override fun getLayoutRes(): Int {
        return R.layout.fragment_edit_member_detail
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.btnSubmit -> {
                if (fragmentEditMemberDetailBinding.edtRelationShip.text.toString().isEmpty()) {
                    showError(requireContext(), getString(R.string.please_enter_relation_with_user))
                } else {
                    careTeam?.id?.let {
                        memberDetailsViewModel.updateCareTeamMember(
                            it,
                            UpdateCareTeamMemberRequestModel(
                                careTeam?.permission,
                                fragmentEditMemberDetailBinding.edtRelationShip.text.toString()
                            )
                        )
                    }
                }
            }
        }
    }

}