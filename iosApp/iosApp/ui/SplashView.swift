//
//  SplashScreen.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-19.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct SplashView: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(NSLocalizedString("unter", comment: "App name."))
            Text(NSLocalizedString("need_a_ride", comment: "App slogan."))
        }.background(UIColor(named: "ColorPrimary"))
    
        
    }
}

struct SplashScreen_Previews: PreviewProvider {
    static var previews: some View {
        SplashView()
    }
}
