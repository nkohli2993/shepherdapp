package com.app.shepherd.ui.component.add_vitals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentAddVitalsBinding
import com.app.shepherd.databinding.FragmentSettingBinding
import com.app.shepherd.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVitalsFragment : BaseFragment<FragmentAddVitalsBinding>(), View.OnClickListener {

    private lateinit var fragmentAddVitalsBinding: FragmentAddVitalsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddVitalsBinding =
            FragmentAddVitalsBinding.inflate(inflater, container, false)

        return fragmentAddVitalsBinding.root
    }

    override fun observeViewModel() {
      }

    override fun initViewBinding() {
        fragmentAddVitalsBinding.listener = this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_vitals
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }

}