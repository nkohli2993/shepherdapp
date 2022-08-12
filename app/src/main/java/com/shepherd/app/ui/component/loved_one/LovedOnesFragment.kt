package com.shepherd.app.ui.component.loved_one

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.databinding.FragmentLovedOnesBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.addLovedOneCondition.AddLovedOneConditionActivity
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Prefs
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
    private var selectedCare: CareTeamModel? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLovedOnesBinding =
            FragmentLovedOnesBinding.inflate(inflater, container, false)



        return fragmentLovedOnesBinding.root
    }

    override fun onResume() {
        super.onResume()
        // Get care Teams for loggedIn User
        lovedOneViewModel.getCareTeamsForLoggedInUser(page, limit, status)
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
                    val lovedOneIDInPrefs =
                        Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")
                    for (i in careTeams) {
                        if (lovedOneIDInPrefs.equals(i.love_user_id_details.uid)) {
                            selectedCare = i
                            break
                        }
                    }
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
            R.id.btnDone -> {
                selectedCare?.let {
                    it.love_user_id_details.let { lovedOneViewModel.saveLovedOneUUID(it.uid!!) }
                }

                backPress()
            }
            R.id.ivBack -> {
                backPress()
            }
        }
    }

    override fun onItemClick(careTeam: CareTeamModel, type: String) {
        when (type) {
            "Medical" -> {
                val intent = Intent(requireContext(), AddLovedOneConditionActivity::class.java)
                intent.putExtra("source", Const.MEDICAL_CONDITION)
                intent.putExtra("love_one_id", careTeam.love_user_id)
                intent.putExtra("care_conditions", careTeam.careConditions)
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            else -> {
                selectedCare = careTeam
            }
        }
    }
}