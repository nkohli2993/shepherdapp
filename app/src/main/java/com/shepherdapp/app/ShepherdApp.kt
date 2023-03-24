package com.shepherdapp.app

import android.app.Application
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shepherdapp.app.ui.component.login.LoginActivity
import com.shepherdapp.app.utils.Const.USER_TOKEN
import com.shepherdapp.app.utils.Prefs
import dagger.hilt.android.HiltAndroidApp


/**
 * Created by Sumit Kumar
 */
@HiltAndroidApp
open class ShepherdApp : Application() {
    companion object {
        var appContext: Application? = null
        lateinit var db: FirebaseFirestore
        var pauseAppLiveData : MutableLiveData<Boolean> = MutableLiveData()
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(ProcessLifecycleObserver())
    }

    private class ProcessLifecycleObserver : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onApplicationPaused() {
            if (!Prefs.with(appContext)?.getString(USER_TOKEN,"").isNullOrEmpty()) {
                pauseAppLiveData.postValue(true)
                Prefs.with(appContext)?.removeAll()
                val intent = Intent(appContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("source", "base")
                appContext?.startActivity(intent)
            }
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onApplicationResumed() {
            pauseAppLiveData.value = false
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onApplicationDestroyed() {
            pauseAppLiveData.value = false
        }
    }


}
