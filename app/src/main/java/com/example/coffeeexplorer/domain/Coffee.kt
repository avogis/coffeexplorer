package com.example.coffeeexplorer.domain

data class Coffee(
    val id: Int,
    val title: String,
    val description: String,
    val image: String,
    val ingredients: List<String>
)
