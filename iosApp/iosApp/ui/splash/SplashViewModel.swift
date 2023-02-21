//
//  SplashViewModel.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-20.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

extension SplashView {
    class SplashViewModel : ObservableObject {
        
        @Published var showLogin = false
        @Published var showDashboard = false
        
        private var getUser: GetUser? = nil
        
        init (getUser: GetUser? = nil) {
            self.getUser = getUser
        }
        
        
        func setGetUser(getUser: GetUser) {
            self.getUser = getUser
        }
        
        func getUserSession() {
            getUser?.getUser {
                value, error in
                if let result = value {
                    if result == nil { self.showLogin = true }
                    else { self.showDashboard = true }
                }
                
                if error != nil {
                    self.showLogin = true
                }
            }
        }
    }
}
