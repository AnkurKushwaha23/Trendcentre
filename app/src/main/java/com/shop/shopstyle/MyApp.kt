package com.shop.shopstyle

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application(){
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            Log.d("FirebaseInit", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Firebase initialization failed", e)
        }
    }
}