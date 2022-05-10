package com.app.shepherd.ui.component.myMedList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.FragmentMyMedlistBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.memberDetails.adapter.MemberModulesAdapter
import com.app.shepherd.ui.component.myMedList.adapter.MyMedicationsAdapter
import com.app.shepherd.ui.component.myMedList.adapter.MyRemindersAdapter
import com.app.shepherd.ui.component.myMedList.adapter.SelectedDayMedicineAdapter
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class MyMedListFragment : BaseFragment<FragmentMyMedlistBinding>(),
    View.OnClickListener {

    private val medListViewModel: MyMedListViewModel by viewModels()

    private lateinit var myMedlistBinding: FragmentMyMedlistBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myMedlistBinding =
            FragmentMyMedlistBinding.inflate(inflater, container, false)

        return myMedlistBinding.root
    }

    override fun initViewBinding() {
        myMedlistBinding.listener = this

        setRemindersAdapter()
        setMyMedicationsAdapter()
        setSelectedDayMedicineAdapter()


    }

    override fun observeViewModel() {
        observe(medListViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(medListViewModel.showSnackBar)
        observeToast(medListViewModel.showToast)
    }


    private fun handleLoginResult(status: Resource<LoginResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { medListViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        myMedlistBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        myMedlistBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setRemindersAdapter() {
        val myRemindersAdapter = MyRemindersAdapter(medListViewModel)
        myMedlistBinding.recyclerViewReminders.adapter = myRemindersAdapter

    }

    private fun setMyMedicationsAdapter() {
        val myMedicationsAdapter = MyMedicationsAdapter(medListViewModel)
        myMedlistBinding.recyclerViewMyMedications.adapter = myMedicationsAdapter

    }

    private fun setSelectedDayMedicineAdapter() {
        val selectedDayMedicineAdapter = SelectedDayMedicineAdapter(medListViewModel)
        myMedlistBinding.recyclerViewSelectedDayMedicine.adapter = selectedDayMedicineAdapter

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonSave -> {
                backPress()
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_medlist
    }


}

