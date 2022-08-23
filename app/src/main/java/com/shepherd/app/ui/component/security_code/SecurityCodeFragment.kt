package com.shepherd.app.ui.component.security_code

//import com.example.numpad.NumPadClick

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.poovam.pinedittextfield.PinField
import com.shepherd.app.R
import com.shepherd.app.databinding.FragmentSecurityCodeBinding
import com.shepherd.app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.annotations.NotNull


@AndroidEntryPoint
class SecurityCodeFragment : BaseFragment<FragmentSecurityCodeBinding>(), View.OnClickListener {
    private val securityCodeViewModel: SecurityCodeViewModel by viewModels()

    private lateinit var fragmentSecurityCodeBinding: FragmentSecurityCodeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSecurityCodeBinding =
            FragmentSecurityCodeBinding.inflate(inflater, container, false)

        return fragmentSecurityCodeBinding.root
    }

    override fun observeViewModel() {
    }

    override fun initViewBinding() {
        fragmentSecurityCodeBinding.listener = this
//        fragmentSecurityCodeBinding.numpad.setOnNumPadClickListener(NumPadClick { nums ->
//            Log.e("TAG", "initViewBinding: $nums")
////            Log.d(
////                "MYTAG",
////                "onNumpadClicked: $nums"
////            )
//        })
        fragmentSecurityCodeBinding.squareField.highlightPaintColor  = ContextCompat.getColor(requireContext(),R.color._192032)
        fragmentSecurityCodeBinding.squareField.fieldColor  = ContextCompat.getColor(requireContext(),R.color._192032)
        fragmentSecurityCodeBinding.squareField.onTextCompleteListener = object : PinField.OnTextCompleteListener {
            override fun onTextComplete(@NotNull enteredText: String): Boolean {
                Toast.makeText(requireContext(), enteredText, Toast.LENGTH_SHORT).show()
                return true // Return false to keep the keyboard open else return true to close the keyboard
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_security_code
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
        }
    }
}