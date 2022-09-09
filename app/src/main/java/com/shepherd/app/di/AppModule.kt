package com.shepherd.app.di

import android.content.Context
import com.shepherd.app.BuildConfig
import com.shepherd.app.ShepherdApp
import com.shepherd.app.constants.ApiConstants
import com.shepherd.app.data.local.LocalData
import com.shepherd.app.network.retrofit.ApiService
import com.shepherd.app.network.retrofit.MyAppInterceptor
import com.shepherd.app.utils.Network
import com.shepherd.app.utils.NetworkConnectivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    private val CONNECT_TIMEOUT: Long = 10000
    private val READ_TIMEOUT: Long = 15000
    private val WRITE_TIMEOUT: Long = 15000

    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext app: Context): ShepherdApp {
        return app as ShepherdApp
    }

    @Provides
    @Singleton
    fun provideLocalRepository(@ApplicationContext context: Context): LocalData {
        return LocalData(context)
    }

    @Provides
    @Singleton
    fun provideCoroutineContext(): CoroutineContext {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivity(@ApplicationContext context: Context): NetworkConnectivity {
        return Network(context)
    }

    @Provides
    @Singleton
    fun provideApiProvider(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
//            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(interceptor: MyAppInterceptor): OkHttpClient {

        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        okHttpClient.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
        okHttpClient.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)

        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = (HttpLoggingInterceptor.Level.BODY)
        okHttpClient.addInterceptor(interceptor)
        okHttpClient.addInterceptor(logInterceptor)

        return okHttpClient.build()
    }
}
