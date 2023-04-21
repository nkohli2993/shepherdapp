package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.dto.signup.UserSignupData
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.EmailValidator
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
import com.shepherdapp.app.view_model.CreateNewAccountViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(MockitoJUnitRunner::class)
class RegisterUserUnitTest {


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    var userSignupData = UserSignupData(
        email = "shamikumar@yopmail.com", "1234", firstname = "Sumit", lastname = "Kumar",
        phoneCode = "91", phoneNo = "95018759813", roleId = "2"
    )

    lateinit var authRepository: AuthRepository
    lateinit var userRepository: UserRepository

    @Mock
    private lateinit var createNewAccountViewModel: CreateNewAccountViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<LoginResponseModel>>>


    companion object {
        private const val USER_JSON = "/json/user.json"
    }


    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        val context = Mockito.mock(Context::class.java)

        authRepository = TestRepositoryProvider().getAuthRepository(context!!)
        userRepository = TestRepositoryProvider().getUserRepository(context!!)
        createNewAccountViewModel = CreateNewAccountViewModel(authRepository, userRepository)

        createNewAccountViewModel.signUpLiveData.observeForever(observer)

    }

    @Test
    fun testFirstNameValuesValidate() {
        assertEquals(false, userSignupData.firstname?.isNullOrEmpty())
    }

    @Test
    fun testLastNameValuesValidate() {
        assertEquals(false, userSignupData.lastname?.isNullOrEmpty())
    }

    @Test
    fun testEmailValuesValidate() {
        assertEquals(true, EmailValidator.get().isValidEmail(userSignupData.email))
    }

    @Test
    fun testPhoneNumberValidate() {
        assertEquals(false, userSignupData.phoneNo?.isNullOrEmpty())
    }

    @Test
    fun testPasswordValuesValidate() {
        val responseOfExecutingYourApiWithCorrectValues: Boolean =
            createNewAccountViewModel.getPasswordValid(userSignupData)
        assertEquals(true, responseOfExecutingYourApiWithCorrectValues)

    }


    @Test
    fun `test User data success`() = runBlockingTest {

        createNewAccountViewModel.createAccount(
            userSignupData.phoneCode,
            userSignupData.profilePhoto,
            userSignupData.firstname,
            userSignupData.lastname,
            userSignupData.email,
            userSignupData.password,
            userSignupData.phoneNo,
            userSignupData.roleId,
            userSignupData.enterprise_code
        )

        Thread.sleep(3000)

        createNewAccountViewModel.signUpLiveData.getOrAwaitValueTest().getContentIfNotHandled()
            .let {
                when (it) {
                    is DataResult.Success -> {
                        assertEquals("Verify your registered email by clicking on email verification link sent to your email account", it.data.message)
                    }

                    is DataResult.Failure -> {
                        assertFalse(true)
                    }

                    else -> {
                        print("Loading")
                    }
                }
            }

    }
}

