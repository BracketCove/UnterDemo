//
//  PassengerDashboardUiState.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

enum PassengerDashboardUiState {
    case rideInactive
    case loading
    case error
    
    struct SearchingForDriver {
        let passengerLat: Double
        let passengerLon: Double
        let destinationAddress: String
    }
    
    struct PassengerPickUp {
        let passengerLat: Double
        let passengerLon: Double
        let destinationAddress: String
        let destinationLat: Double
        let destinationLon: Double
        let driverName: String
        let driverAvatar: String
        let driverLat: Double
        let driverLon: Double
        let totalMessages: Int
    }
    
    struct EnRoute {
        let passengerLat: Double
        let passengerLon: Double
        let destinationAddress: String
        let destinationLat: Double
        let destinationLon: Double
        let driverName: String
        let driverAvatar: String
        let driverLat: Double
        let driverLon: Double
        let totalMessages: Int
    }
    
    struct Arrived {
        let passengerLat: Double
        let passengerLon: Double
        let destinationAddress: String
        let destinationLat: Double
        let destinationLon: Double
        let driverName: String
        let driverAvatar: String
        let totalMessages: Int
    }
}
