package com.shepherdapp.app.di

import android.app.Application
import com.shepherdapp.app.utils.Prefs

class TestApplicationDependencyProvider {

    fun provideSharedPref(application: Application): Prefs {
        return Prefs.with(application.applicationContext)!!
    }
}