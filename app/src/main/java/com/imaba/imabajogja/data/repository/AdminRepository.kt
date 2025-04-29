package com.imaba.imabajogja.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.imaba.imabajogja.data.api.ApiService
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.utils.Result
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(private val apiService: ApiService) {

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

    fun updateProfileOrganization(
        title: String,
        description: String,
        vision: String,
        mission: String,
        address: String,
        email: String,
        phoneNumber: String,
        phoneNumber2: String,
    ): LiveData<Result<SuccesResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.updateOrganizationProfile(
                    title,
                    description,
                    vision,
                    mission,
                    address,
                    email,
                    phoneNumber,
                    phoneNumber2
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Result.Success(it))
                    } ?: emit(Result.Error("Response body kosong"))
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Terjadi kesalahan"
                    emit(Result.Error("Error ${response.code()}: $errorMessage"))
                }
            } catch (e: Exception) {

                Timber.tag("data").d("error AdminRepository: ${e.message}")
                emit(Result.Error(e.message.toString()))
            }
        }
    }
}