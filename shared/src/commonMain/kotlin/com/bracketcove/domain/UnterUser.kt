package com.bracketcove.domain

data class UnterUser(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val type: String = UserType.PASSENGER.value,
    val status: String = UserStatus.INACTIVE.value,
    val avatarPhotoUrl: String = "",
    val vehiclePhotoUrl: String? = "",
    val vehicleDescription: String? = "",
    val registeredAsDriver: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val createdAt: String = "",
    val updatedAt: String = ""
)