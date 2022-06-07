package com.app.shepherd.ui.component.vital_stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentSecurityCodeBinding
import com.app.shepherd.databinding.FragmentVitalStatsBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.security_code.SecurityCodeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VitalStatsFragment : BaseFragment<FragmentVitalStatsBinding>() {
    private val vitalStatsViewModel: VitalStatsViewModel by viewModels()
    private lateinit var fragmentVitalStatsBinding: FragmentVitalStatsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentVitalStatsBinding =
            FragmentVitalStatsBinding.inflate(inflater, container, false)

        return fragmentVitalStatsBinding.root
    }

    override fun observeViewModel() {
    }

    override fun initViewBinding() {
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_vital_stats

    }
}