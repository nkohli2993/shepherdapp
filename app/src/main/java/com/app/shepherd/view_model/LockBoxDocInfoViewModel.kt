package com.app.shepherd.view_model

import com.app.shepherd.data.DataRepository
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class LockBoxDocInfoViewModel @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {


}
