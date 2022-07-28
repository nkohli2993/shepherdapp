package com.shepherd.app.ui.component.edit_profile

import com.shepherd.app.data.DataRepository
import com.shepherd.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject
constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {
}