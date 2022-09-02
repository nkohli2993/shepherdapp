package com.shepherd.app.ui.component.resources

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.data.dto.resource.AllResourceData
import com.shepherd.app.databinding.FragmentResourceDetailBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.toTextFormat
import com.shepherd.app.view_model.ResourceViewModel
import com.squareup.picasso.Picasso


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
                    //  showLoading("")
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
        if (args.source != null) {
            resourceId = args.source!!.toInt()
        }
/*
        if (args.detail != null) {
            resourceDetail = args.detail!!
            setDataResource()
        }
*/
        resourcesViewModel.getResourceDetail(resourceId ?: 0)
    }

    @SuppressLint("SetTextI18n")
    private fun setDataResource() {
        fragmentResourcesDetailBinding.textViewTitle.text = resourceDetail!!.title
        fragmentResourcesDetailBinding.llImageWrapper.visibility = View.VISIBLE
        if (resourceDetail!!.thumbnailUrl != null && resourceDetail!!.thumbnailUrl != "") {
            Picasso.get().load(resourceDetail!!.thumbnailUrl)
                .placeholder(R.drawable.image)
                .into(fragmentResourcesDetailBinding.imageViewTopic)
        }
        fragmentResourcesDetailBinding.descriptionTV.text = resourceDetail!!.content
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
        fragmentResourcesDetailBinding.tvDate.text = "Created on $formattedDate"
        fragmentResourcesDetailBinding.tvTime.text = formattedTime
        // add created by and image check

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