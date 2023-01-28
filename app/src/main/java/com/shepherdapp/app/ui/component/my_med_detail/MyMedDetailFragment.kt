package com.shepherdapp.app.ui.component.my_med_detail

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.FragmentMyMedDetialBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.utils.CustomTypefaceSpan
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.MyMedDetailVM
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
                    fragmentMyMedDetailBinding.tvMedTitle.text =
                        HtmlCompat.fromHtml(it.data.payload.medlist.name ?: "", 0)
                    fragmentMyMedDetailBinding.brandNameTV.text =
                        HtmlCompat.fromHtml(it.data.payload.medlist.name ?: "", 0)
                    /*fragmentMyMedDetailBinding.txtDescription1.text =
                        HtmlCompat.fromHtml( it.data.payload.medlist.description?: "", 0)*/
                    /* if (it.data.payload.medlist.description.isNullOrEmpty()) {
                         fragmentMyMedDetailBinding.txtDescription1.text =
                             getString(R.string.no_description_available)
                     } else {
                         fragmentMyMedDetailBinding.txtDescription1.text =
                             Html.fromHtml(it.data.payload.medlist.description)
                     }*/


                    val firstWord = "Note : "
                    var secondWord: String? = null

                    if (it.data.payload.note.isNullOrEmpty()) {

                        secondWord = getString(R.string.no_description_available)
                        // Create a new spannable with the two strings
                        val spannable: Spannable = SpannableString(firstWord + secondWord)
                        // Set the custom typeface to span over a section of the spannable object
                        spannable.setSpan(
                            CustomTypefaceSpan(
                                "gotham-bold",
                                ResourcesCompat.getFont(requireContext(), R.font.gotham_bold)
                            ),
                            0,
                            firstWord.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannable.setSpan(
                            CustomTypefaceSpan(
                                "gotham-book",
                                ResourcesCompat.getFont(requireContext(), R.font.gotham_book)
                            ),
                            firstWord.length,
                            firstWord.length + secondWord.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
/*
                        fragmentMyMedDetailBinding.txtDescription.text =
                            "Note : " + getString(R.string.no_description_available)*/

                        fragmentMyMedDetailBinding.txtDescription.text = spannable
                    } else {
                        secondWord = it.data.payload.note
                        // Create a new spannable with the two strings
                        val spannable: Spannable = SpannableString(firstWord + secondWord)
                        // Set the custom typeface to span over a section of the spannable object
                        spannable.setSpan(
                            CustomTypefaceSpan(
                                "gotham-bold",
                                ResourcesCompat.getFont(requireContext(), R.font.gotham_bold)
                            ),
                            0,
                            firstWord.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannable.setSpan(
                            CustomTypefaceSpan(
                                "gotham-book",
                                ResourcesCompat.getFont(requireContext(), R.font.gotham_book)
                            ),
                            firstWord.length,
                            firstWord.length + secondWord?.length!!,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        fragmentMyMedDetailBinding.txtDescription.text = spannable

                        /*  val dynamicText =
                              String.format(getString(R.string.format_notes), it.data.payload.note)
                          fragmentMyMedDetailBinding.txtDescription.text =
                              HtmlCompat.fromHtml(dynamicText, HtmlCompat.FROM_HTML_MODE_COMPACT)*/
                    }

                    it.data.payload.assigned_by_details.let { assignedByDetail ->
                        fragmentMyMedDetailBinding.tvUsername.text =
                            "${assignedByDetail.firstname} ${assignedByDetail.lastname}"

                    }
                    if (it.data.payload.assigned_by_details.profile_photo != null && it.data.payload.assigned_by_details.profile_photo != "") {
                        Picasso.get().load(it.data.payload.assigned_by_details.profile_photo)
                            .placeholder(R.drawable.ic_defalut_profile_pic)
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