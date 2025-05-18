package com.example.coffeeexplorer.networking

import com.example.coffeeexplorer.domain.Coffee
import retrofit2.http.GET

interface CoffeeApi {
    @GET("coffee/hot")
    suspend fun getCoffeeList(): List<Coffee>
}
