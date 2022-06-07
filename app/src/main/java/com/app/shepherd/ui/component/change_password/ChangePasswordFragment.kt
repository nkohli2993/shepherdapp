package com.app.shepherd.ui.component.change_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentChangePasswordBinding
import com.app.shepherd.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>(), View.OnClickListener {
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    private lateinit var fragmentChangePasswordBinding: FragmentChangePasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentChangePasswordBinding =
            FragmentChangePasswordBinding.inflate(inflater, container, false)

        return fragmentChangePasswordBinding.root
    }

    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        fragmentChangePasswordBinding.listener = this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_change_password
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }
}