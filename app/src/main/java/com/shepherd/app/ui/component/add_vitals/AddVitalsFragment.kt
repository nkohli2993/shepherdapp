package com.shepherd.app.ui.component.add_vitals

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.dto.add_vital_stats.add_vital_stats.AddVitalData
import com.shepherd.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
import com.shepherd.app.databinding.FragmentAddVitalsBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.extensions.*
import com.shepherd.app.view_model.AddNewVitalStatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
@SuppressLint("SimpleDateFormat", "SetTextI18n")
class AddVitalsFragment : BaseFragment<FragmentAddVitalsBinding>(), View.OnClickListener {
    private lateinit var fragmentAddVitalsBinding: FragmentAddVitalsBinding
    private val addVitalStatsViewModel: AddNewVitalStatsViewModel by viewModels()
    private var isAmPm: String? = null
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
        addVitalStatsViewModel.addVitatStatsLiveData.observeEvent(this) { addedStatsResult ->
            when (addedStatsResult) {
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
                    if (addedStatsResult.error.isNotEmpty()) {
                        showError(requireContext(), addedStatsResult.error)
                    } else {
                        addedStatsResult.message?.let { showError(requireContext(), it) }
                    }
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
            R.id.tvDate -> {
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]

                val datePickerDialog = DatePickerDialog(
                    requireActivity(), R.style.datepicker,
                    { _, year, monthOfYear, dayOfMonth ->
                        fragmentAddVitalsBinding.tvDate.setText(
                            "$dayOfMonth-" + if (monthOfYear + 1 < 10) {
                                "0${(monthOfYear + 1)}"
                            } else {
                                (monthOfYear + 1)
                            } + "-" + year
                        )
                    }, mYear, mMonth, mDay
                )
                datePickerDialog.datePicker.maxDate = c.timeInMillis
                datePickerDialog.show()
            }

            R.id.btnAdd -> {
                if (isValid) {
                    var selectedDate = fragmentAddVitalsBinding.tvDate.text.toString().trim()
                    var dateFormat = SimpleDateFormat("dd-MM-yyyy")
                    val formattedDate: Date = dateFormat.parse(selectedDate)!!
                    dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    selectedDate = dateFormat.format(formattedDate)
                    val data = AddVitalData(
                        fragmentAddVitalsBinding.etHeartRate.text.toString().trim(),
                        fragmentAddVitalsBinding.etSbp.text.toString().trim(),
                        fragmentAddVitalsBinding.etDbp.text.toString().trim(),
                        fragmentAddVitalsBinding.etTemp.text.toString().trim(),
                        fragmentAddVitalsBinding.etSpo.text.toString().trim()
                    )
                    val stats = VitalStatsRequestModel(
                        addVitalStatsViewModel.getLovedOneUUId(),
                        selectedDate,
                        fragmentAddVitalsBinding.tvTime.text.toString().plus(" $isAmPm"),
                        data
                    )
                    addVitalStatsViewModel.addVitalStats(stats)
                }
            }
        }
    }

    private fun timePicker() {
        val mCurrentTime = Calendar.getInstance()
        if (fragmentAddVitalsBinding.tvTime.text.toString().isNotEmpty()) {
            val dateTime = SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().time)
                .plus(" ").plus(fragmentAddVitalsBinding.tvTime.text.toString())
            mCurrentTime.time = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dateTime)!!
        }
        val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mCurrentTime.get(Calendar.MINUTE)

        val mTimePicker = TimePickerDialog(
            context, R.style.datepicker,
            { _, hourOfDay, selectedMinute ->
                // check time for current date
                val selectedDateTime =
                    fragmentAddVitalsBinding.tvDate.text.toString().trim().plus(" ")
                        .plus(String.format("%02d:%02d", hourOfDay, selectedMinute))
                val currentDateTime =
                    SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().time)

                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
                if (dateFormat.parse(selectedDateTime)!!
                        .before(dateFormat.parse(currentDateTime)) || dateFormat.parse(
                        selectedDateTime
                    )!!
                        .equals(dateFormat.parse(currentDateTime))
                ) {
                    if (hourOfDay < 12) {
                        setColorTimePicked(R.color._192032, R.color.colorBlackTrans50)
                        isAmPm = "am"
                        fragmentAddVitalsBinding.tvTime.setText(
                            String.format("%02d:%02d", hourOfDay, selectedMinute)
                        )
                    } else {
                        isAmPm = "pm"
                        setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
                        fragmentAddVitalsBinding.tvTime.setText(
                            String.format("%02d:%02d", hourOfDay - 12, selectedMinute)
                        )
                    }

                } else {
                    showError(
                        requireContext(),
                        getString(R.string.unable_to_add_future_time_data_vital_stats)
                    )
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
                fragmentAddVitalsBinding.tvDate.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.tvDate.error = getString(R.string.please_choose_date)
                    fragmentAddVitalsBinding.tvDate.requestFocus()
                }
                fragmentAddVitalsBinding.tvTime.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.tvTime.error = getString(R.string.please_choose_time)
                    fragmentAddVitalsBinding.tvTime.requestFocus()
                }
                fragmentAddVitalsBinding.etHeartRate.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.etHeartRate.error =
                        getString(R.string.please_enter_your_heart_rate)
                    fragmentAddVitalsBinding.etHeartRate.requestFocus()
                }
                fragmentAddVitalsBinding.etHeartRate.text.toString().toDouble() < 60 -> {
                    fragmentAddVitalsBinding.etHeartRate.error =
                        getString(R.string.please_enter_valid_heart_rate)
                    fragmentAddVitalsBinding.etHeartRate.requestFocus()
                }
                fragmentAddVitalsBinding.etHeartRate.text.toString().toDouble() > 100 -> {
                    fragmentAddVitalsBinding.etHeartRate.error =
                        getString(R.string.please_enter_valid_heart_rate)
                    fragmentAddVitalsBinding.etHeartRate.requestFocus()
                }
                fragmentAddVitalsBinding.etTemp.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.etTemp.error = getString(R.string.please_choose_temp)
                    fragmentAddVitalsBinding.etTemp.requestFocus()
                }
                fragmentAddVitalsBinding.etTemp.checkString().toDouble() < 95.9 -> {
                    fragmentAddVitalsBinding.etTemp.error =
                        getString(R.string.please_enter_valid_body_temperature)
                    fragmentAddVitalsBinding.etTemp.requestFocus()
                }
                fragmentAddVitalsBinding.etTemp.checkString().toDouble() > 105 -> {
                    fragmentAddVitalsBinding.etTemp.error =
                        getString(R.string.please_enter_valid_body_temperature)
                    fragmentAddVitalsBinding.etTemp.requestFocus()
                }
                fragmentAddVitalsBinding.etSbp.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.etSbp.error =
                        getString(R.string.please_enter_your_blood_pressure_sbp)
                    fragmentAddVitalsBinding.etSbp.requestFocus()
                }
                fragmentAddVitalsBinding.etSbp.checkString().toDouble() < 60 -> {
                    fragmentAddVitalsBinding.etSbp.error =
                        getString(R.string.please_enter_valid_sbp_bp)
                    fragmentAddVitalsBinding.etSbp.requestFocus()
                }
                fragmentAddVitalsBinding.etSbp.checkString().toDouble() > 180 -> {
                    fragmentAddVitalsBinding.etSbp.error =
                        getString(R.string.please_enter_valid_sbp_bp)
                    fragmentAddVitalsBinding.etSbp.requestFocus()
                }
                fragmentAddVitalsBinding.etDbp.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.etDbp.error =
                        getString(R.string.please_enter_your_blood_pressure_dbp)
                    fragmentAddVitalsBinding.etDbp.requestFocus()
                }
                fragmentAddVitalsBinding.etDbp.checkString().toDouble() < 60 -> {
                    fragmentAddVitalsBinding.etDbp.error =
                        getString(R.string.please_enter_valid_dbp_bp)
                    fragmentAddVitalsBinding.etDbp.requestFocus()
                }
                fragmentAddVitalsBinding.etDbp.checkString().toDouble() > 180 -> {
                    fragmentAddVitalsBinding.etDbp.error =
                        getString(R.string.please_enter_valid_dbp_bp)
                    fragmentAddVitalsBinding.etDbp.requestFocus()
                }
                fragmentAddVitalsBinding.etSpo.text.toString().trim().isEmpty() -> {
                    fragmentAddVitalsBinding.etSpo.error =
                        getString(R.string.please_enter_body_oxygen)
                    fragmentAddVitalsBinding.etSpo.requestFocus()
                }
                fragmentAddVitalsBinding.etSpo.checkString()
                    .isNotEmpty() && fragmentAddVitalsBinding.etSpo.checkString()
                    .toInt() <= 0 && fragmentAddVitalsBinding.etSpo.checkString().toInt() > 100 -> {
                    fragmentAddVitalsBinding.etSpo.error =
                        getString(R.string.please_enter_valid_oxygen_level)
                    fragmentAddVitalsBinding.etSpo.requestFocus()
                }
                fragmentAddVitalsBinding.etSpo.checkString()
                    .isNotEmpty() && fragmentAddVitalsBinding.etSpo.checkString().toInt() > 100 -> {
                    fragmentAddVitalsBinding.etSpo.error =
                        getString(R.string.please_enter_valid_oxygen_level)
                    fragmentAddVitalsBinding.etSpo.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }
}