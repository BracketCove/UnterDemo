package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.domain.RideStatus
import com.bracketcove.domain.User
import com.bracketcove.rides.RideService

class FakeRideService : RideService {
    override suspend fun getRideIfInProgress(): ServiceResult<Ride?> {
        return ServiceResult.Success(testRide().copy(
            status = RideStatus.PASSENGER_PICK_UP.value,
            driverId = "654321",
            passengerId = "123456",
            destinationLatitude = 51.0543,
            destinationLongitude = -114.20,
            destinationAddress = "101 9 Avenue SW " +
                    "Calgary, Alberta " +
                    "T2P 1J9"
        ))

     //   return ServiceResult.Success(null)
    }

    override suspend fun updateRide(ride: Ride): ServiceResult<Ride?> {
        return ServiceResult.Success(testRide())
    }

    override suspend fun createRide(
        passengerId: String,
        latitude: Double,
        longitude: Double,
        address: String
    ): ServiceResult<Ride> {
        return ServiceResult.Success(testRide().copy(
            passengerId = passengerId,
            destinationLatitude = latitude,
            destinationLongitude = longitude,
            destinationAddress = address
        ))
    }

    override suspend fun cancelRide(ride: Ride): ServiceResult<Unit> {
        return ServiceResult.Success(Unit)
    }

    override suspend fun completeRide(value: Ride): ServiceResult<Unit> {
        return ServiceResult.Success(Unit)
    }
}

private fun testRide(): Ride = Ride(
    rideId = "123456",
    driverId = null,
    passengerId = "123456789",
    status = RideStatus.SEARCHING_FOR_DRIVER.value,
    destinationLatitude = 0.0,
    destinationLongitude = 0.0,
    createdAt = "Some time before",
    updatedAT = "Some time after"
)