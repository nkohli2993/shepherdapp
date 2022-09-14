package com.shepherdapp.app.ui.component.createAccount

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.ActivityInformationBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.StaticPagesViewModel

class InformationActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityInformationBinding
    private val staticPagesViewModel: StaticPagesViewModel by viewModels()
    private var type: String = ""
    private var pageNumber = 1
    private var limit = 10
    override fun observeViewModel() {
        staticPagesViewModel.getStaticPagesLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(this, result.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    when (type) {
                        Const.PRIVACY_POLICY -> {
                            binding.tvDescription.text =
                                HtmlCompat.fromHtml(result.data.payload?.privacyPolicy ?: "", 0)
                        }
                        Const.TERM_OF_USE -> {
                            binding.tvDescription.text =
                                HtmlCompat.fromHtml(result.data.payload?.termsAndConditons ?: "", 0)
                        }
                    }
                }
            }
        }

    }

    override fun initViewBinding() {
        binding = ActivityInformationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        type = intent.getStringExtra("type")!!
        staticPagesViewModel.getStaticPagesApi(pageNumber, limit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this
    }

    private fun setTitle() {
        binding.apply {
            when (type) {
                Const.PRIVACY_POLICY -> {
                    tvTitle.text = getString(R.string.privacy_policy)
                }
                Const.TERM_OF_USE -> {
                    tvTitle.text = getString(R.string.terms_of_use)
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}