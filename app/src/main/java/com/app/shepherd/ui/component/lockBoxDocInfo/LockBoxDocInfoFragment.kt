package com.app.shepherd.ui.component.lockBoxDocInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.app.shepherd.R
import com.app.shepherd.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.app.shepherd.data.dto.lock_box.update_lock_box.UpdateLockBoxRequestModel
import com.app.shepherd.databinding.FragmentUploadedLockBoxDocDetailBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.view_model.LockBoxDocInfoViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_uploaded_lock_box_doc_detail.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class LockBoxDocInfoFragment : BaseFragment<FragmentUploadedLockBoxDocDetailBinding>(),
    View.OnClickListener {

    private val lockBoxDocInfoViewModel: LockBoxDocInfoViewModel by viewModels()

    private lateinit var fragmentUploadedLockBoxDocDetailBinding: FragmentUploadedLockBoxDocDetailBinding
    private val args: LockBoxDocInfoFragmentArgs by navArgs()
    private var lockBox: LockBox? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentUploadedLockBoxDocDetailBinding =
            FragmentUploadedLockBoxDocDetailBinding.inflate(inflater, container, false)

        return fragmentUploadedLockBoxDocDetailBinding.root
    }

    override fun initViewBinding() {
        fragmentUploadedLockBoxDocDetailBinding.listener = this
        lockBox = args.lockBox
        fragmentUploadedLockBoxDocDetailBinding.let {
            it.txtLockBoxName.text = lockBox?.name
            it.txtLockBoxNote.text = lockBox?.note
            Picasso.get().load(lockBox?.documentUrl).placeholder(R.drawable.ic_defalut_profile_pic)
                .into(imgDoc)
            // Click edit lock box icon
            it.imgEditLockBox.setOnClickListener {
                layoutDocDetail.visibility = View.GONE
                layoutEditLockBoxDocDetail.visibility = View.VISIBLE
            }
        }
    }

    override fun observeViewModel() {
        lockBoxDocInfoViewModel.updateLockBoxDocResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                }
            }
        }

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                backPress()
            }
            R.id.imgEditLockBox -> {
                fragmentUploadedLockBoxDocDetailBinding.layoutDocDetail.visibility = View.GONE
                fragmentUploadedLockBoxDocDetailBinding.layoutEditLockBoxDocDetail.visibility =
                    View.VISIBLE
            }
            R.id.btnCancel -> {
                fragmentUploadedLockBoxDocDetailBinding.layoutDocDetail.visibility = View.VISIBLE
                fragmentUploadedLockBoxDocDetailBinding.layoutEditLockBoxDocDetail.visibility =
                    View.GONE
            }
            R.id.btnSaveChanges -> {
                val fileName =
                    fragmentUploadedLockBoxDocDetailBinding.edtFileName.text.toString().trim()
                val fileNote =
                    fragmentUploadedLockBoxDocDetailBinding.edtNote.text.toString().trim()
                val updateLockBoxRequestModel = UpdateLockBoxRequestModel(fileName, fileNote)
                lockBoxDocInfoViewModel.updateLockBoxDoc(lockBox?.id, updateLockBoxRequestModel)

            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_uploaded_lock_box_doc_detail
    }


}

