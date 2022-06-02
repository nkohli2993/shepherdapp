package com.app.shepherd.ui.component.change_password

import com.app.shepherd.data.DataRepository
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject
constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {
}