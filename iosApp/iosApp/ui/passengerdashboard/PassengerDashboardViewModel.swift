//
//  PassengerDashboardViewModel.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-21.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

extension PassengerDashboardView {
    class PassengerDashboardViewModel : ObservableObject {
        @Published var showDashboard = false
        
        private var logoutUser: LogOutUser? = nil
        
        init (_ logoutUser: LogOutUser?) {
            self.logoutUser = logoutUser
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
}
