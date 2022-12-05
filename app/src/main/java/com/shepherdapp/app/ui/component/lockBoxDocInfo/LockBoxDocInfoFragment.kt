package com.shepherdapp.app.ui.component.lockBoxDocInfo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.Payload
import com.shepherdapp.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherdapp.app.data.dto.lock_box.update_lock_box.UpdateLockBoxRequestModel
import com.shepherdapp.app.databinding.FragmentUploadedLockBoxDocDetailBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.lockBoxDocInfo.adapter.UploadedDocumentImagesAdapter
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.LockBoxDocInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_uploaded_lock_box_doc_detail.*


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
    private var lockBoxId: Int? = null
    private var lbtId: Int? = null
    private var payload: Payload? = null
    private var shareLockBoxDocUrl: String? = null
    private val TAG = "LockBoxDocInfoFragment"

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
//       lockBox = args.lockBox
        lockBoxId = args.lockBoxId
        lockBoxDocInfoViewModel.getDetailLockBox(lockBoxId!!)
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
                    payload = result.data.payload
                    fragmentUploadedLockBoxDocDetailBinding.let {
                        it.txtLockBoxName.text = payload?.name
                        it.txtLockBoxNote.text = payload?.note
                        it.edtFileName.setText(payload?.name)
                        it.edtNote.setText(payload?.note)
                        it.txtTypeTV.text = payload?.lockbox_types?.name
                    }
                    lbtId = payload?.lbtId
                    if (payload?.documents.isNullOrEmpty()) {
                        fragmentUploadedLockBoxDocDetailBinding.viewPager.visibility = View.GONE
                        fragmentUploadedLockBoxDocDetailBinding.dotsIndicator.visibility = View.GONE
                    } else {
                        documentImagesAdapter =
                            UploadedDocumentImagesAdapter(
                                requireContext().applicationContext,
                                payload?.documents,
                                this@LockBoxDocInfoFragment
                            )
                        viewPager.adapter = documentImagesAdapter
                        fragmentUploadedLockBoxDocDetailBinding.dotsIndicator.setViewPager(viewPager)
                        fragmentUploadedLockBoxDocDetailBinding.viewPager.addOnPageChangeListener(
                            object :
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
        // Observe Share LockBox Response
        lockBoxDocInfoViewModel.shareLockBoxDocResponseLiveData.observeEvent(this) {
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
                    shareLockBoxDocUrl = it.data.payload?.documentUrl
                    Log.d(TAG, "ShareLockBox DocUrl : $shareLockBoxDocUrl")
                    if (shareLockBoxDocUrl.isNullOrEmpty()) {
                        showError(requireContext(), "Shareable Link Not found...")
                    } else {
                        shareUrl()
                    }
                }
            }
        }
    }

    // Share LockBox Document Url with other apps
    private fun shareUrl() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "LockBox Document URL")
            shareIntent.putExtra(Intent.EXTRA_TEXT, "LockBox Document URL : $shareLockBoxDocUrl")
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            Log.d(TAG, "shareUrl exception :$e")
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvShare -> {
                lockBoxDocInfoViewModel.shareLockBoxDoc(lockBoxId)
//                showInfo(requireContext(), getString(R.string.not_implmented))
            }
            R.id.ivBack -> {
                backPress()
            }
            R.id.imgEditLockBox -> {
                val action =
                    LockBoxDocInfoFragmentDirections.actionNavEditLockbox(
                        lockBoxId.toString(),
                        lbtId.toString()
                    )
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

