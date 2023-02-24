//
//  LoginViewModel.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-21.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

import shared

extension LoginView {
    class LoginViewModel : ObservableObject {
        
        @Published var showDashboard = false
        @Published var showError = false
        
        private var loginUser: LogInUser? = nil
        
        init (loginUser: LogInUser? = nil) {
            self.loginUser = loginUser
        }
        
        
        func setLoginUser(loginUser: LogInUser) {
            self.loginUser = loginUser
        }
        
        func attemptLogin(email: String, password: String) {
//            loginUser?.login(email: email, password: password) {
//                value, error in
//                if let result = value as? ServiceResultValue {
//                
//                    if result.value == nil {
//                        self.showError = true
//                    }
//                    else {
//                        self.showDashboard = true
//                    }
//                }
//                
//                if error != nil {
//                    self.showError = true
//                }
//            }
        }
    }
}
