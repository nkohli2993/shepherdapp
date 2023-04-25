package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.edit_event.EditEventRequestModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.care_point.CarePointRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.component.addNewEvent.CreateEventResponseModel
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
import com.shepherdapp.app.view_model.EditEventViewModel
import kotlinx.coroutines.Dispatchers
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
class EditCarePointUnitTest {


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private var editCareEventModel = EditEventRequestModel(
        name = "Testing event Test case",
        location = "Sahibzada Ajit Singh Nagar, Punjab, India",
        date = "2023-04-27",
        time = "15:00",
        notes = "Event",
        deletedAssignee = arrayListOf(
            "d2d2e458-e726-4134-9b48-cdcd7b6a6fb0",
        ),
        newAssignee = arrayListOf(
            "4b9e9dca-39cc-49fb-bfd7-ab5a66693d73"
        )
    )

    lateinit var dataRepository: DataRepository
    lateinit var userRepository: UserRepository
    lateinit var carePointRepository: CarePointRepository

    @Mock
    private lateinit var editCarePointViewModel: EditEventViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<CreateEventResponseModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        val context = Mockito.mock(Context::class.java)

        dataRepository = TestRepositoryProvider().getDataRepository(context!!, Dispatchers.Default)
        userRepository = TestRepositoryProvider().getUserRepository(context)
        carePointRepository = TestRepositoryProvider().getCarePointRepository(context)
        editCarePointViewModel = EditEventViewModel(dataRepository, userRepository,carePointRepository)

        editCarePointViewModel.createEventLiveData.observeForever(observer)

    }

    @Test
    fun testNameValidate() {
        Assert.assertEquals(false, editCareEventModel.name?.isEmpty())
    }

    @Test
    fun testLocationValidate() {
        Assert.assertEquals(false, editCareEventModel.location?.isEmpty())
    }

    @Test
    fun testAssignedValuesValidate() {
        Assert.assertEquals(true, editCareEventModel.newAssignee?.size!! > 0)
    }

    @Test
    fun `test edit care point data success`() = runBlockingTest {

        editCarePointViewModel.editCarePoint(editCareEventModel,151)

        Thread.sleep(3000)

        editCarePointViewModel.createEventLiveData.getOrAwaitValueTest().getContentIfNotHandled()
            .let {
                when (it) {
                    is DataResult.Success -> {
                        Assert.assertEquals(
                            "CarePoint Updated Successfully!",
                            it.data.message
                        )
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
