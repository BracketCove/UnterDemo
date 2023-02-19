import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var splashViewModel: SplashViewModel = SplashViewModel(
        GetUser(authService: <#T##AuthorizationService#>, userService: <#T##UserService#>)
    )

	let greet = Greeting().greet()

	var body: some View {
        
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
