package com.bracketcove.rides

import com.bracketcove.ServiceResult
import com.bracketcove.domain.Ride

interface RideService {
    suspend fun getRideIfInProgress(): ServiceResult<Ride?>
    suspend fun updateRide(ride: Ride): ServiceResult<Unit>
}