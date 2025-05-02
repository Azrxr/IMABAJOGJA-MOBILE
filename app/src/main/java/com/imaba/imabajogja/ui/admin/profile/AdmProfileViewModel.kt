package com.imaba.imabajogja.ui.admin.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.imaba.imabajogja.data.repository.AdminRepository
import com.imaba.imabajogja.data.response.AdmProfileResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AdmProfileViewModel @Inject constructor(private val repository: AdminRepository) : ViewModel() {

    fun getProfileData(): LiveData<Result<AdmProfileResponse>> {
        return repository.getProfile()
    }

    fun updateProfile(
        username: String, email: String,
        fullname: String, phoneNumber: String,
        provinceId: Int, regencyId: Int, districtId: Int, fullAddress: String,
    ): LiveData<Result<SuccesResponse>> {
        return repository.updateProfile(
            username, email,
            fullname, phoneNumber,
            provinceId, regencyId, districtId, fullAddress,
        )

    }

    fun updatePhotoProfile(photoFile: File): LiveData<Result<SuccesResponse>> {
        return repository.updatePhotoProfile(photoFile)
    }

}