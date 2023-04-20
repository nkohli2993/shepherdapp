package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.med_list.add_med_list.AddMedListRequestModel
import com.shepherdapp.app.data.dto.med_list.add_med_list.AddedMedlistResponseModel
import com.shepherdapp.app.data.dto.medical_conditions.AddMedicalConditionRequestModel
import com.shepherdapp.app.data.dto.medical_conditions.AddedUserMedicalConditionResposneModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.med_list.MedListRepository
import com.shepherdapp.app.data.remote.medical_conditions.MedicalConditionRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.view_model.AddLovedOneConditionViewModel
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
class AddMedicineUnitTest {


    private var context: Context? = null

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    var medicalConditionData = AddMedListRequestModel(
        condition = "Zemplar Oral, Parenteral",
        description = "Vitamins are compounds that you must have for growth and health. They are needed in small amounts only and are available in the foods that you eat. Vitamin D is necessary for strong bones and teeth.",
        "user"
    )

    lateinit var medicineRepository: MedListRepository
    lateinit var userRepository: UserRepository
    @Mock
    private lateinit var medicalConditionViewModel: AddMedicationViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<AddedMedlistResponseModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        context = Mockito.mock(Context::class.java)

        medicineRepository =
            TestRepositoryProvider().getMedListRepository(context!!)
        userRepository =
            TestRepositoryProvider().getUserRepository(context!!)

        medicalConditionViewModel = AddMedicationViewModel(medicineRepository,userRepository)

        medicalConditionViewModel.addMedicineResponseLiveData.observeForever(observer)

    }

    @Test
    fun testNameValuesValidate() {
        Assert.assertEquals(false, medicalConditionData.condition?.isNullOrEmpty())
    }

    @Test
    fun `test add medicine data`() = runBlockingTest {
        medicalConditionViewModel.addNewMedlistMedicine(medicalConditionData)

        Thread.sleep(3000)
        medicalConditionViewModel.addMedicineResponseLiveData.getOrAwaitValueTest()
            .getContentIfNotHandled().let {
                if (it is DataResult.Success) {
                    Assert.assertEquals("Created successfully", it.data.message)
                }
            }

    }

}