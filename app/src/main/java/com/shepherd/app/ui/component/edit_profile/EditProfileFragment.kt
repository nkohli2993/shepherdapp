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
import dagger.hilt.android.AndroidEntryPoint

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
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack->{
                findNavController().popBackStack()
            }
        }
    }

}