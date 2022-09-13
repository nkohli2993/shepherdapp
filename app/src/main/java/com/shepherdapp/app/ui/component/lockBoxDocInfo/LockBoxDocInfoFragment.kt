package com.shepherdapp.app.ui.component.lockBoxDocInfo

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.shepherdapp.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherdapp.app.data.dto.lock_box.update_lock_box.UpdateLockBoxRequestModel
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.lockBoxDocInfo.adapter.UploadedDocumentImagesAdapter
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.utils.loadImageCentreCrop
import com.shepherdapp.app.view_model.LockBoxDocInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_uploaded_lock_box_doc_detail.*
import java.util.*
import androidx.navigation.fragment.findNavController
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.FragmentUploadedLockBoxDocDetailBinding


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class LockBoxDocInfoFragment : BaseFragment<FragmentUploadedLockBoxDocDetailBinding>(),
    View.OnClickListener {

    private lateinit var fragmentUploadedLockBoxDocDetailBinding: FragmentUploadedLockBoxDocDetailBinding
    private val lockBoxDocInfoViewModel: LockBoxDocInfoViewModel by viewModels()
    private var documentImagesAdapter: UploadedDocumentImagesAdapter? = null
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

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewBinding() {
        fragmentUploadedLockBoxDocDetailBinding.listener = this
        lockBox = args.lockBox
        lockBoxDocInfoViewModel.getDetailLockBox(lockBox?.id!!)
        fragmentUploadedLockBoxDocDetailBinding.edtNote.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
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
                    it.data.message?.let { it1 -> showSuccess(requireContext(), it1) }
                    backPress()
                }
            }
        }
        lockBoxDocInfoViewModel.getDetailLockBoxResponseLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val lockBox = result.data.payload
                    fragmentUploadedLockBoxDocDetailBinding.let {
                        it.txtLockBoxName.text = lockBox?.name
                        it.txtLockBoxNote.text = lockBox?.note
                        it.edtFileName.setText(lockBox?.name)
                        it.edtNote.setText(lockBox?.note)
                        it.txtTypeTV.setText(lockBox?.lbtId.toString())  /// set lockbox type name
                    }

                    documentImagesAdapter =
                        UploadedDocumentImagesAdapter(
                            requireContext().applicationContext,
                            lockBox?.documents,
                            this@LockBoxDocInfoFragment
                        )
                    viewPager.adapter = documentImagesAdapter
                    fragmentUploadedLockBoxDocDetailBinding.dotsIndicator.setViewPager(viewPager)
                    fragmentUploadedLockBoxDocDetailBinding.viewPager.addOnPageChangeListener(object :
                        ViewPager.OnPageChangeListener {
                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {
                        }

                        override fun onPageSelected(position: Int) {

                        }

                        override fun onPageScrollStateChanged(state: Int) {}
                    })
                }
            }
        }

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvShare -> {
                showInfo(requireContext(), getString(R.string.not_implmented))
            }
            R.id.ivBack -> {
                backPress()
            }
            R.id.imgEditLockBox -> {
//                fragmentUploadedLockBoxDocDetailBinding.layoutDocDetail.visibility = View.GONE
//                fragmentUploadedLockBoxDocDetailBinding.layoutEditLockBoxDocDetail.visibility =
//                    View.VISIBLE
                val action =
                    LockBoxDocInfoFragmentDirections.actionNavEditLockbox(lockBox?.id.toString())
                findNavController().navigate(action)
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
        return R.layout.fragment_uploaded_lock_box_doc_info
    }
}

