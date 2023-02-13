package com.bracketcove.rides

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.domain.UnterUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface RideService {

    fun rideFlow(): Flow<ServiceResult<Ride?>>
    suspend fun getRideIfInProgress() : ServiceResult<String?>
    suspend fun observeRideById(rideId: String)
    suspend fun updateRide(ride: Ride): ServiceResult<Unit>
    suspend fun createRide(
        passengerId: String,
        passengerName: String,
        passengerLat: Double,
        passengerLon: Double,
        passengerAvatarUrl: String,
        destinationAddress: String,
        destLat: Double,
        destLon: Double,
    ): ServiceResult<String>

    suspend fun cancelRide(ride: Ride): ServiceResult<Unit>
    suspend fun completeRide(value: Ride): ServiceResult<Unit>
    suspend fun getRideByPassengerId(passengerId: String): ServiceResult<Ride?>
    suspend fun getRideByDriverId(driverId: String): ServiceResult<Ride?>
    suspend fun getOpenRideRequests(driverId: String): ServiceResult<Flow<List<Ride>>>
}