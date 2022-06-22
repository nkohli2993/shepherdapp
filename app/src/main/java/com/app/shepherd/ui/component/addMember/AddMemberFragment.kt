package com.app.shepherd.ui.component.addMember

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.FragmentAddMemberBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.addMember.adapter.AddMemberRoleAdapter
import com.app.shepherd.ui.component.addMember.adapter.RestrictionsModuleAdapter
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.utils.*
import com.app.shepherd.utils.RegexUtils.isValidEmail
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_member.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddMemberFragment : BaseFragment<FragmentAddMemberBinding>(),
    View.OnClickListener {

    private val addMemberViewModel: AddMemberViewModel by viewModels()

    private lateinit var fragmentAddMemberBinding: FragmentAddMemberBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddMemberBinding =
            FragmentAddMemberBinding.inflate(inflater, container, false)

        return fragmentAddMemberBinding.root
    }

    override fun initViewBinding() {
        fragmentAddMemberBinding.listener = this

        setRoleAdapter()
        setRestrictionModuleAdapter()



    }

    override fun observeViewModel() {

    }


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {

    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentAddMemberBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentAddMemberBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setRoleAdapter() {
        val addMemberRoleAdapter = AddMemberRoleAdapter(addMemberViewModel)
//        fragmentAddMemberBinding.recyclerViewMemberRole.adapter = addMemberRoleAdapter
    }

    private fun setRestrictionModuleAdapter() {
        val restrictionsModuleAdapter = RestrictionsModuleAdapter(addMemberViewModel)
//        fragmentAddMemberBinding.recyclerViewModules.adapter = restrictionsModuleAdapter

/*       fragmentAddMemberBinding.recyclerViewModules.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )*/

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBack -> {
                backPress()
            }
          /*  R.id.buttonInvite -> {
                //backPress()
                startActivity(Intent(requireContext(), HomeActivity::class.java))
            }*/
            R.id.textViewRole -> {
                manageRoleViewVisibility()
            }
        }
    }

    private fun manageRoleViewVisibility() {
       /* if (recyclerViewMemberRole.visibility == View.VISIBLE) {
            recyclerViewMemberRole.toGone()
            textViewRole.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_drop_down,
                0
            );
        } else {
            recyclerViewMemberRole.toVisible()
            textViewRole.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_drop_up,
                0
            );
        }*/

    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_member
    }


}

