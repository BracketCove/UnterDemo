package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.rides.RideService

class StreamRideService: RideService {
    override suspend fun getRideIfInProgress(): ServiceResult<Ride?> {
        return ServiceResult.Value(null)
    }

    override suspend fun updateRide(ride: Ride): ServiceResult<Ride?> {
        TODO("Not yet implemented")
    }

    override suspend fun createRide(
        passengerId: String,
        latitude: Double,
        longitude: Double,
        address: String
    ): ServiceResult<Ride> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelRide(ride: Ride): ServiceResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun completeRide(value: Ride): ServiceResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getRideByPassengerId(passengerId: String): ServiceResult<Ride?> {
        TODO("Not yet implemented")
    }
}