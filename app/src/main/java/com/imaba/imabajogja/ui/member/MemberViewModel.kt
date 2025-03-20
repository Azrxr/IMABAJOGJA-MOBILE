package com.imaba.imabajogja.ui.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.imaba.imabajogja.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val repository: MemberRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow<String?>(null)
    private val generationFilters = MutableStateFlow<Set<String>>(emptySet())
    private val memberTypeFilters = MutableStateFlow<Set<String>>(emptySet())

    val members = combine(
        searchQuery, generationFilters, memberTypeFilters
    ) { search, generations, memberTypes ->
        Triple(search, generations, memberTypes)
    }.flatMapLatest { (search, generations, memberTypes) ->
        repository.getMembers(search, generations.toList(), memberTypes.toList())
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
}
