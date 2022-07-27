package com.app.shepherd.ui.component.my_med_detail

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentMyMedDetialBinding
import com.app.shepherd.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MyMedDetailFragment : BaseFragment<FragmentMyMedDetialBinding>(), View.OnClickListener {
    private val medDetailViewModel: MyMedDetailVM by viewModels()
    private var textToSpeech: TextToSpeech? = null
    private lateinit var fragmentMyMedDetailBinding: FragmentMyMedDetialBinding
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

    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_med_detial
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.speakIV ->{
                val toSpeak: String = fragmentMyMedDetailBinding.tvMedTitle.getText().toString()
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show()
                textToSpeech?.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
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