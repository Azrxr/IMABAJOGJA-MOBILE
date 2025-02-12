package com.imaba.imabajogja.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imaba.imabajogja.data.model.UserModel
import com.imaba.imabajogja.data.repository.LoginRepository
import com.imaba.imabajogja.data.response.LoginResponse
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(credential: String, password: String): LiveData<Result<LoginResponse>> {
        _isLoading.value = true
        return loginRepository.login(credential, password)

    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            loginRepository.saveSession(user)
        }
    }
}