package com.shepherd.app

import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.shepherdapp.app.ui.component.login.LoginActivity
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class LoginUnitTest {
    @get:Rule
    @JvmField
    public var mActivityTestRule = ActivityTestRule(
        LoginActivity::class.java,false,true
    )
    var loginActivity: LoginActivity? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        loginActivity = mActivityTestRule.activity
    }

    @Before
    fun onLaunch() {
        loginActivity = mActivityTestRule.activity
    }


    @Test
    fun readStringFromContext_LocalizedString() {
        getInstrumentation().runOnMainSync {
            val result: String = loginActivity?.validate("user", "user") ?: "Invalid login!"
            ViewMatchers.assertThat(result, Matchers.`is`(LOGIN_STRING))
        }
    }

    @Test
    fun testEmailValuesValidate() {
        getInstrumentation().runOnMainSync {
            val responseOfExecutingYourApiWithCorrectValues: Boolean =
                loginActivity?.getEmailValid() ?: false
            Assert.assertEquals(true, responseOfExecutingYourApiWithCorrectValues);
        }
    }

    @Test
    fun testPasswordValuesValidate() {
        getInstrumentation().runOnMainSync {
            val responseOfExecutingYourApiWithCorrectValues: Boolean =
                loginActivity?.getPasswordValid() ?: false
            Assert.assertEquals(true, responseOfExecutingYourApiWithCorrectValues);
        }
    }

    companion object {
        private const val LOGIN_STRING = "Invalid login!"
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        loginActivity = null
    }
}