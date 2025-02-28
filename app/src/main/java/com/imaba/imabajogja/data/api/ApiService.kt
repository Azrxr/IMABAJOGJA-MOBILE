package com.imaba.imabajogja.data.api

import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.response.LoginResponse
import com.imaba.imabajogja.data.response.MembersResponse
import com.imaba.imabajogja.data.response.RegisterAdminResponse
import com.imaba.imabajogja.data.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

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
}