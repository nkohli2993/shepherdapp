package com.shepherd.app.ui.component.add_vitals

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
import com.shepherd.app.databinding.FragmentAddVitalsBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.view_model.AddNewVitalStatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
@SuppressLint("SimpleDateFormat")
class AddVitalsFragment : BaseFragment<FragmentAddVitalsBinding>(), View.OnClickListener {
    private lateinit var fragmentAddVitalsBinding: FragmentAddVitalsBinding
    private val addVitalStatsViewModel: AddNewVitalStatsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddVitalsBinding =
            FragmentAddVitalsBinding.inflate(inflater, container, false)

        return fragmentAddVitalsBinding.root
    }

    override fun observeViewModel() {
        addVitalStatsViewModel.addVitatStatsLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(
                        requireContext(),
                        getString(R.string.vital_stats_added_successfully)
                    )
                    backPress()
                }

                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(requireContext(), it) }
                }
            }
        }
    }

    override fun initViewBinding() {
        fragmentAddVitalsBinding.listener = this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_vitals
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.clTimeWrapper -> {
                timePicker()
            }
            R.id.btnAdd -> {
                if (isValid) {
                    // add params for vital stats
                    val stats = VitalStatsRequestModel()
                    addVitalStatsViewModel.addVitalStats(stats)
                }
            }
        }
    }

    private fun timePicker() {
        val mCurrentTime = Calendar.getInstance()
        if (fragmentAddVitalsBinding.tvTime.text.toString().isNotEmpty()) {
            val dateTime = SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().time)
                .plus(fragmentAddVitalsBinding.tvTime.text.toString())
            mCurrentTime.time = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dateTime)!!
        }

        val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mCurrentTime.get(Calendar.MINUTE)

        val mTimePicker = TimePickerDialog(
            context, R.style.datepicker,
            { _, hourOfDay, selectedMinute ->
                fragmentAddVitalsBinding.tvTime.text =
                    String.format("%02d:%02d", hourOfDay, selectedMinute)

                if (hourOfDay < 12) {
                    setColorTimePicked(R.color._192032, R.color.colorBlackTrans50)
                } else {
                    setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
                }
            }, hour,
            minute, true
        )
        mTimePicker.show()
    }

    private fun setColorTimePicked(selected: Int, unselected: Int) {
        fragmentAddVitalsBinding.tvam.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                selected
            )
        )
        fragmentAddVitalsBinding.tvpm.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                unselected
            )
        )
    }

    private val isValid: Boolean
        get() {
            when {
                fragmentAddVitalsBinding.etHeartRate.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.etHeartRate.error =
                        getString(R.string.please_enter_your_heart_rate)
                    fragmentAddVitalsBinding.etHeartRate.requestFocus()
                }
                fragmentAddVitalsBinding.tvTime.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.tvTime.error = getString(R.string.please_choose_time)
                    fragmentAddVitalsBinding.tvTime.requestFocus()
                }
                fragmentAddVitalsBinding.etTemp.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.etTemp.error = getString(R.string.please_choose_temp)
                    fragmentAddVitalsBinding.etTemp.requestFocus()
                }
                fragmentAddVitalsBinding.etBp.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.etBp.error =
                        getString(R.string.please_enter_your_blood_pressure)
                    fragmentAddVitalsBinding.etBp.requestFocus()
                }
                fragmentAddVitalsBinding.etSpo.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.etSpo.error =
                        getString(R.string.please_enter_body_oxygen)
                    fragmentAddVitalsBinding.etSpo.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }


}