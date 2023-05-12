package com.shepherdapp.app.ui.component.carePoints

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.added_events.UserAssigneeModel
import com.shepherdapp.app.databinding.FragmentCareAssigneeListBinding
import com.shepherdapp.app.databinding.FragmentCarePointDetailBinding
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.carePoints.adapter.AssigneeUserAdapter
import com.shepherdapp.app.ui.component.carePoints.adapter.CarePointEventCommentAdapter
import com.shepherdapp.app.utils.CareRole
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.CreatedCarePointsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CareAssigneeListFragment : BaseFragment<FragmentCareAssigneeListBinding>(),
    View.OnClickListener, AssigneeUserAdapter.AssigneeSelected {
    private val carePointsViewModel: CreatedCarePointsViewModel by viewModels()
    private lateinit var fragmentCareAssigneeListBinding: FragmentCareAssigneeListBinding
    private var userAssignees: ArrayList<UserAssigneeModel> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::fragmentCareAssigneeListBinding.isInitialized) {
            fragmentCareAssigneeListBinding =
                FragmentCareAssigneeListBinding.inflate(inflater, container, false)

            if (arguments?.containsKey("assignee_user") == true) {
                userAssignees.clear()
                val list =
                    @Suppress("DEPRECATION") requireArguments().getParcelableArrayList<UserAssigneeModel>(
                        "assignee_user"
                    ) as ArrayList<UserAssigneeModel>
                userAssignees.addAll(list)
            }
            setUserAdapter()
        }
        return fragmentCareAssigneeListBinding.root
    }

    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        fragmentCareAssigneeListBinding.listener = this

    }

    private fun setUserAdapter() {
        //set comment adapter added in list
        userAssignees.filter { listAssinee ->
            listAssinee.user_details.id != carePointsViewModel.getUserDetail()!!.userId
        } as ArrayList
        val commentAdapter = AssigneeUserAdapter(userAssignees, this)
        fragmentCareAssigneeListBinding.recyclerViewCareTeam.adapter = commentAdapter
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_care_assignee_list
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }

    override fun onAssigneeSelected(detail: UserAssigneeModel) {
        Log.e("catch_exception", "assignee: $detail")
        if (carePointsViewModel.getUserDetail()?.userId == detail.user_details.id) {
            showError(requireContext(), "Cannot chat with your own.")
        } else {
            findNavController().navigate(
                R.id.action_nav_assignee_to_nav_chat,
                bundleOf("assignee_user" to detail)
            )

        }
    }
}