//
//  SwiftStreamRideService.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-24.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class SwiftStreamRideService {
    func observeRideById(rideId: String) async -> Void {
        
    }
    
    func getRideIfInProgress() async -> String? {
        return nil
    }
    
    func createRide(
        passengerId: String,
        passengerName: String,
        passengerLat: Double,
        passengerLon: Double,
        passengerAvatarUrl: String,
        destinationAddress: String,
        destLat: Double,
        destLon: Double
    ) async -> String {
        return ""
    }
    
    func cancelRide() async -> Bool {
     return true
    }
    
    func updatePassengerLocation(ride: Ride, lat: Double, lon: Double) async -> Bool {
        return true
    }
}
