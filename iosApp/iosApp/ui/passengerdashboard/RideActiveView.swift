//
//  RideActiveView.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct RideActiveView: View {
    
    private var viewModel: PassengerDashboardViewModel
    private var mapHeight: Double

    init(
        _ viewModel: PassengerDashboardViewModel,
        mapHeight: Double
    ) {
        self.viewModel = viewModel
        self.mapHeight = mapHeight
    }
    
    var body: some View {
        VStack {
            HStack {
                Text(NSLocalizedString("unter", comment: "App name.")).frame(alignment: .leading)
                    .font(.custom("poppins_semi_bold", size: 18))
                    .foregroundColor(.black)
                    .padding(.leading, 16)
                
                Spacer()
                
                Button(NSLocalizedString("logout", comment: "")) {
                    viewModel.attemptLogout()
                }.padding(.trailing, 16)
            }.padding(.top, 16)
            
            GoogleMapView()
                .edgesIgnoringSafeArea(.top)
                .frame(height: mapHeight)
            
            DestinationBarView(
                {}
            )
        }
    }
}


