//
//  MapViewControllerBridge.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-22.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import GoogleMaps
import SwiftUI
import UIKit


struct GoogleMapView: UIViewRepresentable {
    func makeUIView(context: Self.Context) -> GMSMapView {
        let cameraPosition = GMSCameraPosition(latitude: 51.05, longitude: -114.067, zoom: 14.0)
        let mapView = GMSMapView.map(withFrame: CGRect.zero, camera: cameraPosition)
        
        return mapView
    }
    
    func updateUIView(_ uiView: GMSMapView, context: Context) {
        
    }
}

