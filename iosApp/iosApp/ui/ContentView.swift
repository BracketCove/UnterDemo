import SwiftUI
import shared

struct ContentView: View {
    //usecases and services
    private var dependencyLocator: DependencyLocator
        
    init(dependencyLocator: DependencyLocator) {
        self.dependencyLocator = dependencyLocator
    }
    
    var body: some View {
        SplashView(
            dependencyLocator: dependencyLocator
        )
    }
}
