package com.imaba.imabajogja.data.api

import android.util.Log
import com.google.gson.GsonBuilder
import com.imaba.imabajogja.data.model.UserPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiConfig {
    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreference: UserPreference): Interceptor {
        return Interceptor { chain ->
            val req = chain.request()
            val token = runBlocking { userPreference.getToken() }

            Log.d("Interceptor", "Menggunakan token: $token") // 🔥 Log untuk cek token

            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer $token") // 🔥 Tambahkan token
                .addHeader("Accept", "application/json") // 🔥 Pastikan API menerima JSON
                .build()

            chain.proceed(requestHeaders)
        }
    }

    @Provides
    @Singleton
    fun provideAuthenticator(userPreference: UserPreference): Authenticator {
        return Authenticator { _, response ->
            Log.d("Authenticator", "Token expired, mencoba refresh...") // 🔥 Log jika token expired

            val newToken = runBlocking { userPreference.getToken() } // Ambil token baru dari DataStore

            if (newToken.isEmpty()) {
                Log.e("Authenticator", "Token kosong, logout user!") // 🔥 Jika token kosong, logout user
                return@Authenticator null
            }

            response.request.newBuilder()
                .header("Authorization", "Bearer $newToken") // 🔥 Gunakan token baru
                .build()
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authenticator: Authenticator, authInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(authenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
//        val url = "http://192.168.100.7:8000/api/"
//        val url = "http://192.168.31.44:8000/api/"
        val url = "http://10.0.2.2:8000/api/"
//        val url = "http://192.168.177.251:8000/api/"

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


//    @Provides
//    @Singleton
//    fun getApiService(token: String): ApiService {
//        val url = "http://192.168.100.178:8000/api/"
////        val url = "http://192.168.1.88:8000/api/" //perpus utara
////        val url = "http://10.0.2.2:8000/api/" //local
//
//        //token
//        val authInterceptor = Interceptor { chain ->
//            val req = chain.request()
//            //val token = Injection
//            val requestHeaders = req.newBuilder()
//                .addHeader("Authorization", "Bearer $token")
//                .addHeader("Accept", "application/json")
//                .build()
//            chain.proceed(requestHeaders)
//        }
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor(authInterceptor)
//            .build()
//
//        val gson = GsonBuilder()
//            .setLenient()
//            .create()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(url)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(client)
//            .build()
//        return retrofit.create(ApiService::class.java)
//    }
}