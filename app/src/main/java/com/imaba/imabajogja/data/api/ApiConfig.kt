package com.imaba.imabajogja.data.api

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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiConfig {
    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreference: UserPreference): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()

            val token = try {
                runBlocking { userPreference.getToken() }
            } catch (e: Exception) {
                Timber.e("Gagal mengambil token: ${e.message}")
                "" // fallback token kosong
            }

            val requestWithHeaders = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
                .build()

            try {
                chain.proceed(requestWithHeaders)
            } catch (e: Exception) {
                Timber.e("Interceptor error saat melakukan request: ${e.message}")
                throw e // tetap throw agar Retrofit tahu gagal
            }
        }
    }

    @Provides
    @Singleton
    fun provideAuthenticator(
        userPreference: UserPreference,
        tokenExpiredCallback: TokenExpiredCallback
    ): Authenticator {
        return Authenticator { _, response ->
            try {
                if (response.code == 401) {
                    Timber.d("Authenticator: Token expired, memicu logout callback")
                    tokenExpiredCallback.onTokenExpired()
                    return@Authenticator null
                }

                val newToken = runBlocking { userPreference.getToken() }

                if (newToken.isBlank()) {
                    Timber.e("Authenticator: Token kosong, membatalkan autentikasi ulang")
                    return@Authenticator null
                }

                return@Authenticator response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()

            } catch (e: Exception) {
                Timber.e("Authenticator gagal memproses token: ${e.message}")
                null // gagal refresh, batalkan permintaan
            }
        }
    }


    @Provides
    @Singleton
    fun provideTokenExpiredCallback(
        tokenExpiredCallbackImpl: TokenExpiredCallbackImpl
    ): TokenExpiredCallback {
        return tokenExpiredCallbackImpl
    }

    interface TokenExpiredCallback {
        fun onTokenExpired()
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(
        authenticator: Authenticator,
        authInterceptor: Interceptor
    ): OkHttpClient {
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
//        val url = "http://192.168.54.254:8000/api/" //bld
//        val url = "http://10.0.2.2:8000/api/"
//        val url = "http://192.168.55.183:8000/api/"
//        val url = "http://192.168.1.193:8000/api/" //mam
        val url = "http://webservice.imabayogyakarta.com/api/"

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
}