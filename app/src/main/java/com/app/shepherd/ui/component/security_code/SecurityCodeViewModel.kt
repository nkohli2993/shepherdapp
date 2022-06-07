package com.app.shepherd.ui.component.security_code

import com.app.shepherd.data.DataRepository
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SecurityCodeViewModel @Inject
constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {
}