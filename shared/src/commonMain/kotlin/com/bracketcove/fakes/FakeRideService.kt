package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride
import com.bracketcove.rides.RideService

class FakeRideService : RideService {
    override fun getRideIfInProgress(): ServiceResult<Ride?> {
        TODO("Not yet implemented")
    }

    override fun updateRide(ride: Ride): ServiceResult<Unit> {
        TODO("Not yet implemented")
    }
}