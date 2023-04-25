package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.med_list.AddScheduledMedicationResponseModel
import com.shepherdapp.app.data.dto.med_list.ScheduledMedicationRequestModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.med_list.MedListRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
import com.shepherdapp.app.view_model.AddMedicationViewModel
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
class EditScheduledMedicationUnitTest {


    private var context: Context? = null

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    var medicalConditionData =
        ScheduledMedicationRequestModel(
            dosage_id = "3",
            love_user_id = "6299cd52-5df2-40bf-a9c2-7480e559b9be",
            medlist_id = "161",
            note = "Take 3 medicine on time",
            time = null
        )

    lateinit var medListRepository: MedListRepository
    lateinit var userRepository: UserRepository

    @Mock
    private lateinit var scheduledMedicationViewModel: AddMedicationViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<AddScheduledMedicationResponseModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        context = Mockito.mock(Context::class.java)

        medListRepository =
            TestRepositoryProvider().getMedListRepository(context!!)
        userRepository =
            TestRepositoryProvider().getUserRepository(context!!)

        scheduledMedicationViewModel = AddMedicationViewModel(medListRepository, userRepository)

        scheduledMedicationViewModel.addScheduledMedicationResponseLiveData.observeForever(observer)

    }


    @Test
    fun testDosageValuesValidate() {
        Assert.assertEquals(false, medicalConditionData.dosage_id?.isNullOrEmpty())
    }

    @Test
    fun `test scheduled medication data`() = runBlockingTest {
        scheduledMedicationViewModel.addScheduledMedication(medicalConditionData)

        Thread.sleep(3000)
        scheduledMedicationViewModel.addScheduledMedicationResponseLiveData.getOrAwaitValueTest()
            .getContentIfNotHandled().let {
                when (it) {
                    is DataResult.Success -> {
                        Assert.assertEquals("Created successfully", it.data.message)
                    }
                    is DataResult.Failure -> {
                        Assert.assertEquals(true, false)
                    }
                    else -> {
                        print("Loading")
                    }
                }
            }

    }

}