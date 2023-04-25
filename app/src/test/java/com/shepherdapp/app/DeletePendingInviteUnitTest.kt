package com.shepherdapp.app

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamRequestModel
import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamResponseModel
import com.shepherdapp.app.data.dto.invitation.delete_pending_invitee.DeletePendingInviteeByIdResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.care_teams.CareTeamsRepository
import com.shepherdapp.app.data.remote.home_repository.HomeRepository
import com.shepherdapp.app.di.TestRepositoryProvider
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.utils.MainDispatcherRule
import com.shepherdapp.app.utils.getOrAwaitValueTest
import com.shepherdapp.app.view_model.AddMemberViewModel
import com.shepherdapp.app.view_model.CareTeamMembersViewModel
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
class DeletePendingInviteUnitTest {


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    lateinit var authRepository: AuthRepository
    lateinit var userRepository: UserRepository
    lateinit var careTeamRepository: CareTeamsRepository
    lateinit var homeRepository: HomeRepository

    @Mock
    private lateinit var careTeamMemberViewModel: CareTeamMembersViewModel

    @Mock
    lateinit var observer: Observer<Event<DataResult<DeletePendingInviteeByIdResponseModel>>>

    @Before
    fun onSetUp() {

        MockitoAnnotations.initMocks(this)

        val context = Mockito.mock(Context::class.java)

        authRepository = TestRepositoryProvider().getAuthRepository(context!!)
        userRepository = TestRepositoryProvider().getUserRepository(context)
        careTeamRepository = TestRepositoryProvider().getCareTeamRepository(context)
        homeRepository = TestRepositoryProvider().getHomeRepository(context)
        careTeamMemberViewModel = CareTeamMembersViewModel(careTeamRepository,userRepository,homeRepository,authRepository)

        careTeamMemberViewModel.deletePendingInviteeByIdResponseLiveData.observeForever(observer)

    }

    @Test
    fun `test delete care team member data success`() = runBlockingTest {

        careTeamMemberViewModel.deletePendingInviteeById(240)

        Thread.sleep(3000)

        careTeamMemberViewModel.deletePendingInviteeByIdResponseLiveData.getOrAwaitValueTest().getContentIfNotHandled()
            .let {
                when (it) {
                    is DataResult.Success -> {
                        Assert.assertEquals(
                            "Pending invite delete successfully.",
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
