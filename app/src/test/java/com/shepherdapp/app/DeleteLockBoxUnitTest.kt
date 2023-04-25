package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.dto.lock_box.delete_uploaded_lock_box_doc.DeleteUploadedLockBoxDocResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.lock_box.LockBoxRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
import com.shepherdapp.app.view_model.LockBoxViewModel
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
class DeleteLockBoxUnitTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    lateinit var lockBoxRepository: LockBoxRepository
    lateinit var userRepository: UserRepository

    @Mock
    private lateinit var deleteLockBoxViewModel: LockBoxViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>>
    private var lockBoxId = 81
    @Before
    fun onSetUp() {
        MockitoAnnotations.initMocks(this)
        val context = Mockito.mock(Context::class.java)

        lockBoxRepository = TestRepositoryProvider().getLockBoxRepository(context!!)
        userRepository = TestRepositoryProvider().getUserRepository(context!!)

        deleteLockBoxViewModel = LockBoxViewModel(lockBoxRepository, userRepository)
        deleteLockBoxViewModel.deleteLockBoxDocResponseLiveData.observeForever(observer)

    }

    @Test
    fun deleteLockBox() = runBlockingTest {
        deleteLockBoxViewModel.deleteAddedLockBoxDocumentBYID(
            lockBoxId,
            "cc502713-0443-4bdb-bab6-92fd1a28c82c"
        )
        Thread.sleep(3000)
        deleteLockBoxViewModel.deleteLockBoxDocResponseLiveData.getOrAwaitValueTest()
            .getContentIfNotHandled().let {
                when (it) {
                    is DataResult.Success -> {
                        Assert.assertEquals("Document deleted!", it.data.message)
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