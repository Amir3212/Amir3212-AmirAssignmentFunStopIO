package com.example.amirassignment.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.amirassignment.data.local.ProductEntity
import kotlinx.coroutines.delay


class DelayedProductPagingSource(
    private val delegate: PagingSource<Int, ProductEntity>,
    private val appendDelayMs: Long = APPEND_LOAD_DELAY_MS,
) : PagingSource<Int, ProductEntity>() {

    override fun getRefreshKey(state: PagingState<Int, ProductEntity>): Int? =
        delegate.getRefreshKey(state)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductEntity> {
        if (params is LoadParams.Append || params is LoadParams.Prepend) {
            delay(appendDelayMs)
        }
        return delegate.load(params)
    }

    companion object {
        const val APPEND_LOAD_DELAY_MS = 1_000L
    }
}
