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
        NavigationView {
            GeometryReader { geometry in
                VStack(spacing: 16) {
                    Text(NSLocalizedString("unter", comment: "App name.")).frame(alignment: .leading)
                        .font(.custom("poppins_normal", size: 64))
                        .foregroundColor(.white)
                    
                    Text(NSLocalizedString("need_a_ride", comment: "App slogan."))
                        .font(.custom("poppins_light", size: 18))
                        .foregroundColor(.white)
                    
                    
                    NavigationLink(destination: LoginView(), isActive: $viewModel.showLogin) {
                        EmptyView()
                    }.hidden()
                    
                    
                    NavigationLink(destination: PassengerDashboardView(), isActive: $viewModel.showDashboard) {
                        EmptyView()
                    }.hidden()
                }
                .frame(width: geometry.size.width, height: geometry.size.height)
                .background(Color("ColorPrimary"))
                .onAppear {
                    viewModel.setGetUser(getUser: self.getUser)
                    viewModel.getUserSession()
                }
            }
        }
    }
}

struct SplashScreen_Previews: PreviewProvider {
    static var previews: some View {
        SplashView(getUser: GetUser(authService: FakeAuthorizationService(), userService: FakeUserService()))
    }
}
