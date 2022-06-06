package com.app.shepherd.ui.component.my_med_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentMyMedDetialBinding
import com.app.shepherd.databinding.FragmentMyMedlistBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.myMedList.MyMedListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyMedDetialFragment : BaseFragment<FragmentMyMedDetialBinding>() {
    private val medDetailViewModel: MyMedDetailVM by viewModels()

    private lateinit var myMedDetialBinding: FragmentMyMedDetialBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myMedDetialBinding =
            FragmentMyMedDetialBinding.inflate(inflater, container, false)

        return myMedDetialBinding.root
    }

    override fun observeViewModel() {
    }

    override fun initViewBinding() {
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_med_detial
    }
}