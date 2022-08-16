package com.shepherd.app

import android.app.Application
import com.google.firebase.FirebaseApp
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Sumit Kumar
 */
@HiltAndroidApp
open class ShepherdApp : Application() {
    companion object {
        var appContext: Application? = null
//        lateinit var db: FirebaseFirestore
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        FirebaseApp.initializeApp(this)
//        db = Firebase.firestore
    }
}
