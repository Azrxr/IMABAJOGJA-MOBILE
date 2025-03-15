package com.imaba.imabajogja.ui.campus

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.imaba.imabajogja.data.repository.MemberRepository
import com.imaba.imabajogja.data.response.DocumentsResponse
import com.imaba.imabajogja.data.response.StudyItem
import com.imaba.imabajogja.data.response.StudyPlans
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
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
}
