package com.imaba.imabajogja.ui.member

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.imaba.imabajogja.data.repository.MemberRepository
import com.imaba.imabajogja.data.response.DataItemMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val repository: MemberRepository
) : ViewModel() {
    val members: Flow<PagingData<DataItemMember>> = repository.getMembers()
        .onEach { Log.d("Members", " viewModel: Data diterima di ViewModel: $it") }
        .cachedIn(viewModelScope)
}