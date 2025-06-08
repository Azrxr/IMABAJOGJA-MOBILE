package com.imaba.imabajogja.ui.admin.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.imaba.imabajogja.data.repository.AdminRepository
import com.imaba.imabajogja.data.response.ImportMemberResponse
import com.imaba.imabajogja.data.response.MembersResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AdmMemberViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow<String?>(null)
    private val generationFilters = MutableStateFlow<Set<String>>(emptySet())
    private val memberTypeFilters = MutableStateFlow<Set<String>>(emptySet())
    private val planStatus = MutableStateFlow<Set<String?>>(emptySet())

    val members = combine(
        searchQuery, generationFilters, memberTypeFilters
    ) { search, generations, memberTypes ->
        Triple(search, generations, memberTypes)
    }.flatMapLatest { (search, generations, memberTypes) ->
        repository.listMembers(search, generations.toList(), memberTypes.toList(),
        )
    }.cachedIn(viewModelScope)

    fun setSearchQuery(query: String?) {
        searchQuery.value = query
    }

    fun addGenerationFilter(generation: String) {
        generationFilters.value = generationFilters.value + generation
    }

    fun removeGenerationFilter(generation: String) {
        generationFilters.value = generationFilters.value - generation
    }

    fun clearGenerationFilters() {
        generationFilters.value = emptySet()
    }

    fun getCurrentGenerationFilters(): Set<String> = generationFilters.value

    fun addMemberTypeFilter(memberType: String) {
        memberTypeFilters.value = memberTypeFilters.value + memberType
    }

    fun removeMemberTypeFilter(memberType: String) {
        memberTypeFilters.value = memberTypeFilters.value - memberType
    }

    fun clearMemberTypeFilters() {
        memberTypeFilters.value = emptySet()
    }

    fun getCurrentMemberTypeFilters(): Set<String> = memberTypeFilters.value

    fun isFilterActive(): Boolean {
        return generationFilters.value.isNotEmpty() || memberTypeFilters.value.isNotEmpty()
    }

    fun clearFilters() {
        clearGenerationFilters()
        clearMemberTypeFilters()
    }

    fun getMemberSummary(): LiveData<Result<MembersResponse>> {
        return repository.getMemberSummary()
    }

    fun deleteMember(memberId: Int): LiveData<Result<SuccesResponse>> {
        return repository.deleteMember(memberId)
    }

    fun updateMemberAdm( memberId: Int,
        fullname: String, phoneNumber: String,
        provinceId: Int, regencyId: Int, districtId: Int, fullAddress: String, kodePos: String,
        agama: String, nisn: String, tempat: String, tanggalLahir: String, gender: String,
        schollOrigin: String, tahunLulus: Int, angkatan: String, memberType: String
    ): LiveData<Result<SuccesResponse>> {
        return repository.updateMemberAdm( memberId,
            fullname, phoneNumber,
            provinceId, regencyId, districtId, fullAddress, kodePos,
            agama, nisn, tempat, tanggalLahir, gender,
            schollOrigin, tahunLulus, angkatan, memberType
        )

    }

    fun updateMemberPhotoProfile(memberId: Int, photoFile: File): LiveData<Result<SuccesResponse>> {
        return repository.updateMemberPhotoProfileAdm(memberId, photoFile)
    }

    fun importMember(
        file: File,
    ): LiveData<Result<ImportMemberResponse>> {
        return repository.importMember(file)
    }

    fun exportMembersRaw(generations: List<String>, memberTypes: List<String>): Result<ResponseBody> {
        return runBlocking {
            repository.exportMembers(generations, memberTypes)
        }
    }


}