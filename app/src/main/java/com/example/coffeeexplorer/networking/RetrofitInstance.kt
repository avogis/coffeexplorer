package com.example.coffeeexplorer.networking

object ApiClient {
    private const val BASE_URL = "https://api.sampleapis.com/"

    val coffeeApi: CoffeeApi by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(CoffeeApi::class.java)
    }
}
