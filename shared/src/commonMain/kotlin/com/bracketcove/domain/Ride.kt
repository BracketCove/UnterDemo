package com.bracketcove.domain

data class Ride(
    val rideId: String = "",
    val driverId: String? = "",
    val passengerId: String = "",
    val status: String = RideStatus.SEARCHING_FOR_DRIVER.value,
    val destinationLatitude: Double = 0.0,
    val destinationLongitude: Double = 0.0,
    val destinationAddress: String = "",
    val passengerLatitude: Double = 0.0,
    val passengerLongitude: Double = 0.0,
    val passengerName: String = "",
    val passengerAddress: String = "",
    val passengerAvatarUrl: String = "",
    val driverLatitude: Double = 0.0,
    val driverLongitude: Double = 0.0,
    val driverName: String = "",
    val driverAvatarUrl: String = "",
    val createdAt: String = "",
    val updatedAT: String = ""
    )
