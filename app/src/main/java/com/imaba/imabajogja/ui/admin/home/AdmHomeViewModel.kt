package com.imaba.imabajogja.ui.admin.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.imaba.imabajogja.data.repository.AdminRepository
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdmHomeViewModel @Inject constructor (private val repository: AdminRepository) : ViewModel() {

    fun getHomeData(): LiveData<Result<HomeResponse>> {
        return repository.getHomeData()
    }

    fun updateProfileOrganization(
        title: String,
        description: String,
        vision: String,
        mission: String,
        address: String,
        email: String,
        phoneNumber: String,
        phoneNumber2: String
    ) : LiveData<Result<SuccesResponse>> {
        return repository.updateProfileOrganization(
            title,
            description,
            vision,
            mission,
            address,
            email,
            phoneNumber,
            phoneNumber2
        )
    }
}