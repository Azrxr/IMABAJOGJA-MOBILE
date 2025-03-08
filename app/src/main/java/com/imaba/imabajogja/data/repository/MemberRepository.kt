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
import com.imaba.imabajogja.data.response.ProfileResponse
import com.imaba.imabajogja.data.response.ProfileUpdateResponse
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

    fun getProfile(): LiveData<Result<ProfileResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getProfile()

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

    fun updateProfile(
        username : String, email : String,
//        currentPassword: String, newPassword: String, passwordConfirmation: String,
        fullname : String, phoneNumber : String, profileImg: String,
        provinceId: Int, regencyId: Int, districtId: Int, fullAddres: String, kodePos: String,
        agama: String, nisn: String, tempat: String, tanggalLahir: String, gender: String,
        schollOrigin: String, tahunLulus: Int,
    ): LiveData<Result<ProfileUpdateResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.updateProfile(
                    username, email,
//                    currentPassword, newPassword, passwordConfirmation,
                    fullname, phoneNumber, profileImg,
                    provinceId, regencyId, districtId, fullAddres, kodePos,
                    agama, nisn, tempat, tanggalLahir, gender,
                    schollOrigin, tahunLulus)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Result.Success(it))
                    } ?: emit(Result.Error("Response body kosong"))
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Terjadi kesalahan"
                    emit(Result.Error("Error ${response.code()}: $errorMessage"))
                }
            }
            catch (e: Exception) {

                Log.d("data", "error MemberRepository: ${e.message}")
                emit(Result.Error(e.message.toString()))
            }
        }
    }
}