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
                        Log.d(TAG, "ProfilePicURL:$profilePicUrl ")
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
        Picasso.get().load(editProfileViewModel.getUserDetail()?.profilePhoto)
            .placeholder(R.drawable.ic_defalut_profile_pic)
            .into(fragmentEditProfileBinding.imageViewUser)
        if(editProfileViewModel.getUserDetail()?.phoneCode!=null){
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
            R.id.imageViewUser, R.id.llImageWrapper ->{
                if (!checkPermission()) {
                    requestPermission(200)
                } else {
                    openImagePicker()
                }
            }
            R.id.btnSaveChange ->{
                if (isValid) {
                    val profilePicCompleteUrl = if (profilePicUrl.isNullOrEmpty()) {
                        null
                    } else {
                        BuildConfig.BASE_URL_USER + profilePicUrl
                    }

                    editProfileViewModel.updateAccount(
                        ccp.selectedCountryCode,
                        profilePicCompleteUrl,
                        edtFirstName.text.toString().trim(),
                        edtLastName.text.toString().trim(),
                        editTextEmail.text.toString().trim(),
                        edtPhoneNumber.text.toString().trim(),
                        roleId = roleId
                    )
                }

            }
        }
    }

    private val isValid: Boolean
        get() {
            when {
                binding.edtFirstName.text.toString().isEmpty() -> {
                    binding.edtFirstName.error = getString(R.string.please_enter_first_name)
                    binding.edtFirstName.requestFocus()
                }
                binding.edtLastName.text.toString().isEmpty() -> {
                    binding.edtLastName.error = getString(R.string.please_enter_last_name)
                    binding.edtLastName.requestFocus()
                }
                binding.etEmailId.text.toString().isEmpty() -> {
                    binding.etEmailId.error = getString(R.string.please_enter_email_id)
                    binding.etEmailId.requestFocus()
                }
                !binding.etEmailId.text.toString().isValidEmail() -> {
                    binding.etEmailId.error = getString(R.string.please_enter_valid_email_id)
                    binding.etEmailId.requestFocus()
                }
                binding.edtPhoneNumber.text.toString().isEmpty() -> {
                    binding.edtPhoneNumber.error = getString(R.string.enter_phone_number)
                    binding.edtPhoneNumber.requestFocus()
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
            imgProfile.loadImageCentreCrop(R.drawable.ic_outline_person, file)
            imgProfile.scaleType = ImageView.ScaleType.FIT_XY
        }
    }
}