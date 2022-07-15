package com.app.shepherd.ui.component.loved_one

import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.auth_repository.AuthRepository
import com.app.shepherd.data.remote.care_teams.CareTeamsRepository
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LovedOneViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val careTeamsRepository: CareTeamsRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

}