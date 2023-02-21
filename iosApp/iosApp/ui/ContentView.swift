import SwiftUI
import shared

struct ContentView: View {
    
    @State private var path = NavigationPath()
    
	var body: some View {
        NavigationStack(path: $path) {
            
        }
	}
}

struct ContentView_Previews: PreviewProvider {
    
	static var previews: some View {
		ContentView()
	}
}

struct Screens {
    let splash: String
    let login: String
    let passengerDashboard: String
}
