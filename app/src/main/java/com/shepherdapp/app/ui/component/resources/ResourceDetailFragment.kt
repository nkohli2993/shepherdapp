package com.shepherdapp.app.ui.component.resources

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.resource.AllResourceData
import com.shepherdapp.app.databinding.FragmentResourceDetailBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.toTextFormat
import com.shepherdapp.app.view_model.ResourceViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResourceDetailFragment : BaseFragment<FragmentResourceDetailBinding>(), View.OnClickListener {

    private val resourcesViewModel: ResourceViewModel by viewModels()
    private lateinit var fragmentResourcesDetailBinding: FragmentResourceDetailBinding
    private val args: ResourceDetailFragmentArgs by navArgs()
    private var resourceId: Int? = null
    private var resourceDetail: AllResourceData? = null
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

    @SuppressLint("SetTextI18n")
    private fun setDataResource() {
        fragmentResourcesDetailBinding.textViewTitle.text =   HtmlCompat.fromHtml(resourceDetail!!.title ?: "", 0)
        if (resourceDetail!!.thumbnailUrl != null && resourceDetail!!.thumbnailUrl != "") {
           /* Picasso.get().load(resourceDetail!!.thumbnailUrl)
                .placeholder(R.drawable.image)
                .into(fragmentResourcesDetailBinding.imageViewTopic)*/
        }
        fragmentResourcesDetailBinding.descriptionTV.text =    HtmlCompat.fromHtml(resourceDetail!!.content ?: "", 0)
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
//        fragmentResourcesDetailBinding.tvDate.text = "${getString(R.string.created_on)} $formattedDate"
//        fragmentResourcesDetailBinding.tvTime.text = formattedTime

    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_resource_detail
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
            }

        }

    }

}