package com.imaba.imabajogja.data.api

import com.imaba.imabajogja.data.response.DocumentsResponse
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.response.LoginResponse
import com.imaba.imabajogja.data.response.MembersResponse
import com.imaba.imabajogja.data.response.ProfileResponse
import com.imaba.imabajogja.data.response.ProfileUpdateResponse
import com.imaba.imabajogja.data.response.RegisterAdminResponse
import com.imaba.imabajogja.data.response.RegisterResponse
import com.imaba.imabajogja.data.response.StudyPlansResponse
import com.imaba.imabajogja.data.response.StudyResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.response.WilayahItem
import com.imaba.imabajogja.data.response.WilayahResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File

interface ApiService {

    //register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("username") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): RegisterResponse

    //register admin
    @FormUrlEncoded
    @POST("admin/register")
    suspend fun registerAdmin(
        @Field("username") name: String,
        @Field("fullname") fullname: String,
        @Field("phone_number") phoneNumber: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): RegisterAdminResponse

    //login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("login") credential: String,
        @Field("password") password: String
    ): LoginResponse

    //home
    @GET("home")
    suspend fun getHomeData(): Response<HomeResponse>

    //List member
    @POST("member/members")
    suspend fun getMembers(
    ): MembersResponse

    @GET("member/profile")
    suspend fun getProfile(

    ): Response<ProfileResponse>

    @FormUrlEncoded
    @POST("member/profileUpdate")
    suspend fun updateProfile(
        @Field("username") username: String,
        @Field("email") email: String,

        @Field("fullname") fullname: String,
        @Field("phone_number") phoneNumber: String,

        @Field("province_id") provinceId: Int,
        @Field("regency_id") regencyId: Int,
        @Field("district_id") districtId: Int,
        @Field("full_address") fullAddress: String,
        @Field("kode_pos") kodePos: String,
        @Field("agama") agama: String,
        @Field("nisn") nisn: String,
        @Field("tempat") tempat: String,
        @Field("tanggal_lahir") tanggalLahir: String,
        @Field("gender") gender: String,
        @Field("scholl_origin") schollOrigin: String,
        @Field("tahun_lulus") tahunLulus: Int,
    ): Response<ProfileUpdateResponse>

    @Multipart
    @POST("member/profileUpdate")
    suspend fun updatePhotoProfile(
        @Part profileImg: MultipartBody.Part
    ): Response<ProfileUpdateResponse>

    @FormUrlEncoded
    @POST("member/profileUpdate")
    suspend fun updatePassword(
        @Field("current_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("password_confirmation") passwordConfirmation: String,
    ): Response<ProfileUpdateResponse>

    @GET("province")
    suspend fun getProvinces(): WilayahResponse

    @GET("regency/{id}")
    suspend fun getRegencies(
        @Path("id") provinceId: Int,
        @Query("search") search: String? = null
    ): WilayahResponse

    // 🔥 GET District List (Kecamatan berdasarkan Regency ID)
    @GET("district/{id}")
    suspend fun getDistricts(
        @Path("id") regencyId: Int,
        @Query("search") search: String? = null
    ): WilayahResponse

    @GET("university")
    suspend fun getUniversity(): StudyResponse

    @GET("faculty/{id}")
    suspend fun getFaculty(
        @Path("id") provinceId: Int,
        @Query("search") search: String? = null
    ): StudyResponse

    @GET("programStudy/{id}")
    suspend fun getProgramStudy(
        @Path("id") provinceId: Int,
        @Query("search") search: String? = null
    ): StudyResponse

    @FormUrlEncoded
    @POST("member/studyPlaneAdd")
    suspend fun addStudyPlan(
        @Field("university_id") universityId: Int,
        @Field("program_study_id") programStudyId: Int
    ): Response<SuccesResponse>

    @GET("member/studyPlane")
    suspend fun getStudyPlane(): StudyPlansResponse

    @FormUrlEncoded
    @POST("member/studyPlaneUpdate/{id}")
    suspend fun updateStudyPlane(
        @Field("id") id: Int,
        @Field("university_id") universityId: Int,
        @Field("program_study_id") programStudyId: Int
    ): Response<SuccesResponse>


    @DELETE("member/studyPlaneDelete/{id}")
    suspend fun deleteStudyPlane(
        @Path("id") id: Int
    ): Response<SuccesResponse>

    @GET("member/showDocument")
    suspend fun getDocuments(): Response<DocumentsResponse>

    @DELETE("member/deleteDocument/{field}")
    suspend fun deleteDocument(
        @Path("field") field: String
    ) : Response<SuccesResponse>

    @Multipart
    @POST("member/uploadDocument")
    suspend fun uploadDocument(
        @Part("documentType") documentType: RequestBody,
        @Part file: MultipartBody.Part // File dokumen
    ): Response<SuccesResponse>

    @Multipart
    @POST("member/uploadHomePhoto")
    suspend fun uploadHomePhoto(
        @Part photoImg: MultipartBody.Part,
        @Part ("photo_title") photoTitle: RequestBody,
    ): Response<SuccesResponse>

    @DELETE("member/deleteHomePhoto/{id}")
    suspend fun deleteHomePhoto(
        @Path("id") id: Int
    ): Response<SuccesResponse>

}