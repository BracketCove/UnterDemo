//
//  LoginScreen.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-19.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct LoginView: View {
    @State private var email = ""
    @State private var password = ""
    
    private var loginUser: LogInUser
    @StateObject var viewModel = LoginViewModel(loginUser: nil)
    
    init(loginUser: LogInUser) {
        self.loginUser = loginUser
    }
    
    var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 16) {
                Text(NSLocalizedString("unter", comment: "App name.")).frame(alignment: .leading)
                    .font(.custom("poppins_normal", size: 64))
                    .foregroundColor(.black)
                    .padding(.top, 64)
                
                Text(NSLocalizedString("need_a_ride", comment: "App slogan."))
                    .font(.custom("poppins_light", size: 18))
                    .foregroundColor(.black)
                
                TextField(
                    NSLocalizedString("email", comment: ""),
                    text: $email
                )
                .autocapitalization(.none)
                .disableAutocorrection(true)
                .padding([.top, .bottom], 16)
                .padding([.leading, .trailing], 32)
                
                SecureField(
                    NSLocalizedString("password", comment: ""),
                    text: $password
                ).padding([.top, .bottom], 16)
                .padding([.leading, .trailing], 32)
                
                Button(NSLocalizedString("continue", comment: "")) {
                    viewModel.attemptLogin(email: self.email, password: self.password)
                }.padding(.top, 16)
                
                Spacer()
                
//                NavigationLink(destination: PassengerDashboardView(), isActive: $viewModel.showDashboard) {
//                    EmptyView()
//                }.hidden()
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .background(.white)
            .onAppear {
                viewModel.setLoginUser(loginUser: self.loginUser)
            }
        }
    }
}

struct LoginScreen_Previews: PreviewProvider {
    static var previews: some View {
        LoginView(
        loginUser: LogInUser(
            authService: FakeAuthorizationService(),
            userService: FakeUserService()
        ))
    }
}
