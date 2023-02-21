import SwiftUI
import shared

struct ContentView: View {
	var body: some View {
        EmptyView()
	}
}

struct ContentView_Previews: PreviewProvider {
    
	static var previews: some View {
		ContentView()
	}
}

enum Screens: String {
    case Splash = "Splash"
    case Login = "Login"
    case PassengerDashboard = "PassengerDashboard"
}
