package com.bracketcove.rides

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.domain.User

interface RideService {
    suspend fun getRideIfInProgress(): ServiceResult<Ride?>
    suspend fun updateRide(ride: Ride): ServiceResult<Ride?>
    suspend fun createRide(
        passengerId: String,
        latitude: Double,
        longitude: Double,
        address: String
    ): ServiceResult<Ride>

    suspend fun cancelRide(ride: Ride): ServiceResult<Unit>
    suspend fun completeRide(value: Ride): ServiceResult<Unit>
    suspend fun getRideByPassengerId(passengerId: String): ServiceResult<Ride?>
}