package com.bracketcove.rides

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.domain.UnterUser
import kotlinx.coroutines.flow.Flow

interface RideService {
    suspend fun getRideIfInProgress(): Flow<ServiceResult<Ride?>>
    suspend fun updateRide(ride: Ride): ServiceResult<Unit>
    suspend fun createRide(
        passengerId: String,
        latitude: Double,
        longitude: Double,
        destinationAddress: String,
        avatarUrl: String
    ): Flow<ServiceResult<Ride?>>

    suspend fun cancelRide(ride: Ride): ServiceResult<Unit>
    suspend fun completeRide(value: Ride): ServiceResult<Unit>
    suspend fun getRideByPassengerId(passengerId: String): ServiceResult<Ride?>
    suspend fun getRideByDriverId(driverId: String): ServiceResult<Ride?>
    suspend fun getOpenRideRequests(driverId: String): ServiceResult<Flow<List<Ride>>>
}