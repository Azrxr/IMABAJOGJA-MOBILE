package com.imaba.imabajogja.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.imaba.imabajogja.data.api.ApiService
import com.imaba.imabajogja.data.pagging.MemberPagingSource
import com.imaba.imabajogja.data.response.DataItemMember
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(private val apiService: ApiService) {

//    Repository akan menjadi perantara antara API Service dan ViewModel.
    fun getHomeData(): LiveData<Result<HomeResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getHomeData()

            if (!response.isSuccessful) {
                throw Exception("Error dari server: ${response.code()} - ${response.message()}")
            }

            val body = response.body()
            if (body == null) {
                throw Exception("Response dari server kosong")
            }

            Log.d("data", "Response MemberRepository: ${body.data}")
            emit(Result.Success(body))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
            Log.d("data", "error MemberRepository: ${e.message}")
        }
    }

    fun getMembers(): Flow<PagingData<DataItemMember>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,  // Jumlah item per halaman
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MemberPagingSource(apiService) }
        ).flow
    }
}