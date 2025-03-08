package com.imaba.imabajogja.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.imaba.imabajogja.data.repository.MemberRepository
import com.imaba.imabajogja.data.response.ProfileResponse
import com.imaba.imabajogja.data.response.ProfileUpdateResponse
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: MemberRepository) : ViewModel() {

    fun getProfileData(): LiveData<Result<ProfileResponse>> {
        return repository.getProfile()
    }

    fun updateProfile(
        username: String, email: String,
//        currentPassword: String, newPassword: String, passwordConfirmation: String,
        fullname: String, phoneNumber: String, profileImg: String,
        provinceId: Int, regencyId: Int, districtId: Int, fullAddress: String, kodePos: String,
        agama: String, nisn: String, tempat: String, tanggalLahir: String, gender: String,
        schollOrigin: String, tahunLulus: Int,
    ): LiveData<Result<ProfileUpdateResponse>> {
        return repository.updateProfile(
            username, email,
//            currentPassword, newPassword, passwordConfirmation,
            fullname, phoneNumber, profileImg,
            provinceId, regencyId, districtId, fullAddress, kodePos,
            agama, nisn, tempat, tanggalLahir, gender,
            schollOrigin, tahunLulus
        )

    }
}