import SwiftUI
import shared

@main
struct iOSApp: App {
	var body: some Scene {
		WindowGroup {
			ContentView(
                dependencyLocator: getFakeLocator()
            )
		}
	}
    
//    func getDependencyLocator() -> DependencyLocator {
//        return DependencyLocator(
//            getUser: GetUser(authService: FakeAuthorizationService(), userService: FakeUserService()),
//            loginUser: LogInUser(authService: FakeAuthorizationService(), userService: FakeUserService())
//        )
//    }
}
