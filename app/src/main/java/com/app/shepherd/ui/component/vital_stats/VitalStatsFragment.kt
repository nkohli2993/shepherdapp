package com.app.shepherd.ui.component.vital_stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.shepherd.R
import com.app.shepherd.databinding.FragmentVitalStatsBinding
import com.app.shepherd.ui.base.BaseFragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VitalStatsFragment : BaseFragment<FragmentVitalStatsBinding>() {
    private val vitalStatsViewModel: VitalStatsViewModel by viewModels()
    private lateinit var fragmentVitalStatsBinding: FragmentVitalStatsBinding
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
    }

    override fun initViewBinding() {
        setLineChartData()
    }

    private fun setLineChartData() {
        val linevalues = ArrayList<Entry>()
        linevalues.add(Entry(20f, 0.0F))
        linevalues.add(Entry(30f, 3.0F))
        linevalues.add(Entry(40f, 2.0F))
        linevalues.add(Entry(50f, 1.0F))
        linevalues.add(Entry(60f, 8.0F))
        linevalues.add(Entry(70f, 10.0F))
        linevalues.add(Entry(80f, 1.0F))
        linevalues.add(Entry(90f, 2.0F))
        linevalues.add(Entry(100f, 5.0F))
        linevalues.add(Entry(110f, 1.0F))
        linevalues.add(Entry(120f, 20.0F))
        linevalues.add(Entry(130f, 40.0F))
        linevalues.add(Entry(140f, 50.0F))
        val linedataset = LineDataSet(linevalues, "First")
        linedataset.color = resources.getColor(R.color._008D98)

        linedataset.setDrawFilled(false)
        val linevaluess = ArrayList<Entry>()
        linevaluess.add(Entry(30f, 0.0F))
        linevaluess.add(Entry(40f, 3.0F))
        linevaluess.add(Entry(40f, 2.0F))
        linevaluess.add(Entry(60f, 1.0F))
        linevaluess.add(Entry(60f, 8.0F))
        linevaluess.add(Entry(70f, 10.0F))
        linevaluess.add(Entry(80f, 1.0F))
        linevaluess.add(Entry(90f, 2.0F))
        linevaluess.add(Entry(100f, 5.0F))
        linevaluess.add(Entry(110f, 1.0F))
        linevaluess.add(Entry(120f, 20.0F))
        linevaluess.add(Entry(130f, 40.0F))
        linevaluess.add(Entry(140f, 50.0F))
        val linedatasets = LineDataSet(linevaluess, "Second")
        //We add features to our chart
        linedatasets.color = resources.getColor(R.color._008D98)

        linedatasets.setDrawFilled(false)
        val dataSets = java.util.ArrayList<ILineDataSet>()
        dataSets.add(linedataset) // add the data sets
        dataSets.add(linedatasets) // add the data sets

        val lineData = LineData(dataSets)
        fragmentVitalStatsBinding.lineChart.apply {
            setDrawBorders(false)
            setDrawGridBackground(false)
            description.isEnabled = false
            axisLeft.setDrawGridLines(false)
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.setDrawGridLines(false)
            axisRight.isEnabled = false
            data = lineData
            setBackgroundColor(resources.getColor(R.color.colorWhite))
            animateXY(2000, 2000, Easing.EaseInCubic)
        }

    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_vital_stats

    }
}