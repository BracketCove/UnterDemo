//
//  DependencyLocator.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-22.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class DependencyLocator {
    var getUser: GetUser
    var loginUser: LogInUser
    var logoutUser: LogOutUser
    
    init(
        getUser: GetUser,
        loginUser: LogInUser,
        logoutUser: LogOutUser
    ) {
        self.getUser = getUser
        self.loginUser = loginUser
        self.logoutUser = logoutUser
    }
    
    
    
}

func getFakeLocator() -> DependencyLocator {
   return DependencyLocator(
        getUser: GetUser(authService: FakeAuthorizationService(), userService: FakeUserService()),
        loginUser: LogInUser(authService: FakeAuthorizationService(), userService: FakeUserService()),
        logoutUser: LogOutUser(authService: FakeAuthorizationService(), userService: FakeUserService())
    )
}