package com.imaba.imabajogja.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.imaba.imabajogja.data.model.UserModel
import com.imaba.imabajogja.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (private val repository: LoginRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
    fun getUserToken() = viewModelScope.launch {
        val token = repository.getUserToken()
        Log.d("MainViewModel", "Token: $token")
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}