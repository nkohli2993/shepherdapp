package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.medical_conditions.AddMedicalConditionRequestModel
import com.shepherdapp.app.data.dto.medical_conditions.AddedUserMedicalConditionResposneModel
import com.shepherdapp.app.data.remote.medical_conditions.MedicalConditionRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.view_model.AddLovedOneConditionViewModel
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
class AddMedicalConditionUnitTest {


    private var context: Context? = null

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    var medicalConditionData = AddMedicalConditionRequestModel(
        name = "Meningitis",
        description = "Meningococcal Disease. MRSA (Pet Owners) Molluscum Contagiosum",
        "user"
    )

    lateinit var medicalConditionRepository: MedicalConditionRepository

    @Mock
    private lateinit var medicalConditionViewModel: AddLovedOneConditionViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<AddedUserMedicalConditionResposneModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        context = Mockito.mock(Context::class.java)

        medicalConditionRepository =
            TestRepositoryProvider().getMedicalConditionRepository(context!!)

        medicalConditionViewModel = AddLovedOneConditionViewModel(medicalConditionRepository)

        medicalConditionViewModel.addedConditionsResponseLiveData.observeForever(observer)

    }


    @Test
    fun testNameValuesValidate() {
        Assert.assertEquals(false, medicalConditionData.name?.isNullOrEmpty())
    }

    @Test
    fun `test add medical condition data`() = runBlockingTest {
        medicalConditionViewModel.addMedicalConditions(medicalConditionData)

        Thread.sleep(3000)
        medicalConditionViewModel.addedConditionsResponseLiveData.getOrAwaitValueTest()
            .getContentIfNotHandled().let {
            if (it is DataResult.Success) {
                Assert.assertEquals("Created successfully", it.data.message)
            }
        }

    }

}