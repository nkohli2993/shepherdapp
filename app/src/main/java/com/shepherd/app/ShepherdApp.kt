package com.shepherd.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Sumit Kumar
 */
@HiltAndroidApp
open class ShepherdApp : Application() {
    companion object {
        var appContext: Application? = null
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}
