package com.pcsalt.example.airqualityindex.display.details

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.pcsalt.example.airqualityindex.databinding.FragmentAqiDetailsBinding
import com.pcsalt.example.airqualityindex.db.entity.AQIData
import com.pcsalt.example.airqualityindex.display.AQIDataViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AQIDetailsFragment : Fragment() {

    companion object {
        const val EXTRA_CITY_NAME = "extra_city_name"
    }

    private lateinit var binding: FragmentAqiDetailsBinding
    private lateinit var viewModel: AQIDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAqiDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cityName = arguments?.getString(EXTRA_CITY_NAME)

        if (cityName == null) {
            activity?.onBackPressed()
            return
        }

        viewModel = ViewModelProvider(this).get(AQIDataViewModel::class.java)

        viewModel.getLatestDataByCity(cityName).observe(viewLifecycleOwner, {
            prepareChart(cityName, it)
        })
    }

    private fun getMin(dataList: List<AQIData>): Float {
        var value = Float.MAX_VALUE
        for (data in dataList) {
            if (value.compareTo(data.aqi.toFloat()) > 0) {
                value = data.aqi.toFloat()
            }
        }
        return value
    }

    private fun getMax(dataList: List<AQIData>): Float {
        var value = Float.MIN_VALUE
        for (data in dataList) {
            if (value.compareTo(data.aqi.toFloat()) < 0) {
                value = data.aqi.toFloat()
            }
        }
        return value
    }

    private fun prepareChart(cityName: String, dataList: List<AQIData>) {
        val min = getMin(dataList)
        val max = getMax(dataList)

        binding.chartView.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            dragDecelerationFrictionCoef = 0.9f
            isDragEnabled = true
            setDrawGridBackground(false)
            isHighlightPerDragEnabled = true
            setBackgroundColor(Color.WHITE)
            setViewPortOffsets(0f, 0f, 0f, 0f)
        }

        binding.chartView.legend.isEnabled = false
        val xAxis: XAxis = binding.chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.textSize = 10f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(true)
        xAxis.textColor = Color.rgb(255, 192, 56)
        xAxis.setCenterAxisLabels(true)
        xAxis.isGranularityEnabled = false

        xAxis.valueFormatter = object : ValueFormatter() {
            private val format = SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH)
            override fun getFormattedValue(value: Float): String {
                return format.format(Date(value.toLong()))
            }
        }

        val leftAxis: YAxis = binding.chartView.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.granularity = 2f
        leftAxis.axisMinimum = min - 2
        leftAxis.axisMaximum = max + 2
        leftAxis.yOffset = -9f
        leftAxis.textColor = Color.BLACK

        val rightAxis: YAxis = binding.chartView.axisRight
        rightAxis.isEnabled = false

        val values: ArrayList<Entry> = ArrayList()

        for (data in dataList) {
            values.add(Entry(data.lastUpdated.toFloat(), data.aqi.toFloat()))
        }

        val set1 = LineDataSet(values, cityName)
        set1.axisDependency = AxisDependency.LEFT
        set1.color = ColorTemplate.getHoloBlue()
        set1.valueTextColor = ColorTemplate.getHoloBlue()
        set1.lineWidth = 1.5f
        set1.setDrawCircles(false)
        set1.setDrawValues(false)
        set1.fillAlpha = 65
        set1.fillColor = ColorTemplate.getHoloBlue()
        set1.highLightColor = Color.rgb(244, 117, 117)
        set1.setDrawCircleHole(false)

        val data = LineData(set1)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)
        binding.chartView.legend.isEnabled = true
        binding.chartView.data = data
    }
}