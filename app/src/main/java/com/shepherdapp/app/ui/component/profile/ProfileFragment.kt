package com.shepherdapp.app.ui.component.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.login.Payload
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.databinding.FragmentProfileBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.profile.adapter.LovedOnesAdapter
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.Status
import com.shepherdapp.app.utils.extensions.getStringWithHyphen
import com.shepherdapp.app.view_model.ProfileViewModel
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

    private var parentActivityListener: ChildFragmentToActivityListener? = null

    private lateinit var homeActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            homeActivity = context
        }
        if (context is ChildFragmentToActivityListener) parentActivityListener = context
        else throw RuntimeException("$context must implement ChildFragmentToActivityListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfileBinding =
            FragmentProfileBinding.inflate(inflater, container, false)

        profileViewModel.getUserDetailByUUID()

        if (profileViewModel.isLoggedInUserLovedOne() == true) {
            profileViewModel.getCareTeamsByLovedOneId(page, limit, status)
        } else {
            profileViewModel.getCareTeamsForLoggedInUser(page, limit, status)
        }

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

        // Check if last name is null
        val fullName = if (lastName.isNullOrEmpty()) {
            "$firstName"
        } else {
            "$firstName $lastName"
        }

//        val fullName = "$firstName $lastName"
        //Set LoggedIn  User Name
        fragmentProfileBinding.tvName.text = fullName

        // Get loggedIn User's Profile Pic
        if (payload?.userProfiles?.profilePhoto != null && payload?.userProfiles?.profilePhoto != "") {
            val profilePicLoggedInUser = payload?.userProfiles?.profilePhoto
            Picasso.get().load(profilePicLoggedInUser)
                .placeholder(R.drawable.ic_defalut_profile_pic)
                .into(fragmentProfileBinding.imageViewUser)
        }


        //Set Email
        fragmentProfileBinding.txtEmail.text = payload?.email

        // Set Phone Number
        // Check if Phone Code and Phone Number are null

        val phoneCode = payload?.userProfiles?.phoneCode
        val phoneNumber = payload?.userProfiles?.phoneNo
        val phone = if (phoneCode.isNullOrEmpty() && phoneNumber.isNullOrEmpty()) {
            "Phone number not available"
        } else {
            val phoneNo = phoneNumber?.getStringWithHyphen(phoneNumber)
            "+$phoneCode $phoneNo"
        }
        fragmentProfileBinding.txtPhone.text = phone

        //Get user's role
        // fragmentProfileBinding.tvProfessional.text = payload?.userLovedOne?.get(0)?.careRoles?.name

        // Set User's Role
        val role = Prefs.with(ShepherdApp.appContext)!!.getString(Const.USER_ROLE, "")
        if (role.isNullOrEmpty()) {
            fragmentProfileBinding.tvProfessional.text = getString(R.string.care_team_leader)
        } else {
            fragmentProfileBinding.tvProfessional.text = role
        }

    }


    override fun observeViewModel() {
        profileViewModel.userDetailByUUIDLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
//                    it.message?.let { showError(requireContext(), it.toString()) }

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
//                    it.message?.let { showError(requireContext(), it.toString()) }

                    fragmentProfileBinding.recyclerLovedOnes.visibility = View.GONE
                    fragmentProfileBinding.tvYourLovedOnes.visibility = View.GONE
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

                    //To fix : Duplicate LovedOne names in LovedOne Listing
                    // If LoggedIn user is loved one, get first object and add to careTeams

                    if (profileViewModel.isLoggedInUserLovedOne() == true) {
                        careTeams.add(it.data.payload.data.first())
                        lovedOnesAdapter?.addData(careTeams)
                    } else {
                        val data = it.data.payload.data
                        // To fix : Duplicate loved One Issue
                        // If love_user_id at index 0 matches with the love_user_id at index, pick first object only
                        if (data.size == 1) {
                            careTeams = data
                            lovedOnesAdapter?.addData(careTeams)
                        } else {
                            if (data[0].love_user_id == data[1].love_user_id) {
                                careTeams.add(it.data.payload.data.first())
                                lovedOnesAdapter?.addData(careTeams)
                            } else {
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

                    /* it.data.payload.let { payload ->
                         careTeams = payload.data
                         total = payload.total!!
                         currentPage = payload.currentPage!!
                         totalPage = payload.totalPages!!
                     }
                     lovedOnesAdapter?.addData(careTeams)*/

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
            profileViewModel.saveLovedOneUUID(it?.uid!!)
            val lovedOneDetail = UserLovedOne(
                it.id,
                it.uid,
                careTeam.love_user_id,
                careTeam.role_id,
                careTeam.permission
            )
            profileViewModel.saveLovedOneUserDetail(lovedOneDetail)

            // Get the care role name and saved into shared preferences
            val roleName = careTeam.careRoles?.name
            roleName?.let { it1 -> profileViewModel.saveUserRole(it1) }

            // Set User's Role
            val role = Prefs.with(ShepherdApp.appContext)!!.getString(Const.USER_ROLE, "")
            if (role.isNullOrEmpty()) {

                fragmentProfileBinding.tvProfessional.text = getString(R.string.care_team_leader)
            } else {
                fragmentProfileBinding.tvProfessional.text = role
            }

        }

        parentActivityListener?.msgFromChildFragmentToActivity()

        // Update Navigation Drawer Info
        homeActivity.setDrawerInfo()

    }


}

