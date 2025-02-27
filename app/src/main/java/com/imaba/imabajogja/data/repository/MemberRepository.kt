package com.imaba.imabajogja.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.imaba.imabajogja.data.api.ApiService
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.utils.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(private val apiService: ApiService) {

    fun getHomeData(): LiveData<Result<HomeResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getHomeData()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
}