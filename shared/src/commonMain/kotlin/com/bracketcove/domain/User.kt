package com.bracketcove.domain

data class User(
    val userId: String = "",
    val username: String = "",
    val type: String = "PASSENGER",
    val status: String = "",
    val avatarPhotoUrl: String = "",
    val vehiclePhotoUrl: String = "",
    val vehicleDescription: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val createdAt: String = "",
    val updatedAt: String = ""
)