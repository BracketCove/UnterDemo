package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.rides.RideService
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.flow.Flow

class StreamRideService(
    private val client: ChatClient
): RideService {
    override suspend fun getRideIfInProgress(): ServiceResult<Flow<Ride?>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateRide(ride: Ride): ServiceResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createRide(
        passengerId: String,
        latitude: Double,
        longitude: Double,
        destinationAddress: String,
        avatarUrl: String
    ): ServiceResult<Flow<Ride?>> {
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

    override suspend fun getRideByDriverId(driverId: String): ServiceResult<Ride?> {
        TODO("Not yet implemented")
    }

    override suspend fun getOpenRideRequests(driverId: String): ServiceResult<Flow<List<Ride>>> {
        TODO("Not yet implemented")
    }

}