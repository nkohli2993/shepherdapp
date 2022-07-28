package com.shepherd.app.ui.component.vital_stats

import com.shepherd.app.data.DataRepository
import com.shepherd.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VitalStatsViewModel @Inject
constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {
}