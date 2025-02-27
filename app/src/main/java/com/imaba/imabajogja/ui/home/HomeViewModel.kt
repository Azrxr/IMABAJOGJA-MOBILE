package com.imaba.imabajogja.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.repository.MemberRepository
import com.imaba.imabajogja.data.response.HomeResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (private val repository: MemberRepository) : ViewModel() {

    fun getHomeData(): LiveData<Result<HomeResponse>> {
        return repository.getHomeData()
    }
}