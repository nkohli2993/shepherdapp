package com.shepherd.app.ui.component.resources

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.shepherd.app.R
import com.shepherd.app.databinding.FragmentMessagesBinding
import com.shepherd.app.databinding.FragmentResourceDetailBinding
import com.shepherd.app.databinding.FragmentResourcesBinding
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.view_model.ResourceViewModel
import java.util.*


class ResourceDetailFragment : BaseFragment<FragmentResourceDetailBinding>(), View.OnClickListener {

    private val resourcesViewModel: ResourceViewModel by viewModels()

    private lateinit var fragmentResourcesDetailBinding: FragmentResourceDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentResourcesDetailBinding =
            FragmentResourceDetailBinding.inflate(inflater, container, false)

        return fragmentResourcesDetailBinding.root
    }

    override fun observeViewModel() {
        
    }

    override fun initViewBinding() {
        fragmentResourcesDetailBinding.listener = this
        setMedicalHistoryAdapter()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_resource_detail
    }

    private fun createTextView(text: String): View {
        val textView = TextView(requireContext())
        textView.text = text
        textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.gotham_book)
        textView.setTextColor(ContextCompat.getColor(requireContext(),R.color._192032))
        textView.setPadding(20,10,10,10)
        textView.setOnClickListener {
            showInfo(requireContext(),textView.text.toString())
        }
        textView.compoundDrawablePadding = 10
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_round_cancel,0)
        textView.setBackgroundResource(R.drawable.shape_black_border)
        return textView
    }

    private fun setMedicalHistoryAdapter() {
        for (locale in Locale.getAvailableLocales()) {
            val countryName: String = locale.displayCountry
            if (countryName.isNotEmpty()) {
                fragmentResourcesDetailBinding.medicalHistory.addView(
                    createTextView(countryName),
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
            }

        }

    }

}