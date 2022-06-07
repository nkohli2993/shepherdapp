package com.app.shepherd.ui.component.my_med_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentMyMedDetialBinding
import com.app.shepherd.databinding.FragmentMyMedlistBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.myMedList.MyMedListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyMedDetailFragment : BaseFragment<FragmentMyMedDetialBinding>(), View.OnClickListener {
    private val medDetailViewModel: MyMedDetailVM by viewModels()

    private lateinit var myMedDetailBinding: FragmentMyMedDetialBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myMedDetailBinding =
            FragmentMyMedDetialBinding.inflate(inflater, container, false)

        return myMedDetailBinding.root
    }

    override fun observeViewModel() {
    }

    override fun initViewBinding() {
        myMedDetailBinding.listener = this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_med_detial
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }
}