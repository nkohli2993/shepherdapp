package com.shepherd.app.ui.component.edit_profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.BuildConfig
import com.shepherd.app.R
import com.shepherd.app.databinding.FragmentEditProfileBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.BiometricUtils
import com.shepherd.app.utils.UserSlug
import com.shepherd.app.utils.extensions.isValidEmail
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.utils.loadImageCentreCrop
import com.shepherd.app.utils.observe
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_create_new_account.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import java.io.File

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(), View.OnClickListener {

    private var roleId: String? = null
    private val editProfileViewModel: EditProfileViewModel by viewModels()
    private lateinit var fragmentEditProfileBinding: FragmentEditProfileBinding
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var profilePicUrl: String? = null
    private var TAG = "Edit_profile_screen"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentEditProfileBinding =
            FragmentEditProfileBinding.inflate(inflater, container, false)

        return fragmentEditProfileBinding.root
    }

    override fun observeViewModel() {
        observe(selectedFile, ::handleSelectedImage)

        // Observe the response of upload image api
        editProfileViewModel.uploadImageLiveData.observeEvent(this) {
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
                        it1.message?.let { it2 -> showSuccess(requireContext(), it2) }
                        profilePicUrl = it1.payload?.profilePhoto
                        fragmentEditProfileBinding.imageViewUser.loadImageCentreCrop(
                            R.drawable.ic_outline_person,
                            editProfileViewModel.imageFile!!
                        )
                    }
                }
            }
        }

        // Observe the response of sign up api
        editProfileViewModel.updateProfileLiveData.observeEvent(this) {
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
                        it1.message?.let { it2 -> showSuccess(requireContext(), it2) }
                        backPress()
                    }

                }
            }
        }

        // Observe the response of Roles api
        editProfileViewModel.rolesResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    it.message?.let { showError(requireContext(), it.toString()) }
                }
                is DataResult.Loading -> {

                }
                is DataResult.Success -> {
                    roleId = it.data.payload.users.filter { users ->
                        users.slug == UserSlug.User.slug
                    }.map { user ->
                        user.id
                    }[0].toString()
                }
            }
        }

    }


    override fun initViewBinding() {
        fragmentEditProfileBinding.listener = this
        fragmentEditProfileBinding.userDetail = editProfileViewModel.getUserDetail()
        fragmentEditProfileBinding.etEmailId.setText(editProfileViewModel.getUserEmail())
        profilePicUrl = editProfileViewModel.getUserDetail()?.profilePhoto
        Picasso.get().load(editProfileViewModel.getUserDetail()?.profilePhoto)
            .placeholder(R.drawable.ic_defalut_profile_pic)
            .into(fragmentEditProfileBinding.imageViewUser)
        if (editProfileViewModel.getUserDetail()?.phoneCode != null) {
            fragmentEditProfileBinding.ccp.setCountryForPhoneCode(editProfileViewModel.getUserDetail()?.phoneCode!!.toInt())
        }
        // Get Roles
        editProfileViewModel.getRoles(pageNumber, limit)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.imageViewUser, R.id.imgUploadLovedOnePic -> {
                if (!checkPermission()) {
                    requestPermission(200)
                } else {
                    openImagePicker()
                }
            }
            R.id.btnContinue -> {
                if (isValid) {
                    /* val profilePicCompleteUrl = if (profilePicUrl.isNullOrEmpty()) {
                         null
                     } else {
                         when {
                             !profilePicUrl!!.startsWith(BuildConfig.BASE_URL_USER) -> {
                                 BuildConfig.BASE_URL_USER + profilePicUrl
                             }
                             else -> {
                                 profilePicUrl
                             }
                         }
                     }*/
                    editProfileViewModel.updateAccount(
                        ccp.selectedCountryCode,
                        profilePicUrl,
                        fragmentEditProfileBinding.edtFirstName.text.toString().trim(),
                        fragmentEditProfileBinding.edtLastName.text.toString().trim(),
                        fragmentEditProfileBinding.etEmailId.text.toString().trim(),
                        fragmentEditProfileBinding.edtPhoneNumber.text.toString().trim(),
                        roleId = roleId
                    )
                }

            }
        }
    }

    private val isValid: Boolean
        get() {
            when {
                fragmentEditProfileBinding.edtFirstName.text.toString().isEmpty() -> {
                    fragmentEditProfileBinding.edtFirstName.error =
                        getString(R.string.please_enter_first_name)
                    fragmentEditProfileBinding.edtFirstName.requestFocus()
                }
                fragmentEditProfileBinding.edtLastName.text.toString().isEmpty() -> {
                    fragmentEditProfileBinding.edtLastName.error =
                        getString(R.string.please_enter_last_name)
                    fragmentEditProfileBinding.edtLastName.requestFocus()
                }
                fragmentEditProfileBinding.etEmailId.text.toString().isEmpty() -> {
                    fragmentEditProfileBinding.etEmailId.error =
                        getString(R.string.please_enter_email_id)
                    fragmentEditProfileBinding.etEmailId.requestFocus()
                }
                !fragmentEditProfileBinding.etEmailId.text.toString().isValidEmail() -> {
                    fragmentEditProfileBinding.etEmailId.error =
                        getString(R.string.please_enter_valid_email_id)
                    fragmentEditProfileBinding.etEmailId.requestFocus()
                }
                fragmentEditProfileBinding.edtPhoneNumber.text.toString().isEmpty() -> {
                    fragmentEditProfileBinding.edtPhoneNumber.error =
                        getString(R.string.enter_phone_number)
                    fragmentEditProfileBinding.edtPhoneNumber.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }


    private fun handleSelectedImage(file: File?) {
        if (file != null && file.exists()) {
            //imgUploadProfilePic.visibility = View.GONE

            editProfileViewModel.imageFile = file
            editProfileViewModel.uploadImage(file)

        }
    }
}