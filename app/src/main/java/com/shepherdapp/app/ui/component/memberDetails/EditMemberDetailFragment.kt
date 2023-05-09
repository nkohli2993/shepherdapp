package com.shepherdapp.app.ui.component.memberDetails

import android.os.Bundle
import android.text.Spannable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.FragmentEditMemberDetailBinding
import com.shepherdapp.app.databinding.FragmentMemberDetailsBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.extensions.getStringWithHyphen
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.utils.extensions.stripUnderlines
import com.shepherdapp.app.utils.setImageFromUrl
import com.shepherdapp.app.view_model.MemberDetailsViewModel

class EditMemberDetailFragment : BaseFragment<FragmentEditMemberDetailBinding>(),
    View.OnClickListener {
    private val memberDetailsViewModel: MemberDetailsViewModel by viewModels()
    private lateinit var fragmentEditMemberDetailBinding: FragmentEditMemberDetailBinding
    private val args: MemberDetailsFragmentArgs by navArgs()
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
        careTeam?.let {
            fragmentEditMemberDetailBinding.edtRole.setText(it.careRoles?.name)
            careTeam?.user_id_details.let {
                fragmentEditMemberDetailBinding.edtEmail.setText(it!!.email ?: "")
                fragmentEditMemberDetailBinding.edtRelationShip.setText(it!!.relation ?: "")
            }
        }
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

    }

}