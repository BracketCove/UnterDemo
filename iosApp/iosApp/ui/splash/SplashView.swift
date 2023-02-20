//
//  SplashScreen.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-19.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SplashView: View {
    private var getUser: GetUser
    @StateObject var viewModel = SplashViewModel(getUser: nil)
    
    init(getUser: GetUser) {
        self.getUser = getUser
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(NSLocalizedString("unter", comment: "App name."))
            Text(NSLocalizedString("need_a_ride", comment: "App slogan."))
            
            NavigationLink(destination: LoginView(), isActive: $viewModel.showLogin) {
                EmptyView()
            }.hidden()
            
            
            NavigationLink(destination: PassengerDashboardView(), isActive: $viewModel.showDashboard) {
                EmptyView()
            }.hidden()
            
        }
        .onAppear {
            viewModel.setGetUser(getUser: self.getUser)
        }
    }
}

//struct SplashScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        SplashView()
//    }
//}
