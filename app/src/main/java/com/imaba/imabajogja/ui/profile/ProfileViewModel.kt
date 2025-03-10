package com.imaba.imabajogja.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.imaba.imabajogja.data.repository.MemberRepository
import com.imaba.imabajogja.data.response.ProfileResponse
import com.imaba.imabajogja.data.response.ProfileUpdateResponse
import com.imaba.imabajogja.data.response.WilayahItem
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: MemberRepository) : ViewModel() {

    fun getProfileData(): LiveData<Result<ProfileResponse>> {
        return repository.getProfile()
    }

    fun updateProfile(
        username: String, email: String,
        fullname: String, phoneNumber: String,
        provinceId: Int, regencyId: Int, districtId: Int, fullAddress: String, kodePos: String,
        agama: String, nisn: String, tempat: String, tanggalLahir: String, gender: String,
        schollOrigin: String, tahunLulus: Int,
    ): LiveData<Result<ProfileUpdateResponse>> {
        return repository.updateProfile(
            username, email,
            fullname, phoneNumber,
            provinceId, regencyId, districtId, fullAddress, kodePos,
            agama, nisn, tempat, tanggalLahir, gender,
            schollOrigin, tahunLulus
        )

    }

    fun updatePhotoProfile(photoFile: File): LiveData<Result<ProfileUpdateResponse>> {
        return repository.updatePhotoProfile(photoFile)
    }

    fun updatePassword(
        currentPassword: String, newPassword: String, passwordConfirmation: String,
    ): LiveData<Result<ProfileUpdateResponse>> {
        return repository.updatePassword(
            currentPassword, newPassword, passwordConfirmation,
        )
    }

    fun getProvinces(): LiveData<Result<List<WilayahItem>>> = repository.getProvinces()

    fun getRegencies(provinceId: Int, search: String? = null): LiveData<Result<List<WilayahItem>>> =
        repository.getRegencies(provinceId, search)

    fun getDistricts(regencyId: Int, search: String? = null): LiveData<Result<List<WilayahItem>>> =
        repository.getDistricts(regencyId, search)
}