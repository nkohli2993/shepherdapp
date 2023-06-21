package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.component.addNewEvent.CreateEventModel
import com.shepherdapp.app.ui.component.addNewEvent.CreateEventResponseModel
import com.shepherdapp.app.utils.EmailValidator
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
import com.shepherdapp.app.view_model.AddNewEventViewModel
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
class AddCarePointUnitTest {


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private var createEventModel = CreateEventModel(
        loved_one_user_id = "6299cd52-5df2-40bf-a9c2-7480e559b9be",
        name = "Testing event Test case",
        location = "Sahibzada Ajit Singh Nagar, Punjab, India",
        date = "2023-04-25",
        time = "18:00",
        notes = "Event",
        assign_to = arrayListOf(
            "d2d2e458-e726-4134-9b48-cdcd7b6a6fb0",
            "4b9e9dca-39cc-49fb-bfd7-ab5a66693d73"
        )
    )

    lateinit var dataRepository: DataRepository
    lateinit var userRepository: UserRepository

    @Mock
    private lateinit var addCarePointViewModel: AddNewEventViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<CreateEventResponseModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        val context = Mockito.mock(Context::class.java)

        dataRepository = TestRepositoryProvider().getDataRepository(context!!, Dispatchers.Default)
        userRepository = TestRepositoryProvider().getUserRepository(context!!)
        addCarePointViewModel = AddNewEventViewModel(dataRepository, userRepository)

        addCarePointViewModel.createEventLiveData.observeForever(observer)

    }

    @Test
    fun testNameValidate() {
        Assert.assertEquals(false, createEventModel.name?.isNullOrEmpty())
    }

    @Test
    fun testLocationValidate() {
        Assert.assertEquals(false, createEventModel.location?.isNullOrEmpty())
    }

    @Test
    fun testAssignedValuesValidate() {
        Assert.assertEquals(true, createEventModel.assign_to?.size!! > 0)
    }

    @Test
    fun `test add care point data success`() = runBlockingTest {

        addCarePointViewModel.createEvent(
            createEventModel.loved_one_user_id,
            createEventModel.name!!,
            createEventModel.location!!,
            createEventModel.date!!,
            createEventModel.time!!,
            createEventModel.notes!!,
            createEventModel.assign_to!!,null,null,null,null,null
        )

        Thread.sleep(3000)

        addCarePointViewModel.createEventLiveData.getOrAwaitValueTest().getContentIfNotHandled()
            .let {
                when (it) {
                    is DataResult.Success -> {
                        Assert.assertEquals(
                            "Loved one added successfully.",
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
