package com.imaba.imabajogja.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.imaba.imabajogja.data.api.ApiService
import com.imaba.imabajogja.data.pagging.MemberPagingSource
import com.imaba.imabajogja.data.response.AdmProfileResponse
import com.imaba.imabajogja.data.response.DataItemMember
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.response.MembersResponse
import com.imaba.imabajogja.data.response.ProfileResponse
import com.imaba.imabajogja.data.response.ProfileUpdateResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.response.WilayahItem
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.compressPdf
import com.itextpdf.io.IOException
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    fun uploadDocumentOrganization(
        file: File,
        title: String,
        description: String,
    ): LiveData<Result<SuccesResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                // 📝 Cek apakah file perlu dikompres
                val compressedFile = try {
                    compressPdf(file) // 🔥 Kompres PDF sebelum upload
                } catch (e: IOException) {
                    Log.e("UploadDocument", "❌ Gagal mengompres PDF: ${e.message}")
                    emit(Result.Error("Gagal mengompres PDF: ${e.message}"))
                    return@liveData
                }

                val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestFile = compressedFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData(
                    "file_path", compressedFile.name, requestFile
                )

                // 🔥 Panggil API Service
                val response = apiService.addFileOrganization(titlePart, descriptionPart, filePart)


                Log.d("UploadDocument", "📤 Nama file: ${compressedFile.name}")
                Log.d("UploadDocument", "📝 Title: $title, Description: $description")

                if (response.isSuccessful) {
                    emit(Result.Success(response.body()!!))
                    Log.d("UploadDocument", "✅ Berhasil upload!")
                } else {
                    emit(Result.Error("❌ Gagal: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("UploadDocument", "❌ Terjadi kesalahan: ${e.message}")
                emit(Result.Error("Terjadi kesalahan: ${e.message}"))
            }
        }

    fun deleteDocumentOrganization(fileId: Int): LiveData<Result<SuccesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.deleteFileOrganization(fileId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body()!!))
            } else {
                emit(Result.Error("Gagal menghapus dokumen: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Log.e("DeleteDoc", "Error: ${e.message}")
            emit(Result.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    fun listMembers(search: String?, generation: List<String>?, memberType: List<String>?): Flow<PagingData<DataItemMember>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,  // Jumlah item per halaman
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MemberPagingSource(apiService, search, generation, memberType) }
        ).flow
    }

    fun getMemberSummary(): LiveData<Result<MembersResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getMemberSummary()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!))
            } else {
                emit(Result.Error("Gagal memuat summary anggota"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun deleteMember(memberId: Int): LiveData<Result<SuccesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.deleteMember(memberId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body()!!))
            } else {
                emit(Result.Error("Gagal menghapus anggota: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Log.e("DeleteMember", "Error: ${e.message}")
            emit(Result.Error("Terjadi kesalahan: ${e.message}"))
        }

    }

    fun updateMemberAdm( memberId: Int,
        fullname: String, phoneNumber: String,
        provinceId: Int, regencyId: Int, districtId: Int, fullAddres: String, kodePos: String,
        agama: String, nisn: String, tempat: String, tanggalLahir: String, gender: String,
        schollOrigin: String, tahunLulus: Int, angkatan: Int, memberType: String
    ): LiveData<Result<SuccesResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.updateMemberAdm( memberId,
                    fullname, phoneNumber,
                    provinceId, regencyId, districtId, fullAddres, kodePos,
                    agama, nisn, tempat, tanggalLahir, gender,
                    schollOrigin, tahunLulus, angkatan, memberType
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

                Log.d("data", "error MemberRepository: ${e.message}")
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun updateMemberPhotoProfileAdm(memberId: Int, photoFile: File): LiveData<Result<SuccesResponse>> {
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
                val response = apiService.updateMemberPhotoProfileAdm(memberId, body)

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
}