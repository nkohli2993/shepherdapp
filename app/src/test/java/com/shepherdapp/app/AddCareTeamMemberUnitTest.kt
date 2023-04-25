package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamRequestModel
import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.care_teams.CareTeamsRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
import com.shepherdapp.app.view_model.AddMemberViewModel
import kotlinx.coroutines.Dispatchers
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
class AddCareTeamMemberUnitTest {


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private var addCareTeamMemberModel = AddNewMemberCareTeamRequestModel(
        userId = "d2d2e458-e726-4134-9b48-cdcd7b6a6fb0",
        receiverUserId = null,
        email = "car@yopmail.com",
        loveoneUserId = "6299cd52-5df2-40bf-a9c2-7480e559b9be",
        careteamRoleId = null,
        permission = "1,3,4"
    )

    lateinit var dataRepository: DataRepository
    lateinit var userRepository: UserRepository
    lateinit var careTeamRepository: CareTeamsRepository

    @Mock
    private lateinit var addCareTeamMemberViewModel: AddMemberViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<AddNewMemberCareTeamResponseModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        val context = Mockito.mock(Context::class.java)

        dataRepository = TestRepositoryProvider().getDataRepository(context!!, Dispatchers.Default)
        userRepository = TestRepositoryProvider().getUserRepository(context)
        careTeamRepository = TestRepositoryProvider().getCareTeamRepository(context)
        addCareTeamMemberViewModel = AddMemberViewModel(dataRepository, careTeamRepository,userRepository)

        addCareTeamMemberViewModel.addNewMemberCareTeamResponseLiveData.observeForever(observer)

    }

    @Test
    fun testEmailValidate() {
        Assert.assertEquals(false, addCareTeamMemberModel.email?.isEmpty())
    }

    @Test
    fun testPermissionValidate() {
        Assert.assertEquals(false, addCareTeamMemberModel.permission?.isEmpty())
    }



    @Test
    fun `test edit care point data success`() = runBlockingTest {

        addCareTeamMemberViewModel.addNewMemberCareTeam(addCareTeamMemberModel)

        Thread.sleep(3000)

        addCareTeamMemberViewModel.addNewMemberCareTeamResponseLiveData.getOrAwaitValueTest().getContentIfNotHandled()
            .let {
                when (it) {
                    is DataResult.Success -> {
                        Assert.assertEquals(
                            "Invitation sent successfully.",
                            it.data.message
                        )
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
