//
//  FirebaseAuthService.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-19.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

class FirebaseAuthService: AuthorizationService {
    func getSession(completionHandler: @escaping (ServiceResult?, Error?) -> Void) {
        <#code#>
    }
    
    func getSession() async throws -> ServiceResult {
        <#code#>
    }
    
    func login(email: String, password: String, completionHandler: @escaping (ServiceResult?, Error?) -> Void) {
        <#code#>
    }
    
    func login(email: String, password: String) async throws -> ServiceResult {
        <#code#>
    }
    
    func logout(completionHandler: @escaping (ServiceResult?, Error?) -> Void) {
        <#code#>
    }
    
    func logout() async throws -> ServiceResult {
        <#code#>
    }
    
    func signUp(email: String, password: String, completionHandler: @escaping (ServiceResult?, Error?) -> Void) {
        <#code#>
    }
    
    func signUp(email: String, password: String) async throws -> ServiceResult {
        <#code#>
    }
    
    
}
