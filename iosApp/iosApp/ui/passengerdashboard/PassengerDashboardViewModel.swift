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
    @Published var showDashboard = false
    @Published var places = [PlaceViewModel]()
    
    private var logoutUser: LogOutUser? = nil
    
    init (_ logoutUser: LogOutUser?) {
        self.logoutUser = logoutUser
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
    
    func setLogoutUser(logoutUser: LogOutUser) {
        self.logoutUser = logoutUser
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

