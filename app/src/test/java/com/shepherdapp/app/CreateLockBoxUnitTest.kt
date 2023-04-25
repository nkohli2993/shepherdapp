package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.AddNewLockBoxRequestModel
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.AddNewLockBoxResponseModel
import com.shepherdapp.app.data.dto.lock_box.edit_lock_box.EditLockBoxRequestModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.lock_box.LockBoxRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
import com.shepherdapp.app.view_model.AddNewLockBoxViewModel
import com.shepherdapp.app.view_model.LockBoxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CreateLockBoxUnitTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    var allowedUser: ArrayList<String> = arrayListOf("36e91f3b-c4a4-40d3-9d70-58654d2df553")

    private var lockBoxData = AddNewLockBoxRequestModel(
        "Test lockBox updated",
        "Description for lockBox",
        lbtId = 8,
        allowedUserIds = allowedUser,
        loveUserId = "cc502713-0443-4bdb-bab6-92fd1a28c82c",
        documents = null
    )

    lateinit var lockBoxRepository: LockBoxRepository
    lateinit var userRepository: UserRepository

    @Mock
    private lateinit var addLockBoxViewModel: AddNewLockBoxViewModel

    @Mock
    private lateinit var deleteLockBoxViewModel: LockBoxViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<AddNewLockBoxResponseModel>>>

    @Before
    fun onSetUp() {
        MockitoAnnotations.initMocks(this)
        val context = Mockito.mock(Context::class.java)

        lockBoxRepository = TestRepositoryProvider().getLockBoxRepository(context!!)
        userRepository = TestRepositoryProvider().getUserRepository(context!!)
        addLockBoxViewModel = AddNewLockBoxViewModel(lockBoxRepository, userRepository)
        deleteLockBoxViewModel = LockBoxViewModel(lockBoxRepository, userRepository)
        addLockBoxViewModel.addNewLockBoxResponseLiveData.observeForever(observer)

    }

    @Test
    fun testNameValuesValidate() {
        assertEquals(false, lockBoxData.name?.isNullOrEmpty())
    }

    @Test
    fun testNoteValuesValidate() {
        assertEquals(false, lockBoxData.note?.isNullOrEmpty())
    }

    @Test
    fun testLovedOneValuesValidate() {
        assertEquals(false, lockBoxData.loveUserId?.isNullOrEmpty())
    }

    @Test
    fun testAllowedUserValuesValidate() {
        assertEquals(false, lockBoxData.allowedUserIds?.size!! > 0)
    }

    @Test
    fun createLockBox() = runBlockingTest {
        addLockBoxViewModel.addNewLockBoxApi(lockBoxData)
        Thread.sleep(3000)
        addLockBoxViewModel.addNewLockBoxResponseLiveData.getOrAwaitValueTest()
            .getContentIfNotHandled().let {
                when (it) {
                    is DataResult.Success -> {
                        Assert.assertEquals("Created successfully", it.data.message)
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