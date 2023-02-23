//
//  MapViewControllerBridge.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-22.
//  Copyright © 2023 orgName. All rights reserved.
// Note: Credit to Alexander Fanaian for their article "How to Use the Google Maps SDK With SwiftUI"
// for how to set this up
//

import Foundation
import GoogleMaps
import SwiftUI
import UIKit


struct GoogleMapView: UIViewRepresentable {
    
    @ObservedObject var locationManager = LocationManager()
    
    private let zoom: Float = 15.0
    
    func makeUIView(context: Self.Context) -> GMSMapView {
        let lat = locationManager.latitude
        let lon = locationManager.longitude
        print("\(lat) \(lon)")
        
        let cameraPosition = GMSCameraPosition(latitude: locationManager.latitude, longitude: locationManager.longitude, zoom: 14.0)
        let mapView = GMSMapView.map(withFrame: CGRect.zero, camera: cameraPosition)
        mapView.setMinZoom(12, maxZoom: 18)
        return mapView
    }
    
    func updateUIView(_ mapView: GMSMapView, context: Context) {
        let cameraPosition = GMSCameraPosition(latitude: locationManager.latitude, longitude: locationManager.longitude, zoom: 14.0)
        
        mapView.camera = cameraPosition
        mapView.animate(toLocation: CLLocationCoordinate2D(latitude: locationManager.latitude, longitude: locationManager.longitude))
    }
}

