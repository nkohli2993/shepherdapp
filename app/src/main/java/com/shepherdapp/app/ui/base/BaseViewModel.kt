package com.shepherdapp.app.ui.base

import androidx.lifecycle.ViewModel
import com.shepherdapp.app.usecase.errors.ErrorManager
import javax.inject.Inject


/**
 * Created by Sumit Kumar
 */


abstract class BaseViewModel : ViewModel() {
    /**Inject Singleton ErrorManager
     * Use this errorManager to get the Errors
     */
    @Inject
    lateinit var errorManager: ErrorManager
}
