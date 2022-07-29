package com.shepherd.app.ui.component.loved_one

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.dto.care_team.CareTeam
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.databinding.FragmentLovedOnesBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Status
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.view_model.LovedOneViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_join_care_team.*

@AndroidEntryPoint
class LovedOnesFragment : BaseFragment<FragmentLovedOnesBinding>(), View.OnClickListener,
    LovedOneAdapter.OnItemClickListener {

    private lateinit var fragmentLovedOnesBinding: FragmentLovedOnesBinding
    private var lovedOneAdapter: LovedOneAdapter? = null
    private val lovedOneViewModel: LovedOneViewModel by viewModels()
    private var page = 1
    private var limit = 10
    private var status = Status.One.status
    private var careTeams: ArrayList<CareTeamModel> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLovedOnesBinding =
            FragmentLovedOnesBinding.inflate(inflater, container, false)

        // Get care Teams for loggedIn User
        lovedOneViewModel.getCareTeamsForLoggedInUser(page, limit, status)

        return fragmentLovedOnesBinding.root
    }

    override fun observeViewModel() {
        lovedOneViewModel.careTeamsResponseLiveData.observeEvent(this) {
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
                    careTeams = it.data.payload.data
                    setLovedOnesAdapter(careTeams)

                }
            }
        }
    }

    override fun initViewBinding() {
        fragmentLovedOnesBinding.listener = this
        //setLoveOneAdapter()
    }

    private fun setLovedOnesAdapter(careTeams: ArrayList<CareTeamModel>?) {
        lovedOneAdapter = LovedOneAdapter(lovedOneViewModel)
        lovedOneAdapter?.addData(careTeams)
        recyclerViewMembers.adapter = lovedOneAdapter
        lovedOneAdapter?.setClickListener(this)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_loved_ones
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvNew -> {
                findNavController().navigate(
                    LovedOnesFragmentDirections.actionNavLovedOneToNavAddLovedOne(
                        source = Const.ADD_LOVE_ONE
                    )
                )
            }

            R.id.ivBack -> {
                backPress()
            }
        }
    }

    override fun onItemClick(careTeam: CareTeamModel) {
        // Save the selected lovedOne UUID in shared prefs
        careTeam.love_user_id_details.let { lovedOneViewModel.saveLovedOneUUID(it.uid!!) }
    }
}