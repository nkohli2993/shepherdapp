package com.shepherdapp.app.network.retrofit
import com.shepherdapp.app.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Created by Deepak Rattan on 27/05/22
 */

fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    return logging
}

var appInterceptor: Interceptor? = null
fun provideHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
    val httpClient = OkHttpClient.Builder()
    if (appInterceptor == null)
        appInterceptor = MyAppInterceptor()

    httpClient.addInterceptor(appInterceptor!!)
    httpClient.addInterceptor(logging)
    return httpClient.build()
}

fun provideApiProvider(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
//        .baseUrl(ApiConstants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
}

