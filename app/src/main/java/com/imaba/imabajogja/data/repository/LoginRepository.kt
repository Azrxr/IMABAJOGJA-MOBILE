package com.imaba.imabajogja.data.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.imaba.imabajogja.data.api.ApiService
import com.imaba.imabajogja.data.model.UserModel
import com.imaba.imabajogja.data.model.UserPreference
import com.imaba.imabajogja.data.response.LoginResponse
import com.imaba.imabajogja.data.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(credential: String, password: String) : LiveData<Result<LoginResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val login = apiService.login(credential, password)
                emit(Result.Success(login))

            } catch (e: Exception) {
                Log.e("login","gagal login:  ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: LoginRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): LoginRepository =
            instance ?: synchronized(this) {
                instance ?: LoginRepository(apiService , userPreference)
            }.also { instance = it }
    }
}