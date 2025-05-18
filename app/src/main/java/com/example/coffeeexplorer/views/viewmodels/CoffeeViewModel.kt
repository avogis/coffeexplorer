package com.example.coffeeexplorer.views.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.coffeeexplorer.networking.ApiClient
import com.example.coffeeexplorer.networking.CoffeePagingSource

class CoffeeViewModel : ViewModel() {
    val pager = Pager(PagingConfig(pageSize = 20)) {
        CoffeePagingSource(ApiClient.coffeeApi)
    }.flow.cachedIn(viewModelScope)
}

