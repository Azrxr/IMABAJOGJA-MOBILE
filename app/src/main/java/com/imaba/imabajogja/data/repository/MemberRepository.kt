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
import com.imaba.imabajogja.data.response.StudyItem
import com.imaba.imabajogja.data.response.StudyPlans
import com.imaba.imabajogja.data.response.StudyPlansResponse
import com.imaba.imabajogja.data.response.StudyResponse
import com.imaba.imabajogja.data.response.WilayahItem
import com.imaba.imabajogja.data.utils.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
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
        fullname : String, phoneNumber : String,
        provinceId: Int, regencyId: Int, districtId: Int, fullAddres: String, kodePos: String,
        agama: String, nisn: String, tempat: String, tanggalLahir: String, gender: String,
        schollOrigin: String, tahunLulus: Int,
    ): LiveData<Result<ProfileUpdateResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.updateProfile(
                    username, email,
                    fullname, phoneNumber,
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

    fun updatePhotoProfile(photoFile: File): LiveData<Result<ProfileUpdateResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                // 🔥 Konversi file menjadi Multipart
                val requestFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("profile_img_path", photoFile.name, requestFile)
                // 🔥 Panggil API
                val response = apiService.updatePhotoProfile(body)

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

    fun updatePassword(
        currentPassword: String,
        newPassword:String,
        passwordConfirmation: String
    ): LiveData<Result<ProfileUpdateResponse>>{
        return liveData {
            try {
                val response = apiService.updatePassword(
                    currentPassword, newPassword, passwordConfirmation
                )
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

    fun getProvinces(): LiveData<Result<List<WilayahItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getProvinces()
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getRegencies(provinceId: Int, search: String? = null): LiveData<Result<List<WilayahItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getRegencies(provinceId, search)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDistricts(regencyId: Int, search: String? = null): LiveData<Result<List<WilayahItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDistricts(regencyId, search)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun getUniversity(): LiveData<Result<List<StudyItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getUniversity()
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getFaculty(universityId: Int, search: String? = null): LiveData<Result<List<StudyItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getFaculty(universityId, search)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun getProgramStudy(universityId: Int, search: String? = null): LiveData<Result<List<StudyItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getProgramStudy(universityId, search)
            emit(Result.Success(response.data))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStudyPlans(): LiveData<Result<List<StudyPlans>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStudyPlane()
            if (!response.error){
                emit(Result.Success(response.data))
            } else(
                emit(Result.Error(response.message))
            )
        }  catch (e: Exception) {
            emit(Result.Error(e.message.toString() ?: "Terjadi kesalahan"))
        }
    }

    fun addStudyPlan(universityId: Int, programStudyId: Int): LiveData<Result<String>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addStudyPlan(universityId, programStudyId)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!.message))
            } else {
                emit(Result.Error(response.errorBody()?.string() ?: "Gagal menambahkan study plan"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun deleteStudyPlan(id: Int):
        LiveData<Result<String>> = liveData {
            emit(Result.Loading)
            try {
                val response = apiService.deleteStudyPlane(id)
                if (response.isSuccessful && response.body() != null) {
                    emit(Result.Success(response.body()!!.message))
                } else {
                    emit(Result.Error(response.errorBody()?.string() ?: "Gagal menghapus data"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "Terjadi kesalahan"))
            }
    }

}