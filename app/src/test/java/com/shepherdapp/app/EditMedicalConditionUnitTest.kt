package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.medical_conditions.AddMedicalConditionRequestModel
import com.shepherdapp.app.data.dto.medical_conditions.AddedUserMedicalConditionResposneModel
import com.shepherdapp.app.data.dto.medical_conditions.edit_medical_conditions.EditMedicalConditionsResponseModel
import com.shepherdapp.app.data.remote.medical_conditions.MedicalConditionRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
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
class EditMedicalConditionUnitTest {


    private var context: Context? = null

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    var editMedicalConditionData = AddMedicalConditionRequestModel(
        name = "Meningitis",
        description = "Meningococcal Disease and MRSA (Pet Owners) Molluscum Contagiosum.",
        createdBy = null
    )

    lateinit var medicalConditionRepository: MedicalConditionRepository

    @Mock
    private lateinit var medicalConditionViewModel: AddLovedOneConditionViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<EditMedicalConditionsResponseModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        context = Mockito.mock(Context::class.java)

        medicalConditionRepository =
            TestRepositoryProvider().getMedicalConditionRepository(context!!)

        medicalConditionViewModel = AddLovedOneConditionViewModel(medicalConditionRepository)

        medicalConditionViewModel.editConditionResponseLiveData.observeForever(observer)

    }

    @Test
    fun testNameValuesValidate() {
        Assert.assertEquals(false, editMedicalConditionData.name?.isNullOrEmpty())
    }


    @Test
    fun `test edit medical condition data`() = runBlockingTest {
        medicalConditionViewModel.editMedicalCondition(editMedicalConditionData,63)

        Thread.sleep(3000)
        medicalConditionViewModel.editConditionResponseLiveData.getOrAwaitValueTest()
            .getContentIfNotHandled().let {
                when (it) {
                    is DataResult.Success -> {
                        Assert.assertEquals("Condition updated successfully !", it.data.message)
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