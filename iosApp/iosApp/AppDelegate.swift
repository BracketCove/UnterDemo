//
//  AppDelegate.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-22.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import UIKit
import GoogleMaps
import GooglePlaces

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
       let key = Bundle.main.object(forInfoDictionaryKey: "MAPS_API_KEY")
        
        if  let configKey = key as? String {
            GMSServices.provideAPIKey(configKey)
            GMSPlacesClient.provideAPIKey(configKey)
            print("Key retrieved")
        }   else {
            print("Error: could not retrieve maps api key")
        }
        return true
    }
}
