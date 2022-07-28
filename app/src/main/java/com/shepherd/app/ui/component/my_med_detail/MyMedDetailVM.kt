package com.shepherd.app.ui.component.my_med_detail

import com.shepherd.app.data.DataRepository
import com.shepherd.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyMedDetailVM @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {

}