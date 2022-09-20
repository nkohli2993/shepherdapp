package com.shepherdapp.app.ui.component.vital_stats

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.add_vital_stats.vital_stats_dashboard.GraphData
import com.shepherdapp.app.data.dto.add_vital_stats.vital_stats_dashboard.TypeData
import com.shepherdapp.app.data.dto.add_vital_stats.vital_stats_dashboard.VitalStatsData
import com.shepherdapp.app.databinding.FragmentVitalStatsBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.vital_stats.adapter.TypeAdapter
import com.shepherdapp.app.utils.extensions.showError
import java.util.concurrent.TimeUnit
import com.shepherdapp.app.view_model.VitalStatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
@SuppressLint("SimpleDateFormat")
class VitalStatsFragment : BaseFragment<FragmentVitalStatsBinding>(),
    View.OnClickListener {
    private var type: String? = null
    private var xAxisLabel: ArrayList<String> = arrayListOf()
    private val typeList: ArrayList<TypeData> = arrayListOf()
    private val vitalStatsViewModel: VitalStatsViewModel by viewModels()
    private lateinit var fragmentVitalStatsBinding: FragmentVitalStatsBinding
    private var vitalStats: VitalStatsData? = null
    private var graphDataList: ArrayList<GraphData> = arrayListOf()
    //private var heartRate: Int
    var finalSelectedDate: Date? = null
    private val dateFormat = DateFormat.getDateInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentVitalStatsBinding =
            FragmentVitalStatsBinding.inflate(inflater, container, false)

        return fragmentVitalStatsBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun observeViewModel() {
        vitalStatsViewModel.getVitatStatsLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    vitalStats = null
                    graphDataList.clear()
                    fragmentVitalStatsBinding.typeChart.invalidate()
                    fragmentVitalStatsBinding.typeChart.clear()
                    vitalStats = it.data.payload.latestOne
                    graphDataList = it.data.payload.graphData


                    vitalStats.let { stats ->
                        //set data on dash board
                        fragmentVitalStatsBinding.tvHeartRateValue.text =
                            vitalStats!!.data?.heartRate
                        fragmentVitalStatsBinding.tvBodyTempValue.text =
                            vitalStats!!.data?.bodyTemp
                        fragmentVitalStatsBinding.tvBloodPressureValue.text =
                            vitalStats!!.data?.sbp.plus("/${vitalStats!!.data?.dbp}")
                        fragmentVitalStatsBinding.tvOxygenValue.text =
                            vitalStats!!.data?.oxygen
                    }
                    // to get min value from list
                    val dataAdded: ArrayList<Double> = arrayListOf()
                    for (i in graphDataList) {
                        dataAdded.add(i.x.toDouble())
                    }
                    fragmentVitalStatsBinding.tvHRMin.text = "Min ${Collections.min(dataAdded)}/"
//                    fragmentVitalStatsBinding.tvHRMin.text = "Min ${it.data.payload.minAverage}/"
                    fragmentVitalStatsBinding.tvHRMax.text = "Max ${it.data.payload.maxAverage}"
                    fragmentVitalStatsBinding.typeChart.invalidate()
                    fragmentVitalStatsBinding.typeChart.clear()
                    setData()
                }
                is DataResult.Failure -> {
                    hideLoading()
                }
            }
        }

    }

    private fun addType() {
        typeList.clear()
        typeList.add(
            TypeData(
                typeList.size,
                getString(R.string.heart_rate),
                "heart_rate"
            )
        )
        typeList.add(
            TypeData(
                typeList.size,
                getString(R.string.body_temp),
                "body_temp"
            )
        )
        typeList.add(
            TypeData(
                typeList.size,
                getString(R.string.blood_pressure),
                "blood_pressure"
            )
        )
        typeList.add(
            TypeData(
                typeList.size,
                getString(R.string.oxygen),
                "oxygen"
            )
        )

        val typeAdapter =
            TypeAdapter(
                requireContext(),
                R.layout.vehicle_spinner_drop_view_item,
                typeList
            )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fragmentVitalStatsBinding.typeSpinner.adapter = typeAdapter
    }

    override fun initViewBinding() {
        fragmentVitalStatsBinding.listener = this
        fragmentVitalStatsBinding.dateSelectedTV.text =
            SimpleDateFormat("EEE, MMM dd").format(Calendar.getInstance().time)
        addType()


        fragmentVitalStatsBinding.typeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (typeList[p2].type!! == "blood_pressure") {
                        showError(requireContext(), "Not Implemented")
                    } else {
                        type = typeList[p2].type!!
                        callGraphApi()
                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

    }

    private fun callGraphApi() {

        val dateSelected = if(finalSelectedDate == null) Calendar.getInstance().time
        else finalSelectedDate
        Log.d(TAG, "Date Selected :$dateSelected ")
        dateSelected?.let { SimpleDateFormat("yyyy-MM-dd").format(it) }?.let {
            vitalStatsViewModel.getGraphDataVitalStats(
                it,
                vitalStatsViewModel.getLovedOneUUId()!!, type = type!!
            )
        }
       /* val dateSelected = fragmentVitalStatsBinding.dateSelectedTV.text.toString()
        Log.d(TAG, "Date Selected :$dateSelected ")
        val date = SimpleDateFormat("EEE, MMM dd").parse(fragmentVitalStatsBinding.dateSelectedTV.text.toString())
        vitalStatsViewModel.getGraphDataVitalStats(
            SimpleDateFormat("yyyy-MM-dd").format(date!!),
            vitalStatsViewModel.getLovedOneUUId()!!, type = type!!
        )*/
    }

    private fun setData() {
        fragmentVitalStatsBinding.typeChart.setBackgroundColor(Color.WHITE)
        fragmentVitalStatsBinding.typeChart.description.isEnabled = false
        fragmentVitalStatsBinding.typeChart.setMaxVisibleValueCount(24)
        fragmentVitalStatsBinding.typeChart.setPinchZoom(false)
        fragmentVitalStatsBinding.typeChart.setDrawGridBackground(false)

        val xAxis: XAxis = fragmentVitalStatsBinding.typeChart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        val leftAxis: YAxis = fragmentVitalStatsBinding.typeChart.axisLeft
        leftAxis.setLabelCount(15, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(true)

        val rightAxis: YAxis = fragmentVitalStatsBinding.typeChart.axisRight
        rightAxis.isEnabled = false

        fragmentVitalStatsBinding.typeChart.legend.isEnabled = false

        fragmentVitalStatsBinding.typeChart.resetTracking()

        val values = ArrayList<CandleEntry>()
        values.clear()
        for (i in graphDataList.indices) {
            values.add(
                CandleEntry(
                    i.toFloat(),
                    if (graphDataList[i].x == 0) (graphDataList[i].y - 10).toFloat() else graphDataList[i].x.toFloat(),
                    graphDataList[i].y.toFloat(),
                    if (graphDataList[i].x == 0) (graphDataList[i].y - 10).toFloat() else graphDataList[i].x.toFloat(),
                    graphDataList[i].y.toFloat(),
                    0
                )
            )
        }

        xAxisLabel = ArrayList<String>()
        xAxisLabel.clear()
        for (i in graphDataList) {
            val time = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
            val dateTime = SimpleDateFormat("yyyy-MM-dd hh:mm a").parse(time.plus(" ${i.day}"))
            if (SimpleDateFormat("HH:mm").format(dateTime!!) != "00:00") {
                xAxisLabel.add(SimpleDateFormat("HH:mm").format(dateTime))
            }
//            xAxisLabel.add(SimpleDateFormat("HH:mm").format(dateTime!!))
        }
        fragmentVitalStatsBinding.typeChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(xAxisLabel)
        xAxis.setLabelCount(8, false)
        val set1 = CandleDataSet(values, "")
        set1.setDrawIcons(false)
        set1.axisDependency = AxisDependency.LEFT
        set1.shadowColor = Color.WHITE
        set1.shadowWidth = 1f
        set1.decreasingColor = Color.rgb(159, 123, 179)
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = Color.rgb(159, 123, 179)
        set1.increasingPaintStyle = Paint.Style.FILL
        set1.neutralColor = Color.BLUE
        set1.highlightLineWidth = 0f
        set1.barSpace = 3f
        set1.valueTextColor = ContextCompat.getColor(requireContext(), R.color.transparent)
//        fragmentVitalStatsBinding.typeChart.setVisibleXRangeMaximum(24f)
        fragmentVitalStatsBinding.typeChart.setScaleEnabled(false)
        fragmentVitalStatsBinding.typeChart.zoom(3f, 0f, 3f, 0f)
        fragmentVitalStatsBinding.typeChart.axisLeft.setAxisMinValue(10f)
        fragmentVitalStatsBinding.typeChart.axisRight.setAxisMinValue(10f)
        val data = CandleData(set1)
        fragmentVitalStatsBinding.typeChart.data = data
        fragmentVitalStatsBinding.typeChart.invalidate()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_vital_stats

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.typeSpinnerLayout, R.id.graphTypeTV, R.id.spinner_down_arrow_image -> {
                openCloseTypeSpinner()
            }
            R.id.dateSelectedTV -> {
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]

                val datePickerDialog = DatePickerDialog(
                    requireActivity(), R.style.datepicker,
                    { _, year, monthOfYear, dayOfMonth ->
                        val dateSelected = "$dayOfMonth-" + if (monthOfYear + 1 < 10) {
                            "0${(monthOfYear + 1)}"
                        } else {
                            (monthOfYear + 1)
                        } + "-" + year

                        Log.d(TAG, "onClick: DateSelected : $dateSelected")
                        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        date.timeZone = TimeZone.getDefault()
                        finalSelectedDate = date.parse(dateSelected)
                        Log.d(TAG, "onClick: Date : $finalSelectedDate")

                        fragmentVitalStatsBinding.dateSelectedTV.text =
                            SimpleDateFormat("EEE, MMM dd").format(finalSelectedDate)
                        // call api based on type
                        callGraphApi()

                    }, mYear, mMonth, mDay
                )
                datePickerDialog.datePicker.maxDate = c.timeInMillis
                datePickerDialog.show()
            }
        }
    }

    private fun openCloseTypeSpinner() {
        if (fragmentVitalStatsBinding.typeRV.visibility == View.VISIBLE) {
            fragmentVitalStatsBinding.typeRV.visibility = View.GONE
            rotate(0f, fragmentVitalStatsBinding.spinnerDownArrowImage)
        } else {
            fragmentVitalStatsBinding.typeRV.visibility = View.VISIBLE
            rotate(180f, fragmentVitalStatsBinding.spinnerDownArrowImage)
        }
    }

    private fun rotate(degree: Float, image: AppCompatImageView) {
        val rotateAnim = RotateAnimation(
            0.0f, degree,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.duration = 0
        rotateAnim.fillAfter = true
        image.startAnimation(rotateAnim)
    }
}