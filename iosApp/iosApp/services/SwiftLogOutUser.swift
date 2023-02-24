//
//  SwiftLogOutUser.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-24.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class SwiftLogoutUser {
    private var auth: SwiftFirebaseAuthService
    private var user: SwiftStreamUserService
    
    init(auth: SwiftFirebaseAuthService, user: SwiftStreamUserService) {
        self.auth = auth
        self.user = user
    }
    
    
    func logoutUser(user: UnterUser) async -> Bool {
        return true
    }
}
