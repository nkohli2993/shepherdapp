package com.shepherdapp.app.ui.component.contactus

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.FragmentContactUsBinding
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.utils.extensions.stripUnderlines
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class ContactUsFragment : BaseFragment<FragmentContactUsBinding>(), View.OnClickListener {

    private lateinit var fragmentContactUsBinding: FragmentContactUsBinding
    private var parentActivityListener: ChildFragmentToActivityListener? = null
    private lateinit var homeActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            homeActivity = context
        }
        if (context is ChildFragmentToActivityListener) parentActivityListener = context
        else throw RuntimeException("$context must implement ChildFragmentToActivityListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentContactUsBinding =
            FragmentContactUsBinding.inflate(inflater, container, false)

        return fragmentContactUsBinding.root
    }

    override fun initViewBinding() {
        fragmentContactUsBinding.listener = this


        try {
            (fragmentContactUsBinding.txtEmail.text as Spannable).stripUnderlines()
            (fragmentContactUsBinding.txtPhone.text as Spannable).stripUnderlines()
        }catch (e:Exception){ e.printStackTrace() }

    }




    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_contact_us
    }

    override fun observeViewModel() {

    }


}

