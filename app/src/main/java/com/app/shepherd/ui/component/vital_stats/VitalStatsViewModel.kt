package com.app.shepherd.ui.component.vital_stats

import com.app.shepherd.data.DataRepository
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VitalStatsViewModel @Inject
constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {
}