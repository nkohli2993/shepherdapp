package com.app.shepherd.ui.component.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.dto.login.UserLovedOne
import com.app.shepherd.data.dto.user.Payload
import com.app.shepherd.data.dto.user.UserProfiles
import com.app.shepherd.databinding.FragmentProfileBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.profile.adapter.LovedOnesAdapter
import com.app.shepherd.utils.BiometricUtils
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Const.BIOMETRIC_ENABLE
import com.app.shepherd.utils.Prefs
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.view_model.ProfileViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(), View.OnClickListener {

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var fragmentProfileBinding: FragmentProfileBinding

    private var payload: Payload? = null
    private var lovedOneArrayList: ArrayList<UserLovedOne>? = null
    private var lovedOneProfileList: ArrayList<UserProfiles>? = arrayListOf()

    // private var lovedOneUserIDs: ArrayList<Int?>? = null
    private val TAG: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfileBinding =
            FragmentProfileBinding.inflate(inflater, container, false)
        // Get loggedIn User's Profile info by hitting api
        profileViewModel.getUserDetails()

        return fragmentProfileBinding.root
    }

    override fun initViewBinding() {
        fragmentProfileBinding.listener = this
        //initView()
        //setLovedOnesAdapter()
        //setPendingInvitationsAdapter()
        if (BiometricUtils.isSdkVersionSupported && BiometricUtils.isHardwareSupported(
                requireContext()
            ) && BiometricUtils.isFingerprintAvailable(
                requireContext()
            )
        ) {
            fragmentProfileBinding.scBioMetric.apply {
                isChecked = Prefs.with(requireContext())!!.getBoolean(BIOMETRIC_ENABLE)
                setOnCheckedChangeListener { buttonView, isChecked ->
                    registerBiometric(isChecked)

                }
            }
        } else {
            fragmentProfileBinding.clBioMetricLogin.isVisible = false
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
        Picasso.get().load(profilePicLoggedInUser).placeholder(R.drawable.test_image)
            .into(fragmentProfileBinding.imageViewUser)

        //Set Email
        fragmentProfileBinding.txtEmail.text = payload?.email

        // Set LoggedIn user's email
        val phoneCode = payload?.phoneCode
        val phoneNumber = payload?.phoneNo

        val phone = "+$phoneCode $phoneNumber"
        fragmentProfileBinding.txtPhone.text = phone

    }

    private fun registerBiometric(checked: Boolean) {
        profileViewModel.registerBioMetric(
            checked
        )
    }

    override fun observeViewModel() {
        profileViewModel.bioMetricLiveData.observeEvent(this) {
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
                    it.data.let { it1 ->
                        // Save Token to SharedPref
                        it1.payload?.let { payload ->
                            Prefs.with(requireContext())!!
                                .save(Const.BIOMETRIC_ENABLE, payload.isBiometric!!)
                        }
                    }
                }
            }
        }


        profileViewModel.userDetailsLiveData.observeEvent(this) {
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
                    getLovedOneInfo()
                }
            }
        }


        // Observe Loved One Detail
        profileViewModel.lovedOneDetailsLiveData.observeEvent(this) {
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
                    val lovedOneProfile = it.data.payload?.userProfiles
                    if (lovedOneProfile != null) {
                        lovedOneProfileList?.add(lovedOneProfile)
                    }
                    setLovedOnesAdapter(lovedOneProfileList)
                }
            }
        }

    }

    private fun getLovedOneInfo() {
        lovedOneArrayList = payload?.userLovedOne
        if (!lovedOneArrayList.isNullOrEmpty()) {
            val lovedOneUserIDs = lovedOneArrayList?.map {
                it.loveUserId
            } as ArrayList<Int?>?
            Log.d(TAG, "Loved One User Ids :$lovedOneUserIDs ")

            for (i in lovedOneUserIDs?.indices!!) {
                lovedOneUserIDs[i]?.let { profileViewModel.getLovedOneDetails(it) }
            }
        }


    }

    private fun setLovedOnesAdapter(lovedOneProfileList: ArrayList<UserProfiles>?) {
        val lovedOnesAdapter = LovedOnesAdapter(profileViewModel)
        lovedOnesAdapter.addData(lovedOneProfileList)
        fragmentProfileBinding.recyclerLovedOnes.adapter = lovedOnesAdapter

    }

    private fun setPendingInvitationsAdapter() {
//        val pendingInvitationsAdapter = PendingInvitationsAdapter(profileViewModel)
//        fragmentProfileBinding.recyclerPendingInvitations.adapter = pendingInvitationsAdapter

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvChange -> {
                p0.findNavController().navigate(R.id.action_nav_profile_to_changePassword)
            }
            R.id.tvReset -> {
                p0.findNavController().navigate(R.id.action_nav_profile_to_secureCode)
            }
            R.id.clProfileWrapper -> {
                p0.findNavController().navigate(R.id.action_nav_profile_to_editProfile)
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_profile
    }


}

