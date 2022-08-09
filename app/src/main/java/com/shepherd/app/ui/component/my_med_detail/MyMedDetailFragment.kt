package com.shepherd.app.ui.component.my_med_detail

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.shepherd.app.R
import com.shepherd.app.databinding.FragmentMyMedDetialBinding
import com.shepherd.app.ui.base.BaseFragment
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
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show()
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