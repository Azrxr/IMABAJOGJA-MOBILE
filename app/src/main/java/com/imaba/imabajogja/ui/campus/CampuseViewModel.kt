package com.imaba.imabajogja.ui.campus

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.imaba.imabajogja.data.repository.MemberRepository
import com.imaba.imabajogja.data.response.DocumentsResponse
import com.imaba.imabajogja.data.response.ProgramStudyResponse
import com.imaba.imabajogja.data.response.StudyItem
import com.imaba.imabajogja.data.response.StudyMemberResponse
import com.imaba.imabajogja.data.response.StudyPlans
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CampuseViewModel @Inject constructor(private val repository: MemberRepository) : ViewModel() {

    fun getStudyPlans(): LiveData<Result<List<StudyPlans>>> =
        repository.getStudyPlans()

    fun addStudyPlan(universityId: Int, programStudyId: Int): LiveData<Result<String>> {
        return repository.addStudyPlan(universityId, programStudyId)
    }

    fun getUniversity(): LiveData<Result<List<StudyItem>>> = repository.getUniversity()

    fun getFaculty(universityId: Int, search: String? = null): LiveData<Result<List<StudyItem>>> =
        repository.getFaculty(universityId, search)

    fun getProgramStudy(universityId: Int, search: String? = null): LiveData<Result<List<StudyItem>>> =
        repository.getProgramStudy(universityId, search)

    fun deleteStudyPlan(studyPlanId: Int): LiveData<Result<String>> {
        return repository.deleteStudyPlan(studyPlanId)
    }

    fun getDocuments(): LiveData<Result<DocumentsResponse>> =
        repository.getDocuments()

    fun uploadDocument(documentType: String, file: File): LiveData<Result<SuccesResponse>> {
        return repository.uploadDocument(documentType, file)
    }

    fun deleteDocument(field: String): LiveData<Result<SuccesResponse>> {
        return repository.deleteDocument(field)
    }

    fun uploadHomePhoto(photoTitle: String, homePhoto: File): LiveData<Result<SuccesResponse>> {
        return repository.uploadHomePhoto(photoTitle, homePhoto)
    }
    fun uploadPhotoDoc(photoType: String, file: File): LiveData<Result<SuccesResponse>> {
        return repository.uploadPhotoDoc(photoType, file)
    }

    fun deleteHomePhoto(id: Int): LiveData<Result<SuccesResponse>> {
        return repository.deleteHomePhoto(id)
    }

    fun getStudyMember(): LiveData<Result<StudyMemberResponse>> =
        repository.getStudyMember()

    fun deleteStudyMember(): LiveData<Result<SuccesResponse>> {
        return repository.deleteStudyMember()
    }

    fun updateStudyMember(universityId: Int, facultyId: Int ?=null, programStudyId: Int): LiveData<Result<SuccesResponse>> {
        Log.d("ViewModel", "Memanggil updateStudyMember() dengan universityId=$universityId, facultyId=$facultyId, programStudyId=$programStudyId")
        return repository.updateStudyMember(universityId, facultyId, programStudyId)
    }

    fun getAllPrograStudy(
        search : String? = null,
        jenjang: String? = null
    ): LiveData<Result<ProgramStudyResponse>> {
        return repository.getAllProgramStudy(search, jenjang)
    }
}
