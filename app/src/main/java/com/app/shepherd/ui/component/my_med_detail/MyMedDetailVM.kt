package com.app.shepherd.ui.component.my_med_detail

import com.app.shepherd.data.DataRepository
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyMedDetailVM @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {

}