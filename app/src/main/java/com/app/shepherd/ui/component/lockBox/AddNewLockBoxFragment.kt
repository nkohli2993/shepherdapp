package com.app.shepherd.ui.component.lockBox

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentAddNewLockBoxBinding
import com.app.shepherd.databinding.FragmentLockboxBinding
import com.app.shepherd.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Deepak Rattan on 22-07-22
 */
@AndroidEntryPoint
class AddNewLockBoxFragment : BaseFragment<FragmentLockboxBinding>(),
    View.OnClickListener {

//    private val lockBoxViewModel: LockBoxViewModel by viewModels()

    private lateinit var fragmentAddNewLockBoxBinding: FragmentAddNewLockBoxBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddNewLockBoxBinding =
            FragmentAddNewLockBoxBinding.inflate(inflater, container, false)

        return fragmentAddNewLockBoxBinding.root
    }

    override fun initViewBinding() {
        fragmentAddNewLockBoxBinding.listener = this

        setUploadedFilesAdapter()


    }

    override fun observeViewModel() {
    }


    private fun setUploadedFilesAdapter() {
//        val uploadedFilesAdapter = UploadedFilesAdapter(lockBoxViewModel)
//        fragmentAddNewLockBoxBinding.rvUploadedFiles.adapter = uploadedFilesAdapter

    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnChooseFile -> {
                showChooseFileDialog()
            }
        }
    }

    private fun showChooseFileDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_choose_lockbox_file)
        val cvGoogleDrive = dialog.findViewById(R.id.cvGoogleDrive) as CardView
        val cvLocalStorage = dialog.findViewById(R.id.cvLocalStorage) as CardView
        val cancel = dialog.findViewById(R.id.txtCancel) as TextView
        // Click Google Drive
        cvGoogleDrive.setOnClickListener {
            dialog.dismiss()
            showToast("Google Drive Clicked")
        }

        // Click Local Storage
        cvLocalStorage.setOnClickListener {
            dialog.dismiss()
            showToast("Local Storage Clicked")
        }

        // Click Cancel
        cancel.setOnClickListener {
            dialog.dismiss()
            showToast("Cancel clicked")
        }
        dialog.setCancelable(false)
        dialog.show()
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_lockbox
    }


}

