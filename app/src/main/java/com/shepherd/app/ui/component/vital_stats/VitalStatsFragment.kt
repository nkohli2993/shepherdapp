package com.shepherd.app.ui.component.vital_stats

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.*
import com.shepherd.app.R
import com.shepherd.app.data.dto.add_vital_stats.vital_stats_dashboard.GraphData
import com.shepherd.app.data.dto.add_vital_stats.vital_stats_dashboard.TypeData
import com.shepherd.app.data.dto.add_vital_stats.vital_stats_dashboard.VitalStatsData
import com.shepherd.app.databinding.FragmentVitalStatsBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.vital_stats.adapter.TypeAdapter
import com.shepherd.app.view_model.VitalStatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
@SuppressLint("SimpleDateFormat")
class VitalStatsFragment : BaseFragment<FragmentVitalStatsBinding>(),
    View.OnClickListener {
    private val typeList: ArrayList<TypeData> = arrayListOf()
    private val vitalStatsViewModel: VitalStatsViewModel by viewModels()
    private lateinit var fragmentVitalStatsBinding: FragmentVitalStatsBinding
    private var vitalStats: VitalStatsData? = null
    private var graphDataList: ArrayList<GraphData> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentVitalStatsBinding =
            FragmentVitalStatsBinding.inflate(inflater, container, false)

        return fragmentVitalStatsBinding.root
    }

    override fun observeViewModel() {
        vitalStatsViewModel.getVitatStatsLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
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
                    setData()
                }
                is DataResult.Failure -> {
                    hideLoading()

                }
            }
        }

    }

    private fun addType() {
        typeList.add(
            TypeData(
                typeList.size,
                getString(R.string.heart_rate)
            )
        )
        typeList.add(
            TypeData(
                typeList.size,
                getString(R.string.body_temp)
            )
        )
        typeList.add(
            TypeData(
                typeList.size,
                getString(R.string.blood_pressure)
            )
        )
        typeList.add(
            TypeData(
                typeList.size,
                getString(R.string.oxygen)
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

        addType()
        vitalStatsViewModel.getGraphDataVitalStats(
            SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time),
            vitalStatsViewModel.getLovedOneUUId()!!, "heart_rate"
        )
    }

    private fun setData() {
        fragmentVitalStatsBinding.cancleChat.setBackgroundColor(Color.WHITE)
        fragmentVitalStatsBinding.cancleChat.description.isEnabled = false
        fragmentVitalStatsBinding.cancleChat.setMaxVisibleValueCount(120)
        fragmentVitalStatsBinding.cancleChat.setPinchZoom(false)
        fragmentVitalStatsBinding.cancleChat.setDrawGridBackground(false)

        val xAxis: XAxis = fragmentVitalStatsBinding.cancleChat.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        val leftAxis: YAxis = fragmentVitalStatsBinding.cancleChat.axisLeft
        leftAxis.setLabelCount(10, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(true)

        val rightAxis: YAxis = fragmentVitalStatsBinding.cancleChat.axisRight
        rightAxis.isEnabled = false

        fragmentVitalStatsBinding.cancleChat.legend.isEnabled = false

        fragmentVitalStatsBinding.cancleChat.resetTracking()

        val values = ArrayList<CandleEntry>()

        for(i in graphDataList.indices){
            values.add(
                CandleEntry(
                    i.toFloat(),
                    if(graphDataList[i].x == 0) (graphDataList[i].y - 10).toFloat() else graphDataList[i].x.toFloat(),
                    graphDataList[i].y.toFloat(),
                    if(graphDataList[i].x == 0) (graphDataList[i].y - 10).toFloat() else graphDataList[i].x.toFloat(),
                    graphDataList[i].y.toFloat(),
                    0
                )
            )

        }
/*
        for (i in 0 until 40) {
            val multi: Float = (40 + 1).toFloat()
            val `val` = (Math.random() * 40).toFloat() + multi
            val high = (Math.random() * 9).toFloat() + 8f
            val low = (Math.random() * 9).toFloat() + 8f
            val open = (Math.random() * 6).toFloat() + 1f
            val close = (Math.random() * 6).toFloat() + 1f
            val even = i % 2 == 0
            values.add(
                CandleEntry(
                    i.toFloat(),
                    `val` + high,
                    `val` - low,
                    if (even) `val` + open else `val` - open,
                    if (even) `val` - close else `val` + close,
                    0
                )
            )
        }
*/

        Log.e("catch_exception", "value: ${values}")
        val set1 = CandleDataSet(values, "Data Set")

        set1.setDrawIcons(false)
        set1.axisDependency = AxisDependency.LEFT
        set1.shadowColor = Color.WHITE
        set1.shadowWidth = 0.2f
        set1.decreasingColor = Color.rgb(159, 123, 179)
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = Color.rgb(162, 110, 202)
        set1.increasingPaintStyle = Paint.Style.FILL
        set1.neutralColor = Color.BLUE
        set1.highlightLineWidth = 1f
        set1.valueTextColor = Color.rgb(255, 255, 255)
        val data = CandleData(set1)

        fragmentVitalStatsBinding.cancleChat.data = data
        fragmentVitalStatsBinding.cancleChat.invalidate()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_vital_stats

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.typeSpinnerLayout, R.id.graphTypeTV, R.id.spinner_down_arrow_image -> {
                openCloseTypeSpinner()
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