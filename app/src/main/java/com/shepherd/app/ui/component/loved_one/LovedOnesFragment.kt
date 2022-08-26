package com.shepherd.app.ui.component.loved_one

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    var currentPage: Int = 0
    var totalPage: Int = 0
    var total: Int = 0

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
        careTeams.clear()
        page = 1
        lovedOneViewModel.getCareTeamsForLoggedInUser(page, limit, status)
    }

    private fun handleAddedLovedOnePagination() {
        var isScrolling = true
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisiblesItems: Int
        fragmentLovedOnesBinding.recyclerViewMembers.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    isScrolling = true
                    visibleItemCount = recyclerView.layoutManager!!.childCount
                    totalItemCount = recyclerView.layoutManager!!.itemCount
                    pastVisiblesItems =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (isScrolling && visibleItemCount + pastVisiblesItems >= totalItemCount && (currentPage < totalPage)) {
                        isScrolling = false
                        currentPage++
                        page++
                        lovedOneViewModel.getCareTeamsForLoggedInUser(page, limit, status)
                    }
                }
            }
        })
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
                    careTeams.clear()
                    if(page == 1){
                        lovedOneAdapter = null
                        setLovedOnesAdapter(careTeams)
                    }
                    it.data.payload.let { payload ->
                        careTeams = payload.data
                        total = payload.total!!
                        currentPage = payload.currentPage!!
                        totalPage = payload.totalPages!!
                    }
                    lovedOneAdapter?.addData(careTeams)
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
        setLovedOnesAdapter(careTeams)
    }

    private fun setLovedOnesAdapter(careTeams: ArrayList<CareTeamModel>?) {
        lovedOneAdapter = LovedOneAdapter(lovedOneViewModel)

        recyclerViewMembers.adapter = lovedOneAdapter
        lovedOneAdapter?.setClickListener(this)
        handleAddedLovedOnePagination()
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