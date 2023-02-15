package com.bracketcove.rides

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.domain.UnterUser
import kotlinx.coroutines.flow.Flow

interface RideService {

    fun openRides(): Flow<ServiceResult<List<Ride>>>
    fun rideFlow(): Flow<ServiceResult<Ride?>>
    suspend fun getRideIfInProgress() : ServiceResult<String?>
    suspend fun observeRideById(rideId: String)
    suspend fun observeOpenRides()

    /**
     * If successful, it returns the id of the ride (i.e. channel) which the driver has connected
     * to.
     */
    suspend fun connectDriverToRide(ride: Ride, driver: UnterUser): ServiceResult<String>
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

    suspend fun cancelRide(): ServiceResult<Unit>
    suspend fun completeRide(value: Ride): ServiceResult<Unit>
    suspend fun getOpenRideRequests(driverId: String): ServiceResult<Flow<List<Ride>>>
}