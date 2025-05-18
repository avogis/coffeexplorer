package com.example.coffeeexplorer.networking

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.coffeeexplorer.domain.Coffee

class CoffeePagingSource(
    private val api: CoffeeApi
) : PagingSource<Int, Coffee>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Coffee> {
        return try {
            val coffees = api.getCoffeeList()
            LoadResult.Page(
                data = coffees,
                prevKey = null,
                nextKey = null // API doesn’t support real pagination :(
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Coffee>): Int? = null
}

