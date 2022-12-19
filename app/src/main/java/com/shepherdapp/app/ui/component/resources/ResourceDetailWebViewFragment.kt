package com.shepherdapp.app.ui.component.resources

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.FragmentResourceDetailWebViewBinding
import com.shepherdapp.app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResourceDetailWebViewFragment : BaseFragment<FragmentResourceDetailWebViewBinding>(),
    View.OnClickListener {

    private lateinit var fragmentResourceDetailWebViewBinding: FragmentResourceDetailWebViewBinding
    private val args: ResourceDetailWebViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentResourceDetailWebViewBinding =
            FragmentResourceDetailWebViewBinding.inflate(inflater, container, false)

        return fragmentResourceDetailWebViewBinding.root
    }

    override fun observeViewModel() {
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViewBinding() {
        showLoading("")
        fragmentResourceDetailWebViewBinding.listener = this
        setupWebView()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_resource_detail_web_view
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        fragmentResourceDetailWebViewBinding.webView.settings.javaScriptEnabled = true
        fragmentResourceDetailWebViewBinding.webView.settings.domStorageEnabled = true
        fragmentResourceDetailWebViewBinding.webView.webChromeClient = WebChromeClient()
        fragmentResourceDetailWebViewBinding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                view.loadUrl(url)
                return false
            }
        }

        if (args.url.isNotEmpty()) {
            fragmentResourceDetailWebViewBinding.webView.loadUrl(args.url)
        }
        hideLoading()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                backPress()
            }
        }
    }

}