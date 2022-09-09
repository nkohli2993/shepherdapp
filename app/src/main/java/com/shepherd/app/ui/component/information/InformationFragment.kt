package com.shepherd.app.ui.component.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.databinding.FragmentInformationBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.view_model.StaticPagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : BaseFragment<FragmentInformationBinding>(), View.OnClickListener {
    private lateinit var fragmentInformationBinding: FragmentInformationBinding
    private val args: InformationFragmentArgs by navArgs()
    private val staticPagesViewModel: StaticPagesViewModel by viewModels()
    private var pageNumber = 1
    private var limit = 10
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
        staticPagesViewModel.getStaticPagesLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), result.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    when (args.source) {
                        Const.PRIVACY_POLICY -> {
                            fragmentInformationBinding.tvDescription.text =
                                HtmlCompat.fromHtml(result.data.payload?.privacyPolicy ?: "", 0)
                        }
                        Const.TERM_OF_USE -> {
                            fragmentInformationBinding.tvDescription.text =
                                HtmlCompat.fromHtml(result.data.payload?.termsAndConditons ?: "", 0)
                        }
                    }
                }
            }
        }

    }

    override fun initViewBinding() {
        fragmentInformationBinding.listener = this
        staticPagesViewModel.getStaticPagesApi(pageNumber, limit)
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