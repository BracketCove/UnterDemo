package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.domain.RideStatus
import com.bracketcove.rides.RideService

class FakeRideService : RideService {
    override suspend fun getRideIfInProgress(): ServiceResult<Ride?> {
      //  return ServiceResult.Success(testRide())
        return ServiceResult.Success(null)
    }

    override suspend fun updateRide(ride: Ride): ServiceResult<Ride?> {
        return ServiceResult.Success(testRide())
    }

    override suspend fun cancelRide(ride: Ride): ServiceResult<Unit> {
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