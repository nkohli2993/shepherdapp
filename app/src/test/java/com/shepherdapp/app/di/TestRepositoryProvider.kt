package com.shepherdapp.app.di

import android.content.Context
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.lock_box.LockBoxRepository
import com.shepherdapp.app.data.remote.med_list.MedListRepository
import com.shepherdapp.app.data.remote.medical_conditions.MedicalConditionRepository
import com.shepherdapp.app.data.remote.relation_repository.RelationRepository
import com.shepherdapp.app.data.remote.vital_stats.VitalStatsRepository

class TestRepositoryProvider {

    fun getAuthRepository(context: Context): AuthRepository{
        return AuthRepository(TestNetworkDependencyProvider().getApiService(context))
    }
    fun getUserRepository(context: Context): UserRepository{
        return UserRepository(TestNetworkDependencyProvider().getApiService(context))
    }
    fun getLockBoxRepository(context: Context): LockBoxRepository {
        return LockBoxRepository(TestNetworkDependencyProvider().getApiService(context))
    }
    fun getRelationRepository(context: Context): RelationRepository {
        return RelationRepository(TestNetworkDependencyProvider().getApiService(context))
    }
    fun getMedicalConditionRepository(context: Context): MedicalConditionRepository {
        return MedicalConditionRepository(TestNetworkDependencyProvider().getApiService(context))
    }
    fun getMedListRepository(context: Context): MedListRepository {
        return MedListRepository(TestNetworkDependencyProvider().getApiService(context))
    }
    fun getVitalStatsRepository(context: Context): VitalStatsRepository {
        return VitalStatsRepository(TestNetworkDependencyProvider().getApiService(context))
    }


}