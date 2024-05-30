package com.example.sqldatabase.models

data class ProductData(
    val id: Int,
    val image: ByteArray,
    val name: String,
    val price: String,
    val weight: String,
    val description: String
)
