package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.domain.UnterUser
import com.bracketcove.rides.RideService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRideService : RideService {

    private val openRides: Flow<ServiceResult<List<Ride>>> = MutableStateFlow<ServiceResult<List<Ride>>>(ServiceResult.Value(emptyList()))
    private val rideState: Flow<ServiceResult<Ride?>> = MutableStateFlow<ServiceResult<Ride?>>(ServiceResult.Value(null))

    override fun openRides(): Flow<ServiceResult<List<Ride>>> {
        return openRides
    }

    override fun rideFlow(): Flow<ServiceResult<Ride?>> {
        return rideState
    }

    override suspend fun getRideIfInProgress(): ServiceResult<String?> {
        return ServiceResult.Value(null)
    }

    override suspend fun observeRideById(rideId: String) {

    }

    override suspend fun observeOpenRides() {
    }

    override suspend fun connectDriverToRide(ride: Ride, driver: UnterUser): ServiceResult<String> {
        return ServiceResult.Value("")
    }

    override suspend fun createRide(
        passengerId: String,
        passengerName: String,
        passengerLat: Double,
        passengerLon: Double,
        passengerAvatarUrl: String,
        destinationAddress: String,
        destLat: Double,
        destLon: Double
    ): ServiceResult<String> {
        return ServiceResult.Value("123456")
    }

    override suspend fun cancelRide(): ServiceResult<Unit> {
        return ServiceResult.Value(Unit)
    }

    override suspend fun completeRide(ride: Ride): ServiceResult<Unit> {
        return ServiceResult.Value(Unit)

    }

    override suspend fun advanceRide(rideId: String, newState: String): ServiceResult<Unit> {
        return ServiceResult.Value(Unit)
    }

    override suspend fun updateDriverLocation(
        ride: Ride,
        lat: Double,
        lon: Double
    ): ServiceResult<Unit> {
        return ServiceResult.Value(Unit)
    }

    override suspend fun updatePassengerLocation(
        ride: Ride,
        lat: Double,
        lon: Double
    ): ServiceResult<Unit> {
        return ServiceResult.Value(Unit)
    }
}