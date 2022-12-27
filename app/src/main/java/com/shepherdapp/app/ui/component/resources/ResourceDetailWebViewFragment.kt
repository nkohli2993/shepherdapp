package com.shepherdapp.app.ui.component.resources

import android.annotation.SuppressLint
import android.graphics.Bitmap
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
//        setupWebView()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_resource_detail_web_view
    }

    override fun onResume() {
        super.onResume()
        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        fragmentResourceDetailWebViewBinding.webView.settings.javaScriptEnabled = true
        fragmentResourceDetailWebViewBinding.webView.settings.domStorageEnabled = true
        fragmentResourceDetailWebViewBinding.webView.settings.allowFileAccessFromFileURLs = true
        fragmentResourceDetailWebViewBinding.webView.settings.allowUniversalAccessFromFileURLs =
            true
        fragmentResourceDetailWebViewBinding.webView.settings.builtInZoomControls = true
        fragmentResourceDetailWebViewBinding.webView.webChromeClient = WebChromeClient()
        fragmentResourceDetailWebViewBinding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideLoading()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showLoading("")
            }
        }

        if (args.url.isNotEmpty()) {
            // Load PDF in WebView
            if (args.url.endsWith(".pdf")) {
                fragmentResourceDetailWebViewBinding.webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + args.url)
            } else {
                fragmentResourceDetailWebViewBinding.webView.loadUrl(args.url)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                backPress()
            }
        }
    }

}