//
//  PassengerDashboardScreen.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-19.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct PassengerDashboardView: View {
    
    private var dependencyLocator: DependencyLocator
    @StateObject var viewModel = PassengerDashboardViewModel(nil, nil)
    
    init(dependencyLocator: DependencyLocator) {
        self.dependencyLocator = dependencyLocator
    }
    
    var body: some View {
        GeometryReader { geometry in
            if viewModel.showMapView {
                RideActiveView(viewModel, mapHeight: geometry.size.height * 0.4)
            } else {
                DestinationSearchView(viewModel)
            }
        }.onAppear {
            viewModel.setLogoutUser(logoutUser: dependencyLocator.logoutUser)
            viewModel.setRideService(rideService: dependencyLocator.rideService)
        }
    }
}

struct PassengerDashboardScreen_Previews: PreviewProvider {
    static var previews: some View {
        PassengerDashboardView(dependencyLocator: getFakeLocator())
    }
}
