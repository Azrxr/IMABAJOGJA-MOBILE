package com.imaba.imabajogja.data.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.imaba.imabajogja.data.api.ApiService
import com.imaba.imabajogja.data.model.UserModel
import com.imaba.imabajogja.data.model.UserPreference
import com.imaba.imabajogja.data.response.LoginResponse
import com.imaba.imabajogja.data.response.ProfileUpdateResponse
import com.imaba.imabajogja.data.response.RegisterAdminResponse
import com.imaba.imabajogja.data.response.RegisterResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(credential: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(credential, password)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.error) {
                    emit(Result.Error(body.message)) // 🔥 error dari server
                } else {
                    emit(Result.Success(body)) // ✅ sukses
                }
            } else {
                // Parse JSON dari errorBody
                val errorBody = response.errorBody()?.string()
                val parsedError = try {
                    Gson().fromJson(errorBody, LoginResponse::class.java)
                } catch (e: Exception) {
                    null
                }

                val errorMessage = parsedError?.message ?: "Terjadi kesalahan: ${response.code()}"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Terjadi kesalahan: ${e.localizedMessage}"))
        }
    }

/*
    fun _register(username: String, email: String, password: String, passwordConfirmation: String) : LiveData<Result<RegisterResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val register = apiService.register(username, email, password, passwordConfirmation)
                emit(Result.Success(register))

            } catch (e: Exception) {
                Log.e("register","gagal register:  ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    }

 */
    fun register(username: String, email: String, password: String, passwordConfirmation: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(username, email, password, passwordConfirmation)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.error) {
                    emit(Result.Error(body.message)) // 🔥 error dari server
                } else {
                    emit(Result.Success(body)) // ✅ sukses
                }
            } else {
                // Parse JSON dari errorBody
                val errorBody = response.errorBody()?.string()
                val parsedError = try {
                    Gson().fromJson(errorBody, LoginResponse::class.java)
                } catch (e: Exception) {
                    null
                }

                val errorMessage = parsedError?.message ?: "Terjadi kesalahan: ${response.code()}"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Terjadi kesalahan: ${e.localizedMessage}"))
        }
    }
/*
    fun _registerAdm(username: String, fullname: String, phoneNumber: String, email: String, password: String, passwordConfirmation: String) : LiveData<Result<RegisterAdminResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val register = apiService.registerAdmin(username, fullname, phoneNumber, email, password, passwordConfirmation)
                emit(Result.Success(register))

            } catch (e: Exception) {
                Log.e("register","gagal register:  ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    }

 */
    fun registerAdm(username: String, fullname: String, phoneNumber: String, email: String, password: String, passwordConfirmation: String): LiveData<Result<RegisterAdminResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.registerAdmin(username, fullname, phoneNumber, email, password, passwordConfirmation)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.error) {
                    emit(Result.Error(body.message)) // 🔥 error dari server
                } else {
                    emit(Result.Success(body)) // ✅ sukses
                }
            } else {
                // Parse JSON dari errorBody
                val errorBody = response.errorBody()?.string()
                val parsedError = try {
                    Gson().fromJson(errorBody, LoginResponse::class.java)
                } catch (e: Exception) {
                    null
                }

                val errorMessage = parsedError?.message ?: "Terjadi kesalahan: ${response.code()}"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error("Terjadi kesalahan: ${e.localizedMessage}"))
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

    suspend fun getUserToken(): String {
        return userPreference.getToken()
    }

    fun updatePassword(
        currentPassword: String,
        newPassword: String,
        passwordConfirmation: String
    ): LiveData<Result<ProfileUpdateResponse>> {
        return liveData {
            try {
                val response = apiService.updatePassword(
                    currentPassword, newPassword, passwordConfirmation
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

    fun admUpdatePassword(
        currentPassword: String,
        newPassword: String,
        passwordConfirmation: String
    ): LiveData<Result<SuccesResponse>> {
        return liveData {
            try {
                val response = apiService.updateAdmPassword(
                    currentPassword, newPassword, passwordConfirmation
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