package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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
class EditLockboxUnitText{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    var allowedUser: ArrayList<String> = arrayListOf("36e91f3b-c4a4-40d3-9d70-58654d2df553")

    private var editLockBoxData = EditLockBoxRequestModel(
        "Test lockBox update",
        "Description for lockBox update",
        lbtId = 8,
        allowedUserIds = allowedUser,
        documents = null
    )

    lateinit var lockBoxRepository: LockBoxRepository
    lateinit var userRepository: UserRepository

    @Mock
    private lateinit var addLockBoxViewModel: AddNewLockBoxViewModel
   @Mock
    lateinit var observer: Observer<Event<DataResult<AddNewLockBoxResponseModel>>>

    @Before
    fun onSetUp() {
        MockitoAnnotations.initMocks(this)
        val context = Mockito.mock(Context::class.java)

        lockBoxRepository = TestRepositoryProvider().getLockBoxRepository(context!!)
        userRepository = TestRepositoryProvider().getUserRepository(context!!)
        addLockBoxViewModel = AddNewLockBoxViewModel(lockBoxRepository, userRepository)

        addLockBoxViewModel.addNewLockBoxResponseLiveData.observeForever(observer)

    }

    @Test
    fun testNameValuesValidate() {
        Assert.assertEquals(false, editLockBoxData.name?.isNullOrEmpty())
    }

    @Test
    fun testNoteValuesValidate() {
        Assert.assertEquals(false, editLockBoxData.note?.isNullOrEmpty())
    }


    @Test
    fun testAllowedUserValuesValidate() {
        Assert.assertEquals(false, editLockBoxData.allowedUserIds?.size!! > 0)
    }

    @Test
    fun editLockBox() = runBlockingTest {
        addLockBoxViewModel.callEditLockBoxApi(editLockBoxData,80)
        Thread.sleep(3000)
        addLockBoxViewModel.addNewLockBoxResponseLiveData.getOrAwaitValueTest()
            .getContentIfNotHandled().let {
                when (it) {
                    is DataResult.Success -> {
                        Assert.assertEquals("Document updated!", it.data.message)
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