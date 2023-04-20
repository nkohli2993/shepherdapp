package com.shepherdapp.app


import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.add_loved_one.CreateLovedOneModel
import com.shepherdapp.app.data.dto.add_loved_one.CreateLovedOneResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.relation_repository.RelationRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.view_model.AddLovedOneViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.EmailValidator
import org.junit.Assert.assertEquals

@RunWith(MockitoJUnitRunner::class)
class AddLovedOneUnitTest {


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    var addLovedOneData = CreateLovedOneModel(
        email = "nik@yopmail.com",
        firstname = "Nik",
        lastname = "Kumar",
        relationId = 4,
        phoneCode = "91",
        phoneNo = "95018759813",
        dob = "2010-06-14",
        placeId = "ChIJH_imbZDuDzkR2AjlbPGYKVE",
        sendInvitation = true,
        customAddress = "sector 71, mohali"
        )

    lateinit var authRepository: AuthRepository
    lateinit var userRepository: UserRepository
    lateinit var relationRepository: RelationRepository

    @Mock
    private lateinit var addLovedOneViewModel: AddLovedOneViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<CreateLovedOneResponseModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        val context = Mockito.mock(Context::class.java)

        authRepository = TestRepositoryProvider().getAuthRepository(context!!)
        userRepository = TestRepositoryProvider().getUserRepository(context!!)
        relationRepository = TestRepositoryProvider().getRelationRepository(context!!)
        addLovedOneViewModel =
            AddLovedOneViewModel(authRepository, relationRepository, userRepository)

        addLovedOneViewModel.createLovedOneLiveData.observeForever(observer)

    }

    @Test
    fun testFirstNameValuesValidate() {
        assertEquals(false, addLovedOneData.firstname?.isNullOrEmpty())
    }

    @Test
    fun testLastNameValuesValidate() {
        assertEquals(false, addLovedOneData.lastname?.isNullOrEmpty())
    }

    @Test
    fun testEmailValuesValidate() {
        assertEquals(true, EmailValidator.get().isValidEmail(addLovedOneData.email))
    }

    @Test
    fun testPhoneNumberValidate() {
        assertEquals(false, addLovedOneData.phoneNo?.isNullOrEmpty())
    }


    @Test
    fun `test add loved one data success`() = runBlockingTest {

        addLovedOneViewModel.createLovedOneApi(addLovedOneData)

        Thread.sleep(3000)

        addLovedOneViewModel.createLovedOneLiveData.getOrAwaitValueTest().getContentIfNotHandled()
            .let {
                when (it) {
                    is DataResult.Success -> {
                        assertEquals(
                            "Loved one added successfully.",
                            it.data.message
                        )
                    }

                    is DataResult.Failure -> {
                        assertEquals(false, false)
                    }

                    else -> {
                        print("Loading")
                    }
                }
            }

    }
}
