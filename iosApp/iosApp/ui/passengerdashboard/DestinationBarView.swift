//
//  DestinationBarView.swift
//  iosApp
//
//  Created by Ryan Kay on 2023-02-22.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct DestinationBarView: View {
    
    private var handleCancelButtonClick: (() -> Void)? = nil
    
    init(
       _ handleCancelButtonClick:(() -> Void)?
    ) {
        self.handleCancelButtonClick = handleCancelButtonClick
    }
    
    var body: some View {
        VStack {
            HStack {
                Text(NSLocalizedString("destination", comment: ""))
                    .frame(alignment: .leading)
                    .font(.custom("poppins_medium", size: 16))
                    .foregroundColor(Color("LightGrey"))
                    .padding([.top, .leading], 16)
                
                Spacer()
                
                Button(NSLocalizedString("cancel", comment: "")) {
                    handleCancelButtonClick?()
                }.padding(.trailing, 16)
                    .buttonStyle(.borderedProminent)
                    .tint(.red)
                
            }
            
            Text("212 Seagal Street, Louisiana")
                .frame(alignment: .leading)
                .font(.custom("poppins_bold", size: 18))
                .foregroundColor(.black)
                .padding([.top, .leading], 16)
            
        }
        
    }
}

struct DestinationBarView_Previews: PreviewProvider {
    static var previews: some View {
        DestinationBarView(
            {}
        )
    }
}
