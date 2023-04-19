package com.shepherdapp.app

import android.content.Context
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.dto.login.Payload
import com.shepherdapp.app.data.dto.signup.UserSignupData
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.EmailValidator
import com.shepherdapp.app.view_model.LoginViewModel
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.Shadows
import java.lang.String


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


    var userSignupData = UserSignupData(email = "car@yopmail", "1234")

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


/*        runBlocking {
            val result = MutableLiveData<Event<DataResult<LoginResponseModel>>>()
            given(loginViewModel.login(UserSignupData(email = "car@yopmail.com", "12345"), false)).willReturn(result)

            val expectedResult = DataResult.Success(LoginResponseModel())
            val realResult = DataResult.Success(UserSignupData())

            assertEquals(expectedResult,realResult)


        }*/

     //   val realResult: LoginResponseModel = JsonProvider.objectFromJsonFileWithType(USER_JSON)


        loginViewModel.login(userSignupData,false)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val value = Event(loginViewModel.loginResponseLiveData.value)
        Assert.assertTrue(value is DataResult.Success)
        Assert.assertNotNull(value.getContentIfNotHandled().let {
            
        })
        Assert.assertEquals(7834983, value.getContentIfNotHandled?.getContentIfNotHandled())
        Assert.assertEquals("Ravish Rajput", value.data?.name)


/*        loginViewModel.login(userSignupData, false)

        advanceUntilIdle()

        val value = loginViewModel.loginResponseLiveData.getOrAwaitValueTest()


        val data = Event(DataResult.Loading(null))
       // val data = Event(DataResult.Success(realResult))

        assertEquals(value, data)*/





/*        loginViewModel.loginResponseLiveData.observeForever(observer)
        Mockito.verify(observer).onChanged(
            Event(
                DataResult.Success(
                    LoginResponseModel(
                        payload = Payload(email = "car@yopmail.com")
                    )
                )
            )
        )
        loginViewModel.loginResponseLiveData.removeObserver(observer)*/

        // assertEquals(loginViewModel.loginResponseLiveDataTest.getOrAwaitValue(), DataResult.Loading(null))
        //  assertEquals(loginViewModel.loginResponseLiveDataTest.getOrAwaitValue(), realResult)
/*
        loginViewModel.loginResponseLiveData.observeForever {
            it.let {
                val value = it?.getContentIfNotHandled()
                if (value is DataResult.Success) {
                    assertTrue(value.data.payload?.email?.isNotEmpty() == true)
                } else if (value is DataResult.Failure){
                    assertTrue(value.message.isNullOrEmpty())
                }
            }


        }
*/



/*
    @Test
    fun validatePosts() = runBlocking {
        userRepository.login(UserSignupData(email = "car@yopmail.com", password = "1234"), false)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val value = loginViewModel.login().value

*/
/*
        authRepository.login(UserSignupData(email = "car@yopmail.com", password = "1234"), false)
            .collect {
            val fakeResponse: LoginResponseModel =
                JsonProvider.objectFromJsonFileWithType(USER_JSON)

            var email = fakeResponse.payload?.email
            it.let {
                if (it is DataResult.Success) {
                    email = it.data.payload?.email!!
                }
            }
            assertTrue(email?.isNotEmpty() == true)
        }
*//*

    }
*/
    }
}

/*
@Test
fun validateGetPostById() = runBlocking {
    postRepository.getPost("1").catch { e ->
        assertTrue(false)
    }.collect {
        assertNotNull(it)
    }
}
*/
