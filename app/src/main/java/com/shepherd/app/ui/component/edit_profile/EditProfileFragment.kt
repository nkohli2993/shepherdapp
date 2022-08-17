package com.shepherd.app.ui.component.edit_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.databinding.FragmentEditProfileBinding
import com.shepherd.app.ui.base.BaseFragment
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.app_bar_dashboard.*

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(), View.OnClickListener {

    private val editProfileViewModel: EditProfileViewModel by viewModels()

    private lateinit var fragmentEditProfileBinding: FragmentEditProfileBinding
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
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }

}