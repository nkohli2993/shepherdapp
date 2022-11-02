package com.shepherdapp.app.ui.component.vital_stats

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
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
import android.widget.TextView
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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.data.HealthFields.*
import com.google.android.gms.fitness.request.DataDeleteRequest
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats.AddBloodPressureData
import com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats.AddVitalData
import com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
import com.shepherdapp.app.data.dto.add_vital_stats.vital_stats_dashboard.GraphData
import com.shepherdapp.app.data.dto.add_vital_stats.vital_stats_dashboard.TypeData
import com.shepherdapp.app.data.dto.add_vital_stats.vital_stats_dashboard.VitalStatsData
import com.shepherdapp.app.databinding.FragmentVitalStatsBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.vital_stats.adapter.TypeAdapter
import com.shepherdapp.app.utils.extensions.getEndTimeString
import com.shepherdapp.app.utils.extensions.getStartTimeString
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.VitalStatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


const val TAG = "VitalStatsFragment"


/**
 * This enum is used to define actions that can be performed after a successful sign in to Fit.
 * One of these values is passed to the Fit sign-in, and returned in a successful callback, allowing
 * subsequent execution of the desired action.
 */
enum class FitActionRequestCode {
    INSERT_AND_READ_DATA,
    UPDATE_AND_READ_DATA,
    DELETE_DATA
}

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

    private var heartRateAvg: String? = null
    private var heartRateMin: String? = null
    private var heartRateMax: String? = null
    private var heartRate: String? = null

    private var bloodPressureSysAvg: String? = null
    private var bloodPressureSysMin: String? = null
    private var bloodPressureSysMax: String? = null
    private var bloodPressureSys: String? = null

    private var bloodPressureDiaAvg: String? = null
    private var bloodPressureDiaMin: String? = null
    private var bloodPressureDiaMax: String? = null
    private var bloodPressureDia: String? = null


    private var oxygenAvg: String? = null
    private var oxygenMin: String? = null
    private var oxygenMax: String? = null
    private var oxygenSaturation: String? = null


    private var bodyTempAvg: String? = null
    private var bodyTempMin: String? = null
    private var bodyTempMax: String? = null
    private var bodyTemp: String? = null

    private var parentActivityListener: ChildFragmentToActivityListener? = null

    private lateinit var homeActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            homeActivity = context
        }
        if (context is ChildFragmentToActivityListener) parentActivityListener = context
        else throw RuntimeException("$context must implement ChildFragmentToActivityListener")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentVitalStatsBinding =
            FragmentVitalStatsBinding.inflate(inflater, container, false)

        return fragmentVitalStatsBinding.root
    }

    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
//            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(
                HealthDataTypes.TYPE_BLOOD_PRESSURE,
                FitnessOptions.ACCESS_READ
            )
            /*.addDataType(
                HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY,
                FitnessOptions.ACCESS_READ
            )*/
            .addDataType(
                DataType.TYPE_HEART_RATE_BPM,
                FitnessOptions.ACCESS_READ
            )
            /* .addDataType(
                 DataType.AGGREGATE_HEART_RATE_SUMMARY,
                 FitnessOptions.ACCESS_READ
             )*/
            .addDataType(
                HealthDataTypes.TYPE_OXYGEN_SATURATION,
                FitnessOptions.ACCESS_READ
            )
            /* .addDataType(
                 HealthDataTypes.AGGREGATE_OXYGEN_SATURATION_SUMMARY,
                 FitnessOptions.ACCESS_READ
             )*/
            .addDataType(
                HealthDataTypes.TYPE_BODY_TEMPERATURE,
                FitnessOptions.ACCESS_READ
            )
            /* .addDataType(
                 HealthDataTypes.AGGREGATE_BODY_TEMPERATURE_SUMMARY,
                 FitnessOptions.ACCESS_READ
             )*/
            .build()
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

                        //Heart Rate
                        if (vitalStats!!.data?.heartRate.isNullOrEmpty()) {
                            fragmentVitalStatsBinding.tvHeartRateValue.text = "Not Available"
                            fragmentVitalStatsBinding.tvHeartRateUnit.visibility = View.GONE
                        } else {
                            fragmentVitalStatsBinding.tvHeartRateValue.text =
                                vitalStats!!.data?.heartRate
                            fragmentVitalStatsBinding.tvHeartRateUnit.visibility = View.VISIBLE
                        }

                        // Body temperature
                        if (vitalStats!!.data?.bodyTemp.isNullOrEmpty()) {
                            fragmentVitalStatsBinding.tvBodyTempValue.text = "Not Available"
                            fragmentVitalStatsBinding.tvBodyTempUnit.visibility = View.GONE
                        } else {
                            fragmentVitalStatsBinding.tvBodyTempValue.text =
                                vitalStats!!.data?.bodyTemp
                            fragmentVitalStatsBinding.tvBodyTempUnit.visibility = View.VISIBLE
                        }

                        // Blood Pressure
                        if (vitalStats!!.data?.bloodPressure?.sbp.isNullOrEmpty()) {
                            fragmentVitalStatsBinding.tvBloodPressureValue.text = "Not Available"
                        } else {
                            fragmentVitalStatsBinding.tvBloodPressureValue.text =
                                vitalStats!!.data?.bloodPressure?.sbp.plus("/${vitalStats!!.data?.bloodPressure?.dbp}")
                        }

                        // Oxygen Level
                        if (vitalStats!!.data?.oxygen.isNullOrEmpty()) {
                            fragmentVitalStatsBinding.tvOxygenValue.text = "Not Available"
                            fragmentVitalStatsBinding.tvOxygenUnit.visibility = View.GONE

                        } else {
                            fragmentVitalStatsBinding.tvOxygenUnit.visibility = View.VISIBLE
                            fragmentVitalStatsBinding.tvOxygenValue.text =
                                vitalStats!!.data?.oxygen
                        }
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
                    /*fragmentVitalStatsBinding.typeChart.setNoDataText("No Data Available")
                    fragmentVitalStatsBinding.typeChart.invalidate()*/

                    fragmentVitalStatsBinding.tvHeartRateValue.text = "Not Available"
                    fragmentVitalStatsBinding.tvHeartRateUnit.visibility = View.GONE

                    fragmentVitalStatsBinding.tvBodyTempValue.text = "Not Available"
                    fragmentVitalStatsBinding.tvBodyTempUnit.visibility = View.GONE

                    fragmentVitalStatsBinding.tvBloodPressureValue.text = "Not Available"

                    fragmentVitalStatsBinding.tvOxygenValue.text = "Not Available"
                    fragmentVitalStatsBinding.tvOxygenUnit.visibility = View.GONE


                    fragmentVitalStatsBinding.typeChart.clear()

                }
            }
        }


        vitalStatsViewModel.addVitalStatsLiveData.observeEvent(this) { addedStatsResult ->
            when (addedStatsResult) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    /* showSuccess(
                         requireContext(),
                         getString(R.string.vital_stats_added_successfully)
                     )*/

                    Log.d(TAG, "Vital Stats added successfully...")
//                    callGraphApi()
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
        fragmentVitalStatsBinding.typeChart.clear()

        fragmentVitalStatsBinding.dateSelectedTV.text =
            SimpleDateFormat("EEE, MMM dd").format(Calendar.getInstance().time)
        addType()


        fragmentVitalStatsBinding.typeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    /*if (typeList[p2].type!! == "blood_pressure") {
                        showError(requireContext(), "Not Implemented")
                    } else {
                        type = typeList[p2].type!!
                        callGraphApi()
                    }*/
                    type = typeList[p2].type!!
                    callGraphApi()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        // Read the data from google fit server only if the loggedIn user is loved one
        if (vitalStatsViewModel.isLoggedInUserLovedOne() == true) {
            Log.d(TAG, "Read Data from Google Fit as loggedIn user is lovedOne ")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val isPermissionGranted = (activity as BaseActivity).checkGoogleFitPermission()
                if (!isPermissionGranted) {
                    (activity as BaseActivity).requestGoogleFitPermission(this)
                } else {
                    fitSignIn(FitActionRequestCode.INSERT_AND_READ_DATA)
                }
            } else fitSignIn(FitActionRequestCode.INSERT_AND_READ_DATA)
        }
    }

    /**
     * Checks that the user is signed in, and if so, executes the specified function. If the user is
     * not signed in, initiates the sign in flow, specifying the post-sign in function to execute.
     *
     * @param requestCode The request code corresponding to the action to perform after sign in.
     */
    fun fitSignIn(requestCode: FitActionRequestCode) {
        if (oAuthPermissionsApproved()) {
            performActionForRequestCode(requestCode)
        } else {
            requestCode.let {
                GoogleSignIn.requestPermissions(
                    this,
                    requestCode.ordinal,
                    getGoogleAccount(), fitnessOptions
                )
            }
        }
    }

    /**
     * Runs the desired method, based on the specified request code. The request code is typically
     * passed to the Fit sign-in flow, and returned with the success callback. This allows the
     * caller to specify which method, post-sign-in, should be called.
     *
     * @param requestCode The code corresponding to the action to perform.
     */
    private fun performActionForRequestCode(requestCode: FitActionRequestCode) =
        when (requestCode) {
            FitActionRequestCode.INSERT_AND_READ_DATA -> readHistoryData()
            FitActionRequestCode.UPDATE_AND_READ_DATA -> readHistoryData()
            FitActionRequestCode.DELETE_DATA -> deleteData()
        }

    /**
     * Inserts and reads data by chaining {@link Task} from {@link #insertData()} and {@link
     * #readHistoryData()}.
     */
    private fun insertAndReadData() = insertData().continueWith { readHistoryData() }

    /**
     * Updates and reads data by chaining [Task] from [.updateData] and [ ][.readHistoryData].
     */
    private fun updateAndReadData() = updateData().continueWithTask { readHistoryData() }

    /**
     * Creates a [DataSet],then makes a [DataUpdateRequest] to update step data. Then
     * invokes the History API with the HistoryClient object and update request.
     */
    private fun updateData(): Task<Void> {
        // Create a new dataset and update request.
        val dataSet = updateFitnessData()
        val startTime = dataSet.dataPoints[0].getStartTime(TimeUnit.MILLISECONDS)
        val endTime = dataSet.dataPoints[0].getEndTime(TimeUnit.MILLISECONDS)
        // [START update_data_request]
        Log.i(TAG, "Updating the dataset in the History API.")

        val request = DataUpdateRequest.Builder()
            .setDataSet(dataSet)
            .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        // Invoke the History API to update data.
        return Fitness.getHistoryClient(requireContext(), getGoogleAccount())
            .updateData(request)
            .addOnSuccessListener { Log.i(TAG, "Data update was successful.") }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem updating the dataset.", e)
            }
    }

    /**
     * Deletes a [DataSet] from the History API. In this example, we delete all step count data
     * for the past 24 hours.
     */
    private fun deleteData() {
        Log.i(TAG, "Deleting today's step count data.")

        // [START delete_dataset]
        // Set a start and end time for our data, using a start time of 1 day before this moment.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = calendar.timeInMillis

        //  Create a delete request object, providing a data type and a time interval
        val request = DataDeleteRequest.Builder()
            .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
//            .addDataType(DataType.TYPE_HEART_RATE_BPM)
            .build()

        // Invoke the History API with the HistoryClient object and delete request, and then
        // specify a callback that will check the result.
        Fitness.getHistoryClient(requireContext(), getGoogleAccount())
            .deleteData(request)
            .addOnSuccessListener {
                Log.i(TAG, "Successfully deleted today's step count data.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to delete today's step count data.", e)
            }
    }

    /** Creates and returns a {@link DataSet} of step count data to update. */
    private fun updateFitnessData(): DataSet {
        Log.i(TAG, "Creating a new data update request.")

        // [START build_update_data_request]
        // Set a start and end time for the data that fits within the time range
        // of the original insertion.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.MINUTE, -50)
        val startTime = calendar.timeInMillis

        // Create a data source
        val dataSource = DataSource.Builder()
            .setAppPackageName(requireContext())
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
//            .setDataType(DataType.TYPE_HEART_RATE_BPM)
            .setStreamName("$TAG - step count")
            .setType(DataSource.TYPE_RAW)
            .build()

        // Create a data set
        val stepCountDelta = 1000
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        return DataSet.builder(dataSource)
            .add(
                DataPoint.builder(dataSource)
                    .setField(Field.FIELD_STEPS, stepCountDelta)
                    .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build()
            ).build()
        // [END build_update_data_request]
    }


    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the
     * data.
     */
    private fun readHistoryData(): Task<DataReadResponse> {
        // Begin by creating the query.
        val readRequest = queryFitnessData()

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(requireContext(), getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener { dataReadResponse ->
                // For the sake of the sample, we'll print the data so we can see what we just
                // added. In general, logging fitness information should be avoided for privacy
                // reasons.
//                printData(dataReadResponse)
//                parseData(dataReadResponse)
                parseVitalData(dataReadResponse)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem reading the data.", e)
            }
    }

    /**
     * Logs a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would
     * dump all the data. In this sample, logging also prints to the device screen, so we can see
     * what the query returns, but your app should not log fitness information as a privacy
     * consideration. A better option would be to dump the data you receive to a local data
     * directory to avoid exposing it to other applications.
     */
    private fun printData(dataReadResult: DataReadResponse) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.


        if (dataReadResult.buckets.isNotEmpty()) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
            for (bucket in dataReadResult.buckets) {
                bucket.dataSets.forEach { dumpDataSet(it) }
            }
        } else if (dataReadResult.dataSets.isNotEmpty()) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.dataSets.size)
            dataReadResult.dataSets.forEach { dumpDataSet(it) }
        }
        // [END parse_read_data_result]
    }

    private fun parseVitalData(dataReadResult: DataReadResponse) {
        dataReadResult.dataSets.forEach { dataSet ->
            dataSet.dataPoints.forEachIndexed { index, dataPoint ->
                val startTime = dataPoint.getStartTimeString()
                Log.d(TAG, "parseVitalData: StartTime is : $startTime")
                val endTime = dataPoint.getEndTimeString()
                Log.d(TAG, "parseVitalData: EndTime is : $endTime")

                val timeStamp = dataPoint.getTimestamp(TimeUnit.MILLISECONDS)
                Log.d(TAG, "parseVitalData: timestamp is $timeStamp")
                // Get Heart Rate data
                if (dataPoint.dataType.name == DataType.TYPE_HEART_RATE_BPM.name) {
                    heartRate = dataPoint.getValue(dataPoint.dataType.fields[index]).toString()
                    Log.d(TAG, "parseData1: heartRate is $heartRate")
                }

                // Get Blood Pressure
                if (dataPoint.dataType.name == HealthDataTypes.TYPE_BLOOD_PRESSURE.name) {
                    bloodPressureSys = dataPoint.getValue(FIELD_BLOOD_PRESSURE_SYSTOLIC).toString()
                    bloodPressureDia = dataPoint.getValue(FIELD_BLOOD_PRESSURE_DIASTOLIC).toString()
                    Log.d(TAG, "parseData1: bloodPressure Sys is $bloodPressureSys")
                    Log.d(TAG, "parseData1: bloodPressure Dia is $bloodPressureDia")
                }

                // Get Body Temperature
                if (dataPoint.dataType.name == HealthDataTypes.TYPE_BODY_TEMPERATURE.name) {
                    bodyTemp = dataPoint.getValue(dataPoint.dataType.fields[index]).toString()
                    Log.d(TAG, "parseData1: Body Temp is $bodyTemp")
                }

                // Get Oxygen Saturation
                if (dataPoint.dataType.name == HealthDataTypes.TYPE_OXYGEN_SATURATION.name) {
                    oxygenSaturation =
                        dataPoint.getValue(dataPoint.dataType.fields[index]).toString()
                    Log.d(TAG, "parseData1: oxygen saturation is $oxygenSaturation")
                }
            }
        }
        if (!bodyTemp.isNullOrEmpty()) {
            val bodyTempCelsius = bodyTemp?.toDouble()
            Log.d(TAG, "parseVitalData: Temp celsius : $bodyTempCelsius")
            if (bodyTempCelsius != null) {
                val bodyTemFahrenheit = bodyTempCelsius * 9 / 5 + 32
                Log.d(TAG, "parseVitalData: Temp fahrenheit : $bodyTemFahrenheit")
                bodyTemp = bodyTemFahrenheit.toString()
            }
        }

        Log.d(TAG, "parseVitalData: body temp :$bodyTemp")
        val data = AddVitalData(
            heartRate,
            AddBloodPressureData(
                bloodPressureSys,
                bloodPressureDia
            ),
            bodyTemp,
            oxygenSaturation
        )

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        println("currentDate: $currentDate")
        println("currentTime: $currentTime")
        val stats = VitalStatsRequestModel(
            vitalStatsViewModel.getLovedOneUUId(),
            currentDate,
            currentTime,
            data
        )
        vitalStatsViewModel.addVitalStats(stats)
    }


    private fun parseData(dataReadResult: DataReadResponse) {
        dataReadResult.buckets.forEach {
            // Get Heart Rate
            val heartRateDataSet = it.getDataSet(DataType.AGGREGATE_HEART_RATE_SUMMARY)
            heartRateDataSet?.dataPoints?.forEach { dataPoint ->
                heartRateAvg = dataPoint.getValue(Field.FIELD_AVERAGE).toString()
                heartRateMin = dataPoint.getValue(Field.FIELD_MIN).toString()
                heartRateMax = dataPoint.getValue(Field.FIELD_MAX).toString()

                Log.d(TAG, "parseData: HeartRate Avg : $heartRateAvg")
                Log.d(TAG, "parseData: HeartRate Min : $heartRateMin")
                Log.d(TAG, "parseData: HeartRate Max : $heartRateMax")
            }

            // Get Blood Pressure
            val bloodPressureDataSet =
                it.getDataSet(HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY)
            bloodPressureDataSet?.dataPoints?.forEach { dataPoint ->
                bloodPressureSysAvg =
                    dataPoint.getValue(FIELD_BLOOD_PRESSURE_SYSTOLIC_AVERAGE).toString()
                bloodPressureSysMin =
                    dataPoint.getValue(FIELD_BLOOD_PRESSURE_SYSTOLIC_MIN).toString()
                bloodPressureSysMax =
                    dataPoint.getValue(FIELD_BLOOD_PRESSURE_SYSTOLIC_MIN).toString()

                bloodPressureDiaAvg =
                    dataPoint.getValue(FIELD_BLOOD_PRESSURE_DIASTOLIC_AVERAGE).toString()
                bloodPressureDiaMin =
                    dataPoint.getValue(FIELD_BLOOD_PRESSURE_DIASTOLIC_MIN).toString()
                bloodPressureDiaMax =
                    dataPoint.getValue(FIELD_BLOOD_PRESSURE_DIASTOLIC_MAX).toString()

                Log.d(TAG, "parseData: BloodPressure Sys Avg : $bloodPressureSysAvg")
                Log.d(TAG, "parseData: BloodPressure Sys Min : $bloodPressureSysMin")
                Log.d(TAG, "parseData: BloodPressure Sys Max : $bloodPressureSysMax")
                Log.d(TAG, "parseData: BloodPressure Dia Avg : $bloodPressureDiaAvg")
                Log.d(TAG, "parseData: BloodPressure Dia Min : $bloodPressureDiaMin")
                Log.d(TAG, "parseData: BloodPressure Dia Max : $bloodPressureDiaMax")
            }

            // Get Oxygen Saturation
            val oxygenSaturationDataSet =
                it.getDataSet(HealthDataTypes.AGGREGATE_OXYGEN_SATURATION_SUMMARY)
            oxygenSaturationDataSet?.dataPoints?.forEach { dataPoint ->
                oxygenAvg =
                    dataPoint.getValue(HealthFields.FIELD_OXYGEN_SATURATION_AVERAGE).toString()
                oxygenMin = dataPoint.getValue(FIELD_OXYGEN_SATURATION_MIN).toString()
                oxygenMax = dataPoint.getValue(FIELD_OXYGEN_SATURATION_MAX).toString()

                Log.d(TAG, "parseData: Oxygen Saturation Avg : $oxygenAvg")
                Log.d(TAG, "parseData: Oxygen Saturation Min : $oxygenMin")
                Log.d(TAG, "parseData: Oxygen Saturation Max : $oxygenMax")
            }

            // Get Body Temperature
            val bodyTempDataSet = it.getDataSet(HealthDataTypes.AGGREGATE_BODY_TEMPERATURE_SUMMARY)
            bodyTempDataSet?.dataPoints?.forEach { dataPoint ->
                bodyTempAvg = dataPoint.getValue(Field.FIELD_AVERAGE).toString()
                bodyTempMin = dataPoint.getValue(Field.FIELD_MIN).toString()
                bodyTempMax = dataPoint.getValue(Field.FIELD_MAX).toString()

                Log.d(TAG, "parseData: Body Temp Avg : $bodyTempAvg")
                Log.d(TAG, "parseData: Body Temp Min : $bodyTempMin")
                Log.d(TAG, "parseData: Body Temp Max : $bodyTempMax")
            }

        }

    }

    // [START parse_dataset]
    private fun dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")

        for (dp in dataSet.dataPoints) {
            Log.i(TAG, "Data point:")
            Log.i(TAG, "\tType: ${dp.dataType.name}")
            Log.i(TAG, "\tStart: ${dp.getStartTimeString()}")
            Log.i(TAG, "\tEnd: ${dp.getEndTimeString()}")


            dp.dataType.fields.forEach {
                Log.i(TAG, "\tField: ${it.name} Value: ${dp.getValue(it)}")
            }
        }
    }
    // [END parse_dataset]

    /** Returns a [DataReadRequest] for all step count changes in the past week.  */
    private fun queryFitnessData(): DataReadRequest {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = calendar.timeInMillis

        Log.i(TAG, "Range Start: ${dateFormat.format(startTime)}")
        Log.i(TAG, "Range End: ${dateFormat.format(endTime)}")

        return DataReadRequest.Builder()
            // The data request can specify multiple data types to return, effectively
            // combining multiple data queries into one call.
            // In this example, it's very unlikely that the request is for several hundred
            // datapoints each consisting of a few steps and a timestamp.  The more likely
            // scenario is wanting to see how many steps were walked per day, for 7 days.
//            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .read(
                DataType.TYPE_HEART_RATE_BPM,
//                DataType.AGGREGATE_HEART_RATE_SUMMARY
            )
            .read(
                HealthDataTypes.TYPE_BLOOD_PRESSURE,
//                HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY
            )
            .read(
                HealthDataTypes.TYPE_BODY_TEMPERATURE,
//                HealthDataTypes.AGGREGATE_BODY_TEMPERATURE_SUMMARY
            )
            .read(
                HealthDataTypes.TYPE_OXYGEN_SATURATION,
//                HealthDataTypes.AGGREGATE_OXYGEN_SATURATION_SUMMARY
            )
            // Analogous to a "Group By" in SQL, defines how data should be aggregated.
            // bucketByTime allows for a time span, whereas bucketBySession would allow
            // bucketing by "sessions", which would need to be defined in code.
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//            .bucketByTime(1, TimeUnit.DAYS)
            .setLimit(1)
            .enableServerQueries()
            .build()
    }

    /** Creates a {@link DataSet} and inserts it into user's Google Fit history. */
    private fun insertData(): Task<Void> {
        // Create a new dataset and insertion request.
        val dataSet = insertFitnessData()

        // Then, invoke the History API to insert the data.
        Log.i(TAG, "Inserting the dataset in the History API.")
        return Fitness.getHistoryClient(requireContext(), getGoogleAccount())
            .insertData(dataSet)
            .addOnSuccessListener { Log.i(TAG, "Data insert was successful!") }
            .addOnFailureListener { exception ->
                Log.e(TAG, "There was a problem inserting the dataset.", exception)
            }
    }

    /**
     * Creates and returns a {@link DataSet} of step count data for insertion using the History API.
     */
    private fun insertFitnessData(): DataSet {
        Log.i(TAG, "Creating a new data insert request.")

        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.HOUR_OF_DAY, -1)
        val startTime = calendar.timeInMillis

        // Create a data source
        val dataSource = DataSource.Builder()
            .setAppPackageName(requireContext())
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setStreamName("$TAG - step count")
            .setType(DataSource.TYPE_RAW)
            .build()

        // Create a data set
        val stepCountDelta = 950
        return DataSet.builder(dataSource)
            .add(
                DataPoint.builder(dataSource)
                    .setField(Field.FIELD_STEPS, stepCountDelta)
                    .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build()
            ).build()
        // [END build_insert_data_request]
    }

    /**
     * Creates and returns a {@link DataSet} of step count data for insertion using the History API.
     */
    private fun insertBPData(): DataSet {
        Log.i(TAG, "Creating a new BP data insert request.")

        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.HOUR_OF_DAY, -1)
        val startTime = calendar.timeInMillis

        // Create a data source
        val dataSource = DataSource.Builder()
            .setAppPackageName(requireContext())
//            .setDataType(DataType.TYPE_HEART_RATE_BPM)
            .setDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE)
            .setStreamName("$TAG - blood pressure")
            .setType(DataSource.TYPE_RAW)
            .build()

        // Create a data set
        return DataSet.builder(dataSource)
            .add(
                DataPoint.builder(dataSource)
                    .setField(FIELD_BLOOD_PRESSURE_SYSTOLIC, 120.0f)
                    .setField(FIELD_BLOOD_PRESSURE_DIASTOLIC, 80.0f)
                    .setField(FIELD_BODY_POSITION, BODY_POSITION_SITTING)
                    .setField(
                        FIELD_BLOOD_PRESSURE_MEASUREMENT_LOCATION,
                        BLOOD_PRESSURE_MEASUREMENT_LOCATION_LEFT_UPPER_ARM
                    )
                    .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build()
            ).build()
        // [END build_insert_data_request]
    }

    private fun oAuthPermissionsApproved() =
        GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)

    /**
     * Gets a Google account for use in creating the Fitness client. This is achieved by either
     * using the last signed-in account, or if necessary, prompting the user to sign in.
     * `getAccountForExtension` is recommended over `getLastSignedInAccount` as the latter can
     * return `null` if there has been no sign in before.
     */
    private fun getGoogleAccount() =
        GoogleSignIn.getAccountForExtension(requireContext(), fitnessOptions)


    private fun callGraphApi() {

        val dateSelected = if (finalSelectedDate == null) Calendar.getInstance().time
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
        fragmentVitalStatsBinding.typeChart.setMaxVisibleValueCount(xAxisLabel.size)
        fragmentVitalStatsBinding.typeChart.setPinchZoom(false)
        fragmentVitalStatsBinding.typeChart.setDrawGridBackground(false)

        // Fixed the increased x-axis label issue when we load the graph on clicking filter type
        fragmentVitalStatsBinding.typeChart.setVisibleXRangeMinimum(8f)
        fragmentVitalStatsBinding.typeChart.setVisibleXRangeMaximum(8f)


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
        fragmentVitalStatsBinding.typeChart.isHighlightPerTapEnabled = true

        fragmentVitalStatsBinding.typeChart.resetTracking()
        fragmentVitalStatsBinding.typeChart.setDrawMarkers(true)
        fragmentVitalStatsBinding.typeChart.marker = markerView(requireContext())

        val values = ArrayList<CandleEntry>()
        values.clear()
        for (i in graphDataList.indices) {
            values.add(
                /*CandleEntry(
                    i.toFloat(),
                    if (graphDataList[i].x == 0)
                        (graphDataList[i].y - 10).toFloat()
                    else graphDataList[i].x.toFloat(),
                    graphDataList[i].y.toFloat(),
                    if (graphDataList[i].x == 0)
                        (graphDataList[i].y - 10).toFloat()
                    else graphDataList[i].x.toFloat(),
                    graphDataList[i].y.toFloat(),
                    0
                )*/
                // in the response x,x1 -> min & y,y1 ->max

                CandleEntry(
                    i.toFloat(),
                    // Shadow high
                    graphDataList[i].y.toFloat(),
                    // Shadow low
                    graphDataList[i].x.toFloat(),
                    //Open
                    graphDataList[i].x.toFloat(),
                    //Close
                    graphDataList[i].y.toFloat(),
                    0
                )
            )
        }
        Log.d(TAG, "setData: candleEntries : $values")

        xAxisLabel = ArrayList<String>()
        xAxisLabel.clear()
        for (i in graphDataList) {
            val time = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
            val dateTime = SimpleDateFormat("yyyy-MM-dd hh:mm a").parse(time.plus(" ${i.day}"))
            /* if (SimpleDateFormat("HH:mm").format(dateTime!!) != "00:00") {
                 xAxisLabel.add(SimpleDateFormat("HH:mm").format(dateTime))
             }*/
            xAxisLabel.add(SimpleDateFormat("HH:mm").format(dateTime!!))
        }

        Log.d(TAG, "setData: xAxisLabel :$xAxisLabel ")
        fragmentVitalStatsBinding.typeChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(xAxisLabel)

        xAxis.setLabelCount(10, false)
        // To fix the issue of getting duplicate values on X axis
        xAxis.isGranularityEnabled = true
//        xAxis.granularity = 2f
//        xAxis.spaceMin = 0.5f
//        xAxis.spaceMax = 0.5f

        val set1 = CandleDataSet(values, "")
        set1.setDrawIcons(false)
        set1.axisDependency = AxisDependency.LEFT
        set1.shadowColor = Color.WHITE
        set1.shadowWidth = 20f
        set1.decreasingColor = Color.rgb(159, 123, 179)
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = Color.rgb(159, 123, 179)
        set1.increasingPaintStyle = Paint.Style.FILL
        set1.neutralColor = Color.BLUE
        set1.highlightLineWidth = 0f
        set1.barSpace = 3f
        set1.valueTextColor = ContextCompat.getColor(requireContext(), R.color.transparent)
        set1.isHighlightEnabled = true
        set1.highLightColor = Color.RED
//        fragmentVitalStatsBinding.typeChart.setVisibleXRangeMaximum(24f)
        fragmentVitalStatsBinding.typeChart.setScaleEnabled(false)
        fragmentVitalStatsBinding.typeChart.zoom(3f, 0f, 3f, 0f)
        fragmentVitalStatsBinding.typeChart.axisLeft.setAxisMinValue(10f)
        fragmentVitalStatsBinding.typeChart.axisRight.setAxisMinValue(10f)
        Log.d(TAG, "setData: $set1")
        val data = CandleData(set1)
        fragmentVitalStatsBinding.typeChart.data = data
        fragmentVitalStatsBinding.typeChart.invalidate()

        fragmentVitalStatsBinding.typeChart.isHighlightPerTapEnabled = true

        // Chart Value Click
        fragmentVitalStatsBinding.typeChart.setOnChartValueSelectedListener(
            object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val x: Float = e!!.x
                    val y: Float = e.y
                    fragmentVitalStatsBinding.typeChart.highlightValue(h)

                    val layout =
                        LayoutInflater.from(context).inflate(R.layout.custom_marker_view, null)
                    val tv = layout.findViewById<TextView>(R.id.tvContent)
                    tv.textSize = 16.toFloat()
                    tv.setTextColor(resources.getColor(R.color.colorGreen))

                    tv.text = "x:$x y:$y"
                }

                override fun onNothingSelected() {
                }

            }
        )
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_vital_stats

    }

    override fun onResume() {
        parentActivityListener?.msgFromChildFragmentToActivity()
        super.onResume()
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


    private fun markerView(context: Context?): CustomMarkerView? {
        val mv = CustomMarkerView(context, R.layout.custom_marker_view, 16, Color.RED)
        mv.setOffset((-mv.width / 2).toFloat(), (-mv.height - 25).toFloat())
        return mv
    }
}