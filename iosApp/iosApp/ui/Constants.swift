//
//  Constants.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-20.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

struct Screen: Identifiable, Hashable {
    let id: String = "id"
    let screen: ScreenType
}

enum ScreenType: String {
    case splash = "splash"
    case login = "login"
    case passengerDashboard = "passengerDashboard"
}
