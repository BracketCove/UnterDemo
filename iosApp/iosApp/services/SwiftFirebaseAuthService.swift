//
//  SwiftFirebaseAuthService.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-24.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class SwiftFirebaseAuthService {
    func login(email: String, password: String) async -> Bool {
        return true
    }
    
    func logout() async -> Bool {
        return true
    }
    
    
    func getSession() async -> UnterUser? {
        return UnterUser.companion.getDefaultUser()
    }
}


