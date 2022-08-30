package com.shepherd.app.ui.component.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shepherd.app.R
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.login.Payload
import com.shepherd.app.data.dto.login.UserLovedOne
import com.shepherd.app.databinding.FragmentProfileBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.profile.adapter.LovedOnesAdapter
import com.shepherd.app.utils.Status
import com.shepherd.app.utils.extensions.getStringWithHyphen
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.view_model.ProfileViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(), View.OnClickListener,
    LovedOnesAdapter.OnItemClickListener {

    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var fragmentProfileBinding: FragmentProfileBinding
    var lovedOnesAdapter: LovedOnesAdapter? = null
    private var payload: Payload? = null
    private var careTeams: ArrayList<CareTeamModel> = arrayListOf()
    private var page = 1
    private var limit = 10
    private var status = Status.One.status
    var currentPage: Int = 0
    var totalPage: Int = 0
    var total: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfileBinding =
            FragmentProfileBinding.inflate(inflater, container, false)
        profileViewModel.getUserDetailByUUID()
        profileViewModel.getCareTeamsForLoggedInUser(page, limit, status)
        return fragmentProfileBinding.root
    }

    override fun initViewBinding() {
        fragmentProfileBinding.listener = this
        setLovedOnesAdapter()
    }

    private fun handleAddedLovedOnePagination() {
        var isScrolling: Boolean
        var visibleItemCount: Int
        var totalItemCount: Int
        var pastVisiblesItems: Int
        fragmentProfileBinding.nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1)
                        .measuredHeight - v.measuredHeight &&
                    scrollY > oldScrollY
                ) {
                    isScrolling = true
                    visibleItemCount =
                        fragmentProfileBinding.recyclerLovedOnes.layoutManager!!.childCount
                    totalItemCount =
                        fragmentProfileBinding.recyclerLovedOnes.layoutManager!!.itemCount
                    pastVisiblesItems =
                        (fragmentProfileBinding.recyclerLovedOnes.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (isScrolling && visibleItemCount + pastVisiblesItems >= totalItemCount && (currentPage < totalPage)) {
                        isScrolling = false
                        currentPage++
                        page++
                        profileViewModel.getCareTeamsForLoggedInUser(page, limit, status)
                    }
                }
            }
        }
    }

    private fun initView() {
        val firstName = payload?.userProfiles?.firstname
        val lastName = payload?.userProfiles?.lastname
        val fullName = "$firstName $lastName"
        //Set LoggedIn  User Name
        fragmentProfileBinding.tvName.text = fullName

        // Get loggedIn User's Profile Pic
        val profilePicLoggedInUser = payload?.userProfiles?.profilePhoto
        Picasso.get().load(profilePicLoggedInUser).placeholder(R.drawable.ic_defalut_profile_pic)
            .into(fragmentProfileBinding.imageViewUser)

        //Set Email
        fragmentProfileBinding.txtEmail.text = payload?.email

        // Set Phone Number
        val phoneCode = payload?.userProfiles?.phoneCode
        val phoneNumber = payload?.userProfiles?.phoneNo
        val phoneNo = phoneNumber?.getStringWithHyphen(phoneNumber)

        val phone = "+$phoneCode $phoneNo"
        fragmentProfileBinding.txtPhone.text = phone

        //Get user's role
        fragmentProfileBinding.tvProfessional.text = payload?.userLovedOne?.get(0)?.careRoles?.name

    }


    override fun observeViewModel() {
        profileViewModel.userDetailByUUIDLiveData.observeEvent(this) {
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
                    payload = it.data.payload
                    initView()
                }
            }
        }

        profileViewModel.careTeamsResponseLiveData.observeEvent(this) {
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
                    if (page == 1) {
                        lovedOnesAdapter = null
                        setLovedOnesAdapter()
                    }
                    it.data.payload.let { payload ->
                        careTeams = payload.data
                        total = payload.total!!
                        currentPage = payload.currentPage!!
                        totalPage = payload.totalPages!!
                    }
                    lovedOnesAdapter?.addData(careTeams)

                }
            }
        }
    }

    private fun setLovedOnesAdapter() {
        lovedOnesAdapter = LovedOnesAdapter(profileViewModel)
        fragmentProfileBinding.recyclerLovedOnes.adapter = lovedOnesAdapter
        lovedOnesAdapter?.setClickListener(this)
        handleAddedLovedOnePagination()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.clProfileWrapper -> {
                // p0.findNavController().navigate(R.id.action_nav_profile_to_editProfile)
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_profile
    }

    override fun onItemClick(careTeam: CareTeamModel) {
        // Save the selected lovedOne UUID in shared prefs
        careTeam.love_user_id_details.let {
            profileViewModel.saveLovedOneUUID(it.uid!!)
            val lovedOneDetail = UserLovedOne(
                it.id,
                it.uid,
                careTeam.love_user_id,
                careTeam.role_id,
                careTeam.permission
            )
            profileViewModel.saveLovedOneUserDetail(lovedOneDetail)
        }
    }


}

