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

    companion object {
        private const val LOGIN_STRING = "Invalid login!"
    }







    @After
    @Throws(Exception::class)
    fun tearDown() {
        loginActivity = null
    }
}