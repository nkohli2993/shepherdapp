package com.shepherd.app.ui.component.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.dto.care_team.CareTeam
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.login.Payload
import com.shepherd.app.data.dto.login.UserLovedOne
import com.shepherd.app.data.dto.login.UserProfile
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
    var lovedOnesAdapter: LovedOnesAdapter? = null

    private lateinit var fragmentProfileBinding: FragmentProfileBinding

    private var payload: Payload? = null
    private var lovedOneArrayList: ArrayList<UserLovedOne>? = null
    private var careTeams: ArrayList<CareTeamModel> = arrayListOf()
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
        profileViewModel.getUserDetailByUUID()
        profileViewModel.getCareTeamsForLoggedInUser(page, limit, status)
        return fragmentProfileBinding.root
    }

    override fun initViewBinding() {
        fragmentProfileBinding.listener = this
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
                    careTeams = it.data.payload.data
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

    private fun setLovedOnesAdapter(careTeams: ArrayList<CareTeamModel>?) {
        lovedOnesAdapter = LovedOnesAdapter(profileViewModel)
        lovedOnesAdapter?.addData(careTeams)
        fragmentProfileBinding.recyclerLovedOnes.adapter = lovedOnesAdapter
        lovedOnesAdapter?.setClickListener(this)
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
        careTeam.love_user_id_details.let { profileViewModel.saveLovedOneUUID(it.uid!!) }
    }


}

