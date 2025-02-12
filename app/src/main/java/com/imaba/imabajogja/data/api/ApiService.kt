package com.imaba.imabajogja.data.api

import com.imaba.imabajogja.data.response.LoginResponse
import com.imaba.imabajogja.data.response.RegisterAdminResponse
import com.imaba.imabajogja.data.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("role") role: String
    ): RegisterAdminResponse

    //login
    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("login")
    suspend fun login(
        @Field("login") credential: String,
        @Field("password") password: String
    ): LoginResponse
}