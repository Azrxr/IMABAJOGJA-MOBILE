package com.imaba.imabajogja.data.api

import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.response.LoginResponse
import com.imaba.imabajogja.data.response.MembersResponse
import com.imaba.imabajogja.data.response.ProfileResponse
import com.imaba.imabajogja.data.response.ProfileUpdateResponse
import com.imaba.imabajogja.data.response.RegisterAdminResponse
import com.imaba.imabajogja.data.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

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

//        @Field("current_password") currentPassword: String,
//        @Field("new_password") newPassword: String,
//        @Field("password_confirmation") passwordConfirmation: String,

        @Field("fullname") fullname: String,
        @Field("phone_number") phoneNumber: String,
        @Field("profile_img_path") profileImg: String,

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



//    @GET("locations/provinces")
//    suspend fun getProvinces(): Response<List<Province>>
//
//    @GET("locations/regencies/{province_id}")
//    suspend fun getRegencies(@Path("province_id") provinceId: Int): Response<List<Regency>>
//
//    @GET("locations/districts/{regency_id}")
//    suspend fun getDistricts(@Path("regency_id") regencyId: Int): Response<List<District>>

}