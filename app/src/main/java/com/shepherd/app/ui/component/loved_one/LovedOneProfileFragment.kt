package com.shepherd.app.ui.component.loved_one

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.databinding.FragmentLovedOneProfileBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.loved_one.adapter.LovedOneMedicalConditionAdapter
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

    override fun observeViewModel() {

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
                    lovedOneMedicalConditionAdapter?.addData(payload)
                }
            }
        }
    }

    override fun initViewBinding() {
        careTeamModel = args.careTeamModel

        fragmentLovedOneProfileBinding.listener = this
        fragmentLovedOneProfileBinding.data = careTeamModel

        setLovedOneMedicalConditionsAdapter()

        careTeamModel?.love_user_id?.let {
            lovedOneMedicalConditionViewModel.getLovedOneMedicalConditions(
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
        }
    }

    private fun setLovedOneMedicalConditionsAdapter() {
        lovedOneMedicalConditionAdapter =
            LovedOneMedicalConditionAdapter(lovedOneMedicalConditionViewModel)
        fragmentLovedOneProfileBinding.rvLovedOneMedicalConditions.adapter =
            lovedOneMedicalConditionAdapter

    }
}