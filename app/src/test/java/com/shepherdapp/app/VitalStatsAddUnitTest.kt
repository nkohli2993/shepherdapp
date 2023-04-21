package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.add_vital_stats.AddVitalStatsResponseModel
import com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats.AddBloodPressureData
import com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats.AddVitalData
import com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.vital_stats.VitalStatsRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
import com.shepherdapp.app.view_model.AddNewVitalStatsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)

class VitalStatsAddUnitTest {


    private var context: Context? = null

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    var addVitalData =
        VitalStatsRequestModel(
            loveone_user_id = "6299cd52-5df2-40bf-a9c2-7480e559b9be",
            date = "2023-04-19",
            time = null,
            data = AddVitalData(
                heartRate = "100", bodyTemp = "102", oxygen = "98",
                bloodPressure = AddBloodPressureData(sbp = "78", dbp = "120")
            )
        )

    lateinit var vitalStatstRepository: VitalStatsRepository
    lateinit var userRepository: UserRepository

    @Mock
    private lateinit var vitalStatsViewModel: AddNewVitalStatsViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<AddVitalStatsResponseModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        context = Mockito.mock(Context::class.java)

        vitalStatstRepository =
            TestRepositoryProvider().getVitalStatsRepository(context!!)
        userRepository =
            TestRepositoryProvider().getUserRepository(context!!)

        vitalStatsViewModel = AddNewVitalStatsViewModel(vitalStatstRepository, userRepository)

        vitalStatsViewModel.addVitatStatsLiveData.observeForever(observer)

    }


    @Test
    fun testCheckValieValidate() {
        val valueCheck =
            addVitalData.data!!.heartRate.isNullOrEmpty() && addVitalData.data!!.bodyTemp.isNullOrEmpty() &&
                    addVitalData.data!!.oxygen.isNullOrEmpty() && addVitalData.data!!.bloodPressure!!.sbp.isNullOrEmpty() && addVitalData.data!!.bloodPressure!!.dbp.isNullOrEmpty()
        Assert.assertEquals(true, valueCheck)
    }

    @Test
    fun `test scheduled medication data`() = runBlockingTest {
        vitalStatsViewModel.addVitalStats(addVitalData)

        Thread.sleep(3000)
        vitalStatsViewModel.addVitatStatsLiveData.getOrAwaitValueTest()
            .getContentIfNotHandled().let {
                if (it is DataResult.Success) {
                    Assert.assertEquals("Updated successfully!", it.data.message)
                }
            }

    }

}