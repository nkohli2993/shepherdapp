package com.app.shepherd.ui.component.memberDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.app.shepherd.R
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.databinding.FragmentAddMemberBinding
import com.app.shepherd.databinding.FragmentMemberDetailsBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.memberDetails.adapter.MemberModulesAdapter
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class MemberDetailsFragment : BaseFragment<FragmentAddMemberBinding>(),
    View.OnClickListener {

    private val memberDetailsViewModel: MemberDetailsViewModel by viewModels()

    private lateinit var fragmentMemberDetailsBinding: FragmentMemberDetailsBinding

    private val args: MemberDetailsFragmentArgs by navArgs()
    private var careTeam: CareTeam? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMemberDetailsBinding =
            FragmentMemberDetailsBinding.inflate(inflater, container, false)

        return fragmentMemberDetailsBinding.root
    }

    override fun initViewBinding() {
        fragmentMemberDetailsBinding.listener = this

        setRestrictionModuleAdapter()
        careTeam = args.careTeam
        initView()

    }

    private fun initView() {
        // Set profile pic
        Picasso.get().load(careTeam?.user?.userProfiles?.profilePhoto)
            .placeholder(R.drawable.test_image)
            .into(fragmentMemberDetailsBinding.imgCareTeamMember)

        // Set Name
        careTeam?.user?.userProfiles.let {
            fragmentMemberDetailsBinding.txtCareTeamMemberName.text =
                it?.firstname + " " + it?.lastname
        }

        fragmentMemberDetailsBinding.txtCareTeamMemberDesignation.text = careTeam?.careRoles?.name

        /* fragmentMemberDetailsBinding.txtCareTeamMemberName.text =
             careTeam?.user?.userProfiles?.fullName
 */
        //Set EmailID
        /* fragmentMemberDetailsBinding.txtEmailCare.text =
             careTeam?.user?.userProfiles?.*/

        //Set Phone Number
        /*  fragmentMemberDetailsBinding.txtCareTeamMemberName.text =
              careTeam?.user?.userProfiles?*/

        //Set Address
    }

    override fun observeViewModel() {

    }

    private fun setRestrictionModuleAdapter() {
        val memberModulesAdapter = MemberModulesAdapter(memberDetailsViewModel)
//        fragmentMemberDetailsBinding.recyclerViewModules.adapter = memberModulesAdapter

        /* fragmentMemberDetailsBinding.recyclerViewModules.addItemDecoration(
             DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
         )*/

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            /*  R.id.buttonSave -> {
                  backPress()
              }*/
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_member_details
    }


}

