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
import com.shepherdapp.app.view_model.LoginViewModel
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
class LoginUnitTest {


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    var userSignupData = UserSignupData(email = "car@yopmail.com", "1234")

    lateinit var authRepository: AuthRepository
    lateinit var userRepository: UserRepository

    @Mock
    private lateinit var loginViewModel: LoginViewModel

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
        loginViewModel = LoginViewModel(authRepository, userRepository)

        loginViewModel.loginResponseLiveData.observeForever(observer)

    }


    @Test
    fun testEmailValuesValidate() {
        assertEquals(true, EmailValidator.get().isValidEmail(userSignupData.email))
    }

    @Test
    fun testPasswordValuesValidate() {
        val responseOfExecutingYourApiWithCorrectValues: Boolean =
            loginViewModel.getPasswordValid(userSignupData)
        assertEquals(true, responseOfExecutingYourApiWithCorrectValues)

    }


    @Test
    fun `test User data success`() = runBlockingTest {
        val email = "cars@yopmail.com"

        loginViewModel.login(userSignupData, false)


        Thread.sleep(3000)
        loginViewModel.loginResponseLiveData.getOrAwaitValueTest().getContentIfNotHandled().let {
            if (it is DataResult.Success) {
                assertEquals(email, it.data.payload?.email)
            }
        }

    }

}