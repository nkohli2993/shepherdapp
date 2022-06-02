package com.app.shepherd.ui.component.security_code

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentSecurityCodeBinding
import com.app.shepherd.ui.base.BaseFragment
//import com.example.numpad.NumPadClick
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SecurityCodeFragment : BaseFragment<FragmentSecurityCodeBinding>() {
    private val securityCodeViewModel: SecurityCodeViewModel by viewModels()

    private lateinit var fragmentSecurityCodeBinding: FragmentSecurityCodeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSecurityCodeBinding =
            FragmentSecurityCodeBinding.inflate(inflater, container, false)

        return fragmentSecurityCodeBinding.root
    }

    override fun observeViewModel() {
    }

    override fun initViewBinding() {
//        fragmentSecurityCodeBinding.numpad.setOnNumPadClickListener(NumPadClick { nums ->
//            Log.e("TAG", "initViewBinding: $nums")
////            Log.d(
////                "MYTAG",
////                "onNumpadClicked: $nums"
////            )
//        })
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_security_code
    }
}