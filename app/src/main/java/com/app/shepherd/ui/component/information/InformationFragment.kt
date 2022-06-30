package com.app.shepherd.ui.component.information

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentInformationBinding
import com.app.shepherd.databinding.FragmentSettingBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.utils.Const
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : BaseFragment<FragmentInformationBinding>(), View.OnClickListener {
    private lateinit var fragmentInformationBinding: FragmentInformationBinding
    private val args: InformationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentInformationBinding =
            FragmentInformationBinding.inflate(inflater, container, false)

        return fragmentInformationBinding.root
    }

    override fun observeViewModel() {
    }

    override fun initViewBinding() {
        fragmentInformationBinding.listener = this
        setTitle()
    }

    private fun setTitle() {
        fragmentInformationBinding.apply {
            when (args.source) {
                Const.PRIVACY_POLICY -> {
                    tvTitle.text = getString(R.string.privacy_policy)
                }
                Const.TERM_OF_USE -> {
                    tvTitle.text = getString(R.string.terms_of_use)
                }
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_information

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }

}