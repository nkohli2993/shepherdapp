package com.app.shepherd.ui.component.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.data.dto.login.Payload
import com.app.shepherd.data.dto.login.UserLovedOne
import com.app.shepherd.data.dto.login.UserProfile
import com.app.shepherd.databinding.FragmentProfileBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.profile.adapter.LovedOnesAdapter
import com.app.shepherd.utils.Status
import com.app.shepherd.utils.extensions.getStringWithHyphen
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.view_model.ProfileViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(), View.OnClickListener,
    LovedOnesAdapter.OnItemClickListener {

    private val profileViewModel: ProfileViewModel by viewModels()
    var lovedOnesAdapter: LovedOnesAdapter? = null

    private lateinit var fragmentProfileBinding: FragmentProfileBinding

    private var payload: Payload? = null
    private var lovedOneArrayList: ArrayList<UserLovedOne>? = null
    private var lovedOneProfileList: ArrayList<UserProfile>? = arrayListOf()
    private var careTeams: ArrayList<CareTeam> = arrayListOf()
    private var selectedCareTeams: ArrayList<CareTeam> = arrayListOf()

    // private var lovedOneUserIDs: ArrayList<Int?>? = null
    private val TAG: String? = null
    private var page = 1
    private var limit = 10
    private var status = Status.One.status


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfileBinding =
            FragmentProfileBinding.inflate(inflater, container, false)
        // Get loggedIn User's Profile info by hitting api
        //profileViewModel.getUserDetails()
        profileViewModel.getUserDetailByUUID()

        // Get care Teams for loggedIn User
        profileViewModel.getCareTeamsForLoggedInUser(page, limit, status)

        return fragmentProfileBinding.root
    }

    override fun initViewBinding() {
        fragmentProfileBinding.listener = this
        //initView()
        //setLovedOnesAdapter()
        //setPendingInvitationsAdapter()

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
//                    getLovedOneInfo()
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
                    careTeams = it.data.payload.careTeams
                    setLovedOnesAdapter(careTeams)

                }
            }
        }
    }

    private fun getLovedOneInfo() {
        lovedOneArrayList = payload?.userLovedOne
        if (!lovedOneArrayList.isNullOrEmpty()) {
            val lovedOneUserIDs = lovedOneArrayList?.map {
                it.loveUserId
            } as ArrayList<String?>?
            Log.d(TAG, "Loved One User Ids :$lovedOneUserIDs ")

            for (i in lovedOneUserIDs?.indices!!) {
                lovedOneUserIDs[i]?.let { profileViewModel.getLovedOneDetails(it) }
            }
        }
    }

    private fun setLovedOnesAdapter(careTeams: ArrayList<CareTeam>?) {
        lovedOnesAdapter = LovedOnesAdapter(profileViewModel)
        lovedOnesAdapter?.addData(careTeams)
        fragmentProfileBinding.recyclerLovedOnes.adapter = lovedOnesAdapter
        lovedOnesAdapter?.setClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.clProfileWrapper -> {
                p0.findNavController().navigate(R.id.action_nav_profile_to_editProfile)
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_profile
    }

    override fun onItemClick(careTeam: CareTeam) {
        // Save the selected lovedOne UUID in shared prefs
        careTeam.loveUserId?.let { profileViewModel.saveLovedOneUUID(it) }
    }


}

