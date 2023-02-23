//
//  PassengerDashboardViewModel.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-21.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import MapKit

class PassengerDashboardViewModel : ObservableObject {
    @Published var places = [PlaceViewModel]()
    @Published var showMapView = false
    
    @Published var uiState: PassengerDashboardUiState = .loading
    
    private var logoutUser: LogOutUser? = nil
    private var getUser: GetUser? = nil
    private var rideService: RideService? = nil
    
    func setDependencies(dependencyLocator: DependencyLocator) {
        self.logoutUser = dependencyLocator.logoutUser
        self.getUser = dependencyLocator.getUser
        self.rideService = dependencyLocator.rideService
    }
    
    func setRideService(rideService: RideService) {
        self.rideService = rideService
    }
    
    func handleSelectedPlace(_ place: PlaceViewModel) {
        print("\(place.lat) \(place.lon)")
    }
    
    func search(text: String, region: MKCoordinateRegion) {
        
        let searchRequest = MKLocalSearch.Request()
        searchRequest.naturalLanguageQuery = text
        searchRequest.region = region
        
        let search = MKLocalSearch(request: searchRequest)
        
        search.start { response, error in
            
            guard let response = response else {
                print("Error: \(error?.localizedDescription ?? "Unknown error")")
                return
            }
            
            self.places = response.mapItems.map(PlaceViewModel.init)
        }
    }
        
    func attemptLogout() {
        //            logoutUser?.logout(user: user) {
        //                value, error in
        //                if let result = value as? ServiceResultValue {
        //
        //                    if result.value == nil {
        //                       // self.showError = true
        //                    }
        //                    else {
        //                        self.showDashboard = true
        //                    }
        //                }
        //
        //                if error != nil {
        //                   // self.showError = true
        //                }
        //            }
    }
}

