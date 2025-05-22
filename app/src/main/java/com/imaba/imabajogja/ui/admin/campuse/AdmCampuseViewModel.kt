package com.imaba.imabajogja.ui.admin.campuse

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.imaba.imabajogja.data.repository.AdminRepository
import com.imaba.imabajogja.data.response.ImportMemberResponse
import com.imaba.imabajogja.data.response.MemberDetailResponse
import com.imaba.imabajogja.data.response.ProgramStudyImportResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.http.Field
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AdmCampuseViewModel @Inject constructor(private val repository: AdminRepository) : ViewModel(){

    fun getMemberDetail(memberId: Int) : LiveData<Result<MemberDetailResponse>> {
        return repository.getMemberDetailAdm(memberId)
    }

    fun addStudyPlan(
        memberId: Int, universityId: Int, programStudyId: Int
    ): LiveData<Result<SuccesResponse>> {
        return repository.addStudyPlaneAdm(memberId, universityId, programStudyId)
    }

    fun updateStatusPlan(
        memberId: Int, studyPlanId: Int, status: String
    ): LiveData<Result<SuccesResponse>> {
        return repository.updateStudyPlaneAdm(memberId, studyPlanId, status)
    }

    fun deleteDocument(memberId: Int, field: String) : LiveData<Result<SuccesResponse>> {
        return repository.deleteDocument(memberId, field)
    }

    fun deleteHomePhoto(id: Int) : LiveData<Result<SuccesResponse>> {
       return repository.deleteHomePhoto(id)
    }

    fun uploadDocument(memberId: Int, docId: Int, documentType: String, file: File): LiveData<Result<SuccesResponse>> {
        return repository.uploadDocument(memberId, docId, documentType, file)
    }

    fun uploadHomePhoto(memberId: Int, photoTitle: String, homePhoto: File): LiveData<Result<SuccesResponse>> {
        return repository.uploadHomePhoto(memberId, photoTitle, homePhoto)
    }
    fun uploadPhotoDoc(memberId: Int, docId: Int, photoType: String, file: File): LiveData<Result<SuccesResponse>> {
        return repository.uploadPhotoDoc(memberId, docId, photoType, file)
    }

    fun importProgramStudy(
        file: File,
    ): LiveData<Result<ProgramStudyImportResponse>> {
        return repository.importProgramStudy(file)
    }

    fun updateStudyMember(memberId: Int, universityId: Int, facultyId: Int ?=null, programStudyId: Int): LiveData<Result<SuccesResponse>> {
        Log.d("ViewModel", "Memanggil updateStudyMember() dengan universityId=$universityId, facultyId=$facultyId, programStudyId=$programStudyId")
        return repository.updateStudyMemberAdm(memberId, universityId, facultyId, programStudyId)
    }

}