package com.imaba.imabajogja.data.di

import android.content.Context
import android.util.Log
import com.imaba.imabajogja.data.api.ApiConfig
import com.imaba.imabajogja.data.model.UserPreference
import com.imaba.imabajogja.data.model.dataStore
import com.imaba.imabajogja.data.repository.LoginRepository
import com.imaba.imabajogja.data.repository.MemberRepository
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): LoginRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getToken() }
        val role = runBlocking { pref.getRole() }
        Log.d("token", "token dari data store: ${user}, role: ${role}")
        val apiService = ApiConfig.getApiService(user)
        return LoginRepository.getInstance(apiService, pref)
    }

}
