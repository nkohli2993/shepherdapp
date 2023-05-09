package com.shepherdapp.app.ui.component.carePoints

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.FragmentCareAssigneeListBinding
import com.shepherdapp.app.ui.base.BaseFragment


class CareAssigneeListFragment : BaseFragment<FragmentCareAssigneeListBinding>() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_care_assignee_list, container, false)
    }

    override fun observeViewModel() {
        TODO("Not yet implemented")
    }

    override fun initViewBinding() {
    }

    override fun getLayoutRes(): Int {
       return R.layout.fragment_care_assignee_list
    }
}