//
//  DestinationSearchView.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-23.
//  Copyright © 2023 orgName. All rights reserved.
// Credit to azamsharp on Github for this approach to autocomplete

import SwiftUI
import MapKit

struct DestinationSearchView: View {
    
    
    @StateObject private var locationManager = LocationManager()
    @State private var search: String = ""
    private var viewModel: PassengerDashboardViewModel
    
    init(
        _ viewModel: PassengerDashboardViewModel
    ) {
        self.viewModel = viewModel
    }
    
    
    var body: some View {
        VStack {
            List(viewModel.places) { place in
                Text("\(place.name), \(place.address)")
                    .onTapGesture {
                        viewModel.handleSelectedPlace(place)
                    }
            }
            
        }.searchable(text: $search)
            .onChange(of: search, perform: { searchText in
                
                if !searchText.isEmpty {
                    print("Searching")
                    viewModel.search(text: searchText, region: locationManager.region)
                } else {
                    viewModel.places = []
                }
            })
            .navigationTitle(NSLocalizedString("enter_a_destination", comment: "App name."))
    }
}

