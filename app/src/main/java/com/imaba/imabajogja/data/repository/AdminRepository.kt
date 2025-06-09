package com.imaba.imabajogja.data.repository

import android.os.Environment
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
import com.imaba.imabajogja.data.response.ImportMemberResponse
import com.imaba.imabajogja.data.response.MemberDetailResponse
import com.imaba.imabajogja.data.response.MembersResponse
import com.imaba.imabajogja.data.response.ProgramStudyImportResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.compressPdf
import com.imaba.imabajogja.data.utils.reduceFileImage
import com.itextpdf.io.IOException
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
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
                val requestFile =
                    compressedFile.asRequestBody("application/pdf".toMediaTypeOrNull())
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

    fun listMembers(
        search: String?,
        generation: List<String>?,
        memberType: List<String>?
    ): Flow<PagingData<DataItemMember>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,  // Jumlah item per halaman
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MemberPagingSource(apiService, search, generation, memberType, planStatus = null) }
        ).flow
    }

    /* TODO: belum fungsi
    fun listMemberStudy(
        planStatus: String?
    ): Flow<PagingData<DataItemMember>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,  // Jumlah item per halaman
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MemberPagingSource(apiService, search = null, generation = null, memberType = null, planStatus) }
        ).flow
    }

     */

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

    fun updateMemberAdm(
        memberId: Int,
        fullname: String, phoneNumber: String,
        provinceId: Int, regencyId: Int, districtId: Int, fullAddres: String, kodePos: String,
        agama: String, nisn: String, tempat: String, tanggalLahir: String, gender: String,
        schollOrigin: String, tahunLulus: Int, angkatan: String, memberType: String, noMember: String? = null
    ): LiveData<Result<SuccesResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.updateMemberAdm(
                    memberId,
                    fullname, phoneNumber,
                    provinceId, regencyId, districtId, fullAddres, kodePos,
                    agama, nisn, tempat, tanggalLahir, gender,
                    schollOrigin, tahunLulus, angkatan, memberType, noMember
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

    fun updateMemberPhotoProfileAdm(
        memberId: Int,
        photoFile: File
    ): LiveData<Result<SuccesResponse>> {
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

    fun getMemberDetailAdm(memberId: Int): LiveData<Result<MemberDetailResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getMemberDetailAdm(memberId)

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

    fun addStudyPlaneAdm(
        memberId: Int, universityId: Int, programStudyId: Int,
    ): LiveData<Result<SuccesResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.addStudyPlanAdm(
                    memberId, universityId, programStudyId,
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

    fun updateStudyPlaneAdm(
        memberId: Int,
        studyPlanId: Int,
        status: String,
    ): LiveData<Result<SuccesResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.updateStudyPlanAdm(
                    memberId, studyPlanId, status,
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

    fun uploadDocument(
        memberId: Int,
        docId: Int,
        documentType: String,
        file: File
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

                val docTypeRequestBody =
                    documentType.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestFile =
                    compressedFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData(
                    documentType,
                    compressedFile.name,
                    requestFile
                )

                // 🔥 Panggil API Service
                val response =
                    apiService.uploadDocumentAdm(memberId, docId, docTypeRequestBody, filePart)

                Log.d("UploadDocument", "Mengunggah dokumen: $documentType")
                Log.d("UploadDocument", "Nama file: ${file.name}, Ukuran: ${file.length()} bytes")

                if (response.isSuccessful) {
                    emit(Result.Success(response.body()!!))
                    Log.d("UploadDocument", "✅ Dokumen berhasil diunggah!")
                } else {
                    emit(Result.Error("Gagal mengupload dokumen: ${response.message()}"))
                    Log.e("UploadDocument", "❌ Gagal: ${response.body()}")
                }
            } catch (e: Exception) {
                Log.e("UploadDocument", "❌ Terjadi kesalahan: ${e.message}")
                emit(Result.Error("Terjadi kesalahan: ${e.message}"))
            }
        }

    fun deleteDocument(memberId: Int, field: String): LiveData<Result<SuccesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.deleteDocumentAdm(memberId, field)
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

    // Upload foto rumah dengan title
    fun uploadPhotoDoc(
        memberId: Int,
        docId: Int,
        documentType: String,
        file: File
    ): LiveData<Result<SuccesResponse>> =
        liveData {

            emit(Result.Loading)
            try {
                // 🔥 Konversi file menjadi Multipart
                val compressedFile = file.reduceFileImage()
                val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

                val filePart = MultipartBody.Part.createFormData(
                    documentType,
                    compressedFile.name,
                    requestFile
                )
                val photoType = documentType.toRequestBody("text/plain".toMediaTypeOrNull())

                // 🔥 Panggil API
                val response = apiService.uploadDocumentAdm(memberId, docId, photoType, filePart)

                if (response.isSuccessful) {
                    emit(Result.Success(response.body()!!))
                } else {
                    emit(Result.Error("Gagal mengupload foto: ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

        }

    // Upload foto rumah dengan title
    fun uploadHomePhoto(
        memberId: Int,
        photoTitle: String,
        photoFile: File
    ): LiveData<Result<SuccesResponse>> =
        liveData {

            emit(Result.Loading)
            try {
                // 🔥 Konversi file menjadi Multipart
                val compressedFile = photoFile.reduceFileImage()
                // 🔥 Konversi file menjadi MultipartBody
                val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "photo_img_path",
                    compressedFile.name,
                    requestFile
                )
                val titleBody = photoTitle.toRequestBody("text/plain".toMediaTypeOrNull())

                // 🔥 Panggil API
                val response = apiService.uploadHomePhotoAdm(memberId, body, titleBody)

                if (response.isSuccessful) {
                    emit(Result.Success(response.body()!!))
                } else {
                    emit(Result.Error("Gagal mengupload foto: ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

        }

    // Hapus foto rumah berdasarkan ID
    fun deleteHomePhoto(id: Int): LiveData<Result<SuccesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.deleteHomePhotoAdm(id)
            if (response.isSuccessful) {
                emit(Result.Success(response.body()!!))
            } else {
                emit(Result.Error("Gagal menghapus foto rumah: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Log.e("DeletePhoto", "Error: ${e.message}")
            emit(Result.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    fun importMember(file: File): LiveData<Result<ImportMemberResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val requestFile =
                    file.asRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val response = apiService.importMember(body)

                if (response.isSuccessful && response.body() != null) {
                    Log.d("ImportMember", "Respons sukses: ${response.body()}")
                    val bodyString = response.body()?.toString()
                    Log.d("ImportCheck", "Body: $bodyString")
                    emit(Result.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Terjadi kesalahan"
                    Log.e("ImportMember", "Respons gagal: $errorMessage")
                    emit(Result.Error("Gagal mengupload file: $errorMessage"))
                }
            } catch (e: Exception) {
                Log.e("ImportMember", "Exception: ${e.message}")
                emit(Result.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }

    fun importProgramStudy(file: File): LiveData<Result<ProgramStudyImportResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val requestFile =
                    file.asRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val response = apiService.importProgramStudy(body)

                if (response.isSuccessful && response.body() != null) {
                    Log.d("ImportMember", "Respons sukses: ${response.body()}")
                    val bodyString = response.body()?.toString()
                    Log.d("ImportCheck", "Body: $bodyString")
                    emit(Result.Success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Terjadi kesalahan"
                    Log.e("ImportMember", "Respons gagal: $errorMessage")
                    emit(Result.Error("Gagal mengupload file: $errorMessage"))
                }
            } catch (e: Exception) {
                Log.e("ImportMember", "Exception: ${e.message}")
                emit(Result.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
            }
        }


    suspend fun exportMembers(
        generations: List<String>?,
        memberTypes: List<String>?
    ): Result<ResponseBody> {
        return try {
            val response = apiService.exportMembers(generations, memberTypes)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                return Result.Error("Export failed: $errorBody")
            }

            val body = response.body() ?: return Result.Error("Empty response body")

            Result.Success(body)
        } catch (e: Exception) {
            Result.Error("Export error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }


    fun updateStudyMemberAdm(
        memberId: Int,
        universityId: Int,
        facultyId: Int ?= null,
        programStudyId: Int
    ): LiveData<Result<SuccesResponse>> = liveData {
        emit(Result.Loading)
        try {
            Timber.d("Mengirim data ke server: universityId=$universityId, facultyId=$facultyId, programStudyId=$programStudyId")

            val response = apiService.updateStudyMemberAdm(memberId, universityId, facultyId, programStudyId)

            if (response.isSuccessful && response.body() != null) {
                Log.d("UpdateStudyMember", "Berhasil update: ${response.body()!!.message}")
                emit(Result.Success(response.body()!!))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Gagal memperbarui data"
                Log.e("UpdateStudyMember", "Error Response: $errorMessage")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("UpdateStudyMember", "Exception: ${e.message}")
            emit(Result.Error("Terjadi kesalahan: ${e.message}"))
        }
    }
}