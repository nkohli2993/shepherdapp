package com.app.shepherd.ui.component.schedule_medicine

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentSchedulweMedicineBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.schedule_medicine.adapter.DaysAdapter
import com.app.shepherd.utils.extensions.showInfo
import java.util.*


class ScheduleMedicineFragment : BaseFragment<FragmentSchedulweMedicineBinding>(),
    View.OnClickListener {
    private lateinit var fragmentScheduleMedicineBinding: FragmentSchedulweMedicineBinding
    var alDose = arrayOf( "160 mg", "80 mg", "40 mg", "20 mg", "10 mg")
    var alFrequency = arrayOf(
        "Select Frequency",
        "Once a day",
        "Twice a day",
        "Three times a day",
        "Four times a day"
    )
    var alDays =
        arrayOf(
            "Select Day",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentScheduleMedicineBinding =
            FragmentSchedulweMedicineBinding.inflate(inflater, container, false)

        return fragmentScheduleMedicineBinding.root
    }

    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        fragmentScheduleMedicineBinding.listener = this
        val doseAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, alDose)
        doseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fragmentScheduleMedicineBinding.spDose.adapter = doseAdapter
        val frequencyAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                alFrequency
            )
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fragmentScheduleMedicineBinding.spFrequency.adapter = frequencyAdapter
        fragmentScheduleMedicineBinding.spDays.adapter =
            DaysAdapter(requireContext(), alDays)

    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_schedulwe_medicine
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.clTimeWrapper ->{
                timePicker()
            }
            R.id.btnSubmit ->{
                if(isValid){

                }
            }
        }
    }


    private fun timePicker() {
        val mCurrentTime = Calendar.getInstance()
        val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mCurrentTime.get(Calendar.MINUTE)

        val mTimePicker = TimePickerDialog(
            context,R.style.datepicker,
            { view, hourOfDay, selectedMinute ->
                fragmentScheduleMedicineBinding.tvTime.text =
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
        fragmentScheduleMedicineBinding.tvam.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                selected
            )
        )
        fragmentScheduleMedicineBinding.tvpm.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                unselected
            )
        )
    }

    private val isValid: Boolean
        get() {
            when {
                fragmentScheduleMedicineBinding.tvTime.text.toString().trim().isEmpty() -> {
                    showInfo(requireContext(), getString(R.string.please_enter_time_of_birth))
                    fragmentScheduleMedicineBinding.tvTime.requestFocus()
                }

                fragmentScheduleMedicineBinding.etNote.text.toString().trim().isEmpty() -> {
                    fragmentScheduleMedicineBinding.etNote.error =
                        getString(R.string.please_enter_med_list_notes)
                    fragmentScheduleMedicineBinding.etNote.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }

}