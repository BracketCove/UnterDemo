//
//  StreamUserService.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-19.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class StreamUserService : UserService {
    func getUserById(userId: String, completionHandler: @escaping (ServiceResult?, Error?) -> Void) {
        <#code#>
    }
    
    func getUserById(userId: String) async throws -> ServiceResult {
        <#code#>
    }
    
    func initializeNewUser(user: UnterUser, completionHandler: @escaping (ServiceResult?, Error?) -> Void) {
        <#code#>
    }
    
    func initializeNewUser(user: UnterUser) async throws -> ServiceResult {
        <#code#>
    }
    
    func logOutUser(user: UnterUser, completionHandler: @escaping (Error?) -> Void) {
        <#code#>
    }
    
    func logOutUser(user: UnterUser) async throws {
        <#code#>
    }
    
    func updateUser(user: UnterUser, completionHandler: @escaping (ServiceResult?, Error?) -> Void) {
        <#code#>
    }
    
    func updateUser(user: UnterUser) async throws -> ServiceResult {
        <#code#>
    }
    
    
}
