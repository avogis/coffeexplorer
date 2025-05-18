package com.example.coffeeexplorer.networking

import com.example.coffeeexplorer.domain.Coffee
import retrofit2.http.GET

interface CoffeeApi {
    @GET("coffee/hot") // or "coffee/iced"
    suspend fun getCoffeeList(): List<Coffee>
}
