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
    @StateObject var viewModel = PassengerDashboardViewModel(nil)
    
    init(dependencyLocator: DependencyLocator) {
        self.dependencyLocator = dependencyLocator
    }
    
    var body: some View {
        GeometryReader { geometry in
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
                    .frame(height: geometry.size.height * 0.4)
                
                DestinationBarView(
                    {}
                )
                

            }
        }.onAppear {
            viewModel.setLogoutUser(logoutUser: dependencyLocator.logoutUser)
        }
    }
}

struct PassengerDashboardScreen_Previews: PreviewProvider {
    static var previews: some View {
        PassengerDashboardView(dependencyLocator: getFakeLocator())
    }
}
