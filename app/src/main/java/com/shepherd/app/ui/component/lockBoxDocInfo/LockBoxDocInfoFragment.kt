package com.shepherd.app.ui.component.lockBoxDocInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.shepherd.app.R
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.DocumentUrl
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherd.app.data.dto.lock_box.update_lock_box.UpdateLockBoxRequestModel
import com.shepherd.app.databinding.FragmentUploadedLockBoxDocDetailBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.lockBoxDocInfo.adapter.UploadedDocumentImagesAdapter
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.view_model.LockBoxDocInfoViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_uploaded_lock_box_doc_detail.*
import kotlinx.android.synthetic.main.fragment_uploaded_lock_box_doc_detail.viewPager


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class LockBoxDocInfoFragment : BaseFragment<FragmentUploadedLockBoxDocDetailBinding>(),
    View.OnClickListener {

    private val lockBoxDocInfoViewModel: LockBoxDocInfoViewModel by viewModels()
    private var documentImagesAdapter: UploadedDocumentImagesAdapter? = null
    private lateinit var fragmentUploadedLockBoxDocDetailBinding: FragmentUploadedLockBoxDocDetailBinding
    private val args: LockBoxDocInfoFragmentArgs by navArgs()
    private var lockBox: LockBox? = null
    private var list: ArrayList<DocumentUrl> = arrayListOf()  //TODO: Change type according to type of file to show on view pager


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

        documentImagesAdapter =
            UploadedDocumentImagesAdapter(requireContext().applicationContext, lockBox?.documents)
        viewPager.adapter = documentImagesAdapter
        fragmentUploadedLockBoxDocDetailBinding.dotsIndicator.setViewPager(viewPager)
        fragmentUploadedLockBoxDocDetailBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                //TODO: Handle click for document based on functionality
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
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
                    it.data.message?.let { it1 -> showSuccess(requireContext(), it1) }
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

