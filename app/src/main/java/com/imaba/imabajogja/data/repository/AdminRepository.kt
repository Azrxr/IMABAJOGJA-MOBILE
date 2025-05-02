package com.imaba.imabajogja.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.imaba.imabajogja.data.api.ApiService
import com.imaba.imabajogja.data.response.AdmProfileResponse
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.response.ProfileResponse
import com.imaba.imabajogja.data.response.ProfileUpdateResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.response.WilayahItem
import com.imaba.imabajogja.data.utils.Result
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(private val apiService: ApiService) {

    fun getProfile(): LiveData<Result<AdmProfileResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAdmProfile()

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
        username: String, email: String,
        fullname: String, phoneNumber: String,
        provinceId: Int, regencyId: Int, districtId: Int, fullAddres: String
    ): LiveData<Result<SuccesResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.updateAdmProfile(
                    username, email,
                    fullname, phoneNumber,
                    provinceId, regencyId, districtId, fullAddres
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

                Log.d("dataProfile", "error AdminRepository: ${e.message}")
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun updatePhotoProfile(photoFile: File): LiveData<Result<SuccesResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                // 🔥 Konversi file menjadi Multipart
                val requestFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "profile_img_path",
                    photoFile.name,
                    requestFile
                )
                // 🔥 Panggil API
                val response = apiService.updateAdmPhotoProfile(body)

                if (response.isSuccessful) {
                    emit(Result.Success(response.body()!!))
                } else {
                    emit(Result.Error("Gagal mengupload foto: ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }


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