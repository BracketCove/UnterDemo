//
//  PlaceViewModel.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import MapKit

struct PlaceViewModel: Identifiable {
    
    let id = UUID()
    private var mapItem: MKMapItem
    
    init(mapItem: MKMapItem) {
        self.mapItem = mapItem
    }
    
    var name: String {
        mapItem.name ?? ""
    }
    
    var lat: Double {
        mapItem.placemark.coordinate.latitude
    }
    
    var lon: Double {
        mapItem.placemark.coordinate.longitude
    }
    
    var address: String {
        var address = ""
        
        if let street = mapItem.placemark.areasOfInterest?.first {
            address.append(street)
            address.append(", ")
        }
        
        if let area = mapItem.placemark.thoroughfare {
            address.append(area)
            address.append(", ")
        }
        
        if let region = mapItem.placemark.locality {
            address.append(region)
        }

        return address
    }
}
