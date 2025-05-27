package com.imaba.imabajogja.data.pagging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.imaba.imabajogja.data.api.ApiConfig
import com.imaba.imabajogja.data.api.ApiService
import com.imaba.imabajogja.data.response.DataItemMember
import okio.IOException
import retrofit2.HttpException

class MemberPagingSource(
    private val apiService: ApiService,
    private val search: String?,
    private val generation: List<String>?,
    private val memberType: List<String>?,
    private val planStatus: String?
): PagingSource<Int, DataItemMember>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItemMember> {
        return try{
            val currentPage = params.key ?: 1
            val response = apiService.getMembers(
                search = search,
                generation = generation,
                memberType = memberType,
                planStatus = planStatus,)
            Log.d("Members", "paging: API Response: ${response.data.data}") // ✅ Debugging

            LoadResult.Page(
                data = response.data.data,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (currentPage < response.data.lastPage) currentPage + 1 else null
            )
        } catch (e: IOException){
            Log.e("Members", " paging : Error Network: ${e.message}")
            LoadResult.Error(e)
        } catch (e: HttpException){
            Log.e("Members", " paging : Error Server: ${e.message}")
            LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, DataItemMember>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
