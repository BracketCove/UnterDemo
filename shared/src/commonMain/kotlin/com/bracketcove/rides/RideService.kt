package com.bracketcove.rides

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride

interface RideService {
    fun getRideIfInProgress(): ServiceResult<Ride?>
    fun updateRide(ride: Ride): ServiceResult<Unit>
}