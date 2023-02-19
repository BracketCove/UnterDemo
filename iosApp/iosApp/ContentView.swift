import SwiftUI
import shared

struct ContentView: View {
    @State private var navigationState: String? = Screens.Splash.rawValue

	let greet = Greeting().greet()

	var body: some View {
        NavigationView {
            LazyVStack {
                NavigationLink(destination: Text("First View"), tag: Screens.Splash.rawValue, selection: $navigationState) {
                    SplashScreen()
                }
                
                NavigationLink(destination: Text("First View"), tag: Screens.Login.rawValue, selection: $navigationState) {
                    LoginScreen()
                }
                
                NavigationLink(destination: Text("First View"), tag: Screens.PassengerDashboard.rawValue, selection: $navigationState) {
                    PassengerDashboardScreen()
                }
            }
        }
		Text(greet)
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
