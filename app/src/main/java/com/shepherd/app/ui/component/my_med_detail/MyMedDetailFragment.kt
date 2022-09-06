package com.shepherd.app.ui.component.my_med_detail

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.databinding.FragmentMyMedDetialBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.view_model.MyMedDetailVM
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MyMedDetailFragment : BaseFragment<FragmentMyMedDetialBinding>(), View.OnClickListener {
    private val medDetailViewModel: MyMedDetailVM by viewModels()
    private var textToSpeech: TextToSpeech? = null
    private lateinit var fragmentMyMedDetailBinding: FragmentMyMedDetialBinding
    private val TAG = "MyMedDetailFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMyMedDetailBinding =
            FragmentMyMedDetialBinding.inflate(inflater, container, false)

        return fragmentMyMedDetailBinding.root
    }

    override fun observeViewModel() {
        medDetailViewModel.getMedicationDetailResponseLiveData.observeEvent(this) {
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
                    Log.d(TAG, "Medication Detail :${it.data.payload} ")
                    fragmentMyMedDetailBinding.tvMedTitle.text = it.data.payload.medlist.name
                    fragmentMyMedDetailBinding.brandNameTV.text =it.data.payload.medlist.name
                    fragmentMyMedDetailBinding.txtDescription1.text = it.data.payload.medlist.description

                    it.data.payload.assigned_by_details.let { assignedByDetail ->
                        fragmentMyMedDetailBinding.tvUsername.text = "${assignedByDetail.firstname} ${assignedByDetail.lastname}"

                    }
                    if(it.data.payload.assigned_by_details.profile_photo!=null && it.data.payload.assigned_by_details.profile_photo!=""){
                        Picasso.get().load(it.data.payload.assigned_by_details.profile_photo)
                            .placeholder(R.drawable.image_placeholder)
                            .into(fragmentMyMedDetailBinding.imageViewUser)

                    }

                }
            }
        }
    }

    override fun initViewBinding() {
        fragmentMyMedDetailBinding.listener = this
        textToSpeech = TextToSpeech(
            requireContext().applicationContext
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.UK
            }
        }

        val id = arguments?.getInt("id")
        Log.d(TAG, "id : $id ")
        // Get Medication Detail
        id?.let { medDetailViewModel.getMedicationDetail(it) }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_med_detial
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.speakIV -> {
                val toSpeak: String = fragmentMyMedDetailBinding.tvMedTitle.text.toString()
                textToSpeech?.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    override fun onPause() {
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
        super.onPause()
    }
}