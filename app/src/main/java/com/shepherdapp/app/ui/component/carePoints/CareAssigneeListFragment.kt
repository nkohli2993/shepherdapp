package com.shepherdapp.app.ui.component.carePoints

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.added_events.UserAssigneeModel
import com.shepherdapp.app.databinding.FragmentCareAssigneeListBinding
import com.shepherdapp.app.databinding.FragmentCarePointDetailBinding
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.carePoints.adapter.AssigneeUserAdapter
import com.shepherdapp.app.ui.component.carePoints.adapter.CarePointEventCommentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CareAssigneeListFragment : BaseFragment<FragmentCareAssigneeListBinding>(),
    View.OnClickListener {

    private lateinit var fragmentCareAssigneeListBinding: FragmentCareAssigneeListBinding
    private var userAssignes: ArrayList<UserAssigneeModel> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCareAssigneeListBinding =
            FragmentCareAssigneeListBinding.inflate(inflater, container, false)

        if(arguments?.containsKey("assignee_user") == true){
            val list = requireArguments().getParcelableArrayList<UserAssigneeModel>("assignee_user") as ArrayList<UserAssigneeModel>
            userAssignes.addAll(list)
        }
        return fragmentCareAssigneeListBinding.root
    }

    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        fragmentCareAssigneeListBinding.listener = this
        setUserAdapter()
    }
    private fun setUserAdapter() {
        //set comment adapter added in list
        val commentAdapter = AssigneeUserAdapter(userAssignes)
        fragmentCareAssigneeListBinding.recyclerViewCareTeam.adapter = commentAdapter
    }


    override fun getLayoutRes(): Int {
       return R.layout.fragment_care_assignee_list
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivBack ->{
                findNavController().popBackStack()
            }
        }
    }
}