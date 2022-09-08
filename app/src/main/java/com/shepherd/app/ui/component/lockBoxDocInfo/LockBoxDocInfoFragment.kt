package com.shepherd.app.ui.component.lockBoxDocInfo

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.shepherd.app.R
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherd.app.data.dto.lock_box.update_lock_box.UpdateLockBoxRequestModel
import com.shepherd.app.databinding.FragmentUploadedLockBoxDocDetailBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.lockBox.LockBoxFragmentDirections
import com.shepherd.app.ui.component.lockBoxDocInfo.adapter.UploadedDocumentImagesAdapter
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.utils.loadImageCentreCrop
import com.shepherd.app.view_model.LockBoxDocInfoViewModel
import com.squareup.picasso.Picasso
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
        fragmentUploadedLockBoxDocDetailBinding.let {
            it.txtLockBoxName.text = lockBox?.name
            it.txtLockBoxNote.text = lockBox?.note
            it.edtFileName.setText(lockBox?.name)
            it.edtNote.setText(lockBox?.note)
            Picasso.get().load(lockBox?.documentUrl).placeholder(R.drawable.ic_defalut_profile_pic)
                .into(imgDoc)
            // Click edit lock box icon
            it.imgEditLockBox.setOnClickListener {
//                layoutDocDetail.visibility = View.GONE
//                layoutEditLockBoxDocDetail.visibility = View.VISIBLE
                val action = LockBoxDocInfoFragmentDirections.actionNavEditLockbox(lockBox?.id.toString())
                findNavController().navigate(action)

            }
        }
        fragmentUploadedLockBoxDocDetailBinding.edtNote.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        documentImagesAdapter =
            UploadedDocumentImagesAdapter(requireContext().applicationContext, lockBox?.documents,this@LockBoxDocInfoFragment)
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
                val action = LockBoxDocInfoFragmentDirections.actionNavEditLockbox(lockBox?.id.toString())
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
        return R.layout.fragment_uploaded_lock_box_doc_detail
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showChooseFileDialog(path: String, type: String) {
        val dialog =
            Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_show_file)
        val tvClose = dialog.findViewById(R.id.tvClose) as AppCompatTextView
        val image = dialog.findViewById(R.id.imageShowIV) as AppCompatImageView
        val webview = dialog.findViewById(R.id.webview) as WebView
        image.visibility = View.GONE
        webview.visibility = View.GONE
        when (type) {
            "image" -> {
                image.visibility = View.VISIBLE
                image.loadImageCentreCrop(
                    R.drawable.image,
                    path
                )

            }
            else -> {
                webview.visibility = View.VISIBLE
                val webSettings: WebSettings = webview.settings
                webSettings.javaScriptEnabled = true
                val webViewClient = WebViewClientImpl(activity)
                webview.webViewClient = webViewClient
                webview.loadUrl(path)
            }
        }
        tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()
    }


}

