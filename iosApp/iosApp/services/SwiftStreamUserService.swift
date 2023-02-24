//
//  SwiftStreamUserService.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-24.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class SwiftStreamUserService {
    func getUserById(userId: String) async -> UnterUser? {
        return UnterUser.companion.getDefaultUser()
    }
    
    func logOutUser(user: UnterUser) async -> Bool {
        return true
    }
}
