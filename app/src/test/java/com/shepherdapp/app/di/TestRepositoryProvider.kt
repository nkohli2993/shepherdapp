package com.shepherdapp.app.di

import android.content.Context
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository

class TestRepositoryProvider {

    fun getAuthRepository(context: Context): AuthRepository{
        return AuthRepository(TestNetworkDependencyProvider().getApiService(context))
    }
    fun getUserRepository(context: Context): UserRepository{
        return UserRepository(TestNetworkDependencyProvider().getApiService(context))
    }

}