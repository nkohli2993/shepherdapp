package com.shepherdapp.app.di

import android.content.Context
import com.shepherdapp.app.network.retrofit.ApiService
import com.shepherdapp.app.network.retrofit.MyAppInterceptor

class TestNetworkDependencyProvider {

    fun getApiService(context: Context): ApiService {
        return AppModule().run {
            provideApiService(provideApiProvider(provideOkHttpClient(MyAppInterceptor(null))))
        }
    }
}