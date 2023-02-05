package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.rides.RideService

class FakeRideService : RideService {
    override suspend fun getRideIfInProgress(): ServiceResult<Ride?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateRide(ride: Ride): ServiceResult<Unit> {
        TODO("Not yet implemented")
    }
}