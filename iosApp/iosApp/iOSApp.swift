import SwiftUI
import shared

@main
struct iOSApp: App {
	var body: some Scene {
		WindowGroup {
			SplashView(
                getUser: GetUser(authService: FakeAuthorizationService(), userService: FakeUserService()),
                loginUser: LogInUser(authService: FakeAuthorizationService(), userService: FakeUserService())
            )
		}
	}
}
