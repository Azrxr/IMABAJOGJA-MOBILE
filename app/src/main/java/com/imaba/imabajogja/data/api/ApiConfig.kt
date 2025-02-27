package com.imaba.imabajogja.data.api

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiConfig {
    @Provides
    @Singleton
    fun getApiService(token: String): ApiService {
        val url = "http://192.168.100.178:8000/api/"
//        val url = "http://192.168.1.88:8000/api/" //perpus utara
//        val url = "http://10.0.2.2:8000/api/" //local

        //token
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            //val token = Injection
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(requestHeaders)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}