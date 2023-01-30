package com.bracketcove.domain

data class Ride(
    val rideId: String = "",
    val driverId: String = "",
    val passengerId: String = "",
    val status: String = "",
    val destinationLatitude: Double = 0.0,
    val destinationLongitude: Double = 0.0,
    val createdAt: String = "",
    val updatedAT: String = ""
)
