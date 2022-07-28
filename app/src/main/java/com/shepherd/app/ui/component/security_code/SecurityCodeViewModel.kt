package com.shepherd.app.ui.component.security_code

import com.shepherd.app.data.DataRepository
import com.shepherd.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SecurityCodeViewModel @Inject
constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {
}