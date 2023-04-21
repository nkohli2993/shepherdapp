package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.EmailValidator
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
import com.shepherdapp.app.view_model.ForgotPasswordViewModel
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
class ForgotPasswordUserUnitTest {


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    var email = "shamikumar@yopmail.com"

    lateinit var authRepository: AuthRepository
    lateinit var userRepository: UserRepository

    @Mock
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

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
        forgotPasswordViewModel = ForgotPasswordViewModel(authRepository)

        forgotPasswordViewModel.forgotPasswordResponseLiveData.observeForever(observer)

    }


    @Test
    fun testEmailValuesValidate() {
        assertEquals(true, EmailValidator.get().isValidEmail(email))
    }


    @Test
    fun `test User data success`() = runBlockingTest {

        forgotPasswordViewModel.forgotPassword(email)

        Thread.sleep(3000)

        forgotPasswordViewModel.forgotPasswordResponseLiveData.getOrAwaitValueTest().getContentIfNotHandled()
            .let {
                when (it) {
                    is DataResult.Success -> {
                        assertEquals(
                            "Reset Password link has been sent to your email",
                            it.data.message
                        )
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

