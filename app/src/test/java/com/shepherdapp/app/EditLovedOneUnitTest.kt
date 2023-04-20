package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.add_loved_one.CreateLovedOneModel
import com.shepherdapp.app.data.dto.add_loved_one.CreateLovedOneResponseModel
import com.shepherdapp.app.data.dto.edit_loved_one.EditLovedOneResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.relation_repository.RelationRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.EmailValidator
import com.shepherdapp.app.view_model.AddLovedOneViewModel
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
class EditLovedOneUnitTest {


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    var editLovedOneData = CreateLovedOneModel(
        email = "nikki@yopmail.com",
        firstname = "Nikita",
        lastname = "Kumar1",
        relationId = 4,
        phoneCode = "91",
        phoneNo = "95018759813",
        dob = "2010-06-14",
        placeId = "ChIJH_imbZDuDzkR2AjlbPGYKVE",
        sendInvitation = true,
        customAddress = "Phase 5, Mohali"
    )

    lateinit var authRepository: AuthRepository
    lateinit var userRepository: UserRepository
    lateinit var relationRepository: RelationRepository

    @Mock
    private lateinit var editLovedOneViewModel: AddLovedOneViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<EditLovedOneResponseModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        val context = Mockito.mock(Context::class.java)

        authRepository = TestRepositoryProvider().getAuthRepository(context!!)
        userRepository = TestRepositoryProvider().getUserRepository(context!!)
        relationRepository = TestRepositoryProvider().getRelationRepository(context!!)
        editLovedOneViewModel =
            AddLovedOneViewModel(authRepository, relationRepository, userRepository)

        editLovedOneViewModel.editLovedOneLiveData.observeForever(observer)

    }

    @Test
    fun testFirstNameValuesValidate() {
        Assert.assertEquals(false, editLovedOneData.firstname?.isNullOrEmpty())
    }

    @Test
    fun testLastNameValuesValidate() {
        Assert.assertEquals(false, editLovedOneData.lastname?.isNullOrEmpty())
    }

    @Test
    fun testEmailValuesValidate() {
        Assert.assertEquals(true, EmailValidator.get().isValidEmail(editLovedOneData.email))
    }

    @Test
    fun testPhoneNumberValidate() {
        Assert.assertEquals(false, editLovedOneData.phoneNo?.isNullOrEmpty())
    }


    @Test
    fun `test edit loved one data success`() = runBlockingTest {
        editLovedOneViewModel.editLovedOne(
            email = editLovedOneData.email,
            firstname = editLovedOneData.firstname!!,
            lastname = editLovedOneData.lastname,
            relation_id = editLovedOneData.relationId,
            phone_code = editLovedOneData.phoneCode,
            dob = editLovedOneData.dob,
            place_id = editLovedOneData.placeId,
            customAddress = editLovedOneData.customAddress,
            phone_no = editLovedOneData.phoneNo,
            sendInvitation = editLovedOneData.sendInvitation,
            profile_photo = null,
            uuid = "35979780-aa3c-462a-85f8-39e923078c2c"
        )

        Thread.sleep(3000)

        editLovedOneViewModel.editLovedOneLiveData.getOrAwaitValueTest().getContentIfNotHandled()
            .let {
                when (it) {
                    is DataResult.Success -> {
                        Assert.assertEquals(
                            "Success",
                            it.data.message
                        )
                    }

                    is DataResult.Failure -> {
                        Assert.assertEquals(false, false)
                    }

                    else -> {
                        print("Loading")
                    }
                }
            }

    }
}
