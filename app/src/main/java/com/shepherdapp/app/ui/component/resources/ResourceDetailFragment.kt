package com.shepherdapp.app.ui.component.resources

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.resource.AllResourceData
import com.shepherdapp.app.databinding.FragmentResourceDetailBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.utils.TextViewClickMovement
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.toTextFormat
import com.shepherdapp.app.view_model.ResourceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResourceDetailFragment : BaseFragment<FragmentResourceDetailBinding>(), View.OnClickListener,
    TextViewClickMovement.OnTextViewClickMovementListener {

    private val resourcesViewModel: ResourceViewModel by viewModels()
    private lateinit var fragmentResourcesDetailBinding: FragmentResourceDetailBinding
    private val args: ResourceDetailFragmentArgs by navArgs()
    private var resourceId: Int? = null
    private var resourceDetail: AllResourceData? = null
    private val TAG = "ResourceDetailFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentResourcesDetailBinding =
            FragmentResourceDetailBinding.inflate(inflater, container, false)

        return fragmentResourcesDetailBinding.root
    }

    override fun observeViewModel() {
        resourcesViewModel.resourceIdBasedResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    resourceDetail = it.data.payload
                    setDataResource()
                }
            }
        }

    }

    override fun initViewBinding() {
        fragmentResourcesDetailBinding.listener = this
        resourceId = args.source.toInt()
        resourcesViewModel.getResourceDetail(resourceId ?: 0)
    }

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    private fun setDataResource() {
        fragmentResourcesDetailBinding.textViewTitle.text =
            HtmlCompat.fromHtml(resourceDetail!!.title ?: "", 0)
        if (resourceDetail!!.thumbnailUrl != null && resourceDetail!!.thumbnailUrl != "") {
            /* Picasso.get().load(resourceDetail!!.thumbnailUrl)
                 .placeholder(R.drawable.image)
                 .into(fragmentResourcesDetailBinding.imageViewTopic)*/
        }
        /* fragmentResourcesDetailBinding.descriptionTV.text =
             HtmlCompat.fromHtml(resourceDetail!!.content ?: "", 0)*/


        fragmentResourcesDetailBinding.descriptionTV.settings.javaScriptEnabled = true
        fragmentResourcesDetailBinding.descriptionTV.settings.domStorageEnabled = true
        fragmentResourcesDetailBinding.descriptionTV.webChromeClient = WebChromeClient()
        fragmentResourcesDetailBinding.descriptionTV.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // Send the clicked url to ResourceDetailWebViewFragment
                val action =
                    ResourceDetailFragmentDirections.actionNavResourceDetailToResourceDetailWebViewFragment(
                        url = url
                    )
                findNavController().navigate(action)
                return true
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

        // Load HTML data into WebView
        fragmentResourcesDetailBinding.descriptionTV.loadData(
            resourceDetail?.content ?: "",
            "text/html",
            null
        )


        val formattedDate = resourceDetail!!.createdAt!!.toTextFormat(
            resourceDetail!!.createdAt!!,
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "EEE, MMM dd"
        )
        val formattedTime = resourceDetail!!.createdAt!!.toTextFormat(
            resourceDetail!!.createdAt!!,
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "hh:mm a"
        )

        fragmentResourcesDetailBinding.txtCategory.text = resourceDetail?.category?.name
//        fragmentResourcesDetailBinding.tvDate.text = "${getString(R.string.created_on)} $formattedDate"
//        fragmentResourcesDetailBinding.tvTime.text = formattedTime

//        fragmentResourcesDetailBinding.descriptionTV.linksClickable = true
//        fragmentResourcesDetailBinding.descriptionTV.autoLinkMask = Linkify.ALL
        /*  fragmentResourcesDetailBinding.descriptionTV.movementMethod =
              TextViewClickMovement(this, context)
  */
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_resource_detail
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
            }
            R.id.tvShare -> {
                shareUrl()
            }
        }
    }

    override fun onLinkClicked(linkText: String?, linkType: TextViewClickMovement.LinkType?) {
        Log.d(TAG, "onLinkClicked: LinkURL : $linkText ")
        if (!linkText.isNullOrEmpty()) {
            // Navigate to ResourceDetailWebViewFragment with string url clicked
            val action =
                ResourceDetailFragmentDirections.actionNavResourceDetailToResourceDetailWebViewFragment(
                    url = linkText
                )
            findNavController().navigate(action)
        }
    }

    override fun onLongClick(text: String?) {
    }

    private fun shareUrl() {
        val resourceId = resourceDetail?.id
        val shareResourceUrl = BuildConfig.BASE_URL_RESOURCE_SHARING + "resource/detail/" + "$resourceId"
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Resource Document URL")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Resource Document URL : $shareResourceUrl")
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            Log.d(TAG, "shareUrl exception :$e")
        }
    }
}