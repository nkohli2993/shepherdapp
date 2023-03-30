package com.shepherdapp.app

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shepherdapp.app.utils.Const
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
        var pauseAppLiveData: MutableLiveData<Boolean> = MutableLiveData()
    }

    var countDownTimer: CountDownTimer? = null

    override fun onCreate() {
        super.onCreate()
        appContext = this
        FirebaseApp.initializeApp(this)
        db = Firebase.firestore

        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(lifecycleEventObserver)
    }

    private val lifecycleEventObserver = LifecycleEventObserver { source, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (countDownTimer != null) {
                    countDownTimer?.cancel()
                    countDownTimer = null
                }
            }
            Lifecycle.Event.ON_PAUSE -> {
                if (!Prefs.with(appContext)?.getString(Const.USER_TOKEN, "").isNullOrEmpty())
                    startTimer()
            }
            Lifecycle.Event.ON_DESTROY -> {
                if (!Prefs.with(appContext)?.getString(Const.USER_TOKEN, "").isNullOrEmpty())
                    logoutApp()
            }
            Lifecycle.Event.ON_CREATE -> {}
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_ANY -> {}
        }
    }

    private fun startTimer() {
        if (countDownTimer == null) {
            // 10 min timer
            countDownTimer = object : CountDownTimer(1 * 60 * 1000, 1000) {
                override fun onTick(p0: Long) {
                    Log.d("TAG", "onTick: $p0")
                }

                override fun onFinish() {
                    logoutApp()
                    countDownTimer = null
                }
            }.start()
        }

    }

    private fun logoutApp() {
        if (!Prefs.with(appContext)?.getString(Const.USER_TOKEN, "").isNullOrEmpty()) {
            pauseAppLiveData.postValue(true)
            Prefs.with(appContext)?.save(Const.USER_INACTIVE_LOGOUT,true)
            Log.d("TAG", "logoutApp: "+"clearToken")
//                val intent = Intent(appContext, LoginActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                intent.putExtra("source", "base")
//                appContext?.startActivity(intent)
        }

    }


}
