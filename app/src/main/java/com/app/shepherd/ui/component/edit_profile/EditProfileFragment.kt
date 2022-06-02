package com.app.shepherd.ui.component.edit_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentChangePasswordBinding
import com.app.shepherd.databinding.FragmentEditProfileBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.change_password.ChangePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {

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
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_edit_profile
    }

}