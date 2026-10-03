import SwiftUI

@main
struct iOSApp: App {
    init() {
        MainViewControllerKt.initializeIosNiyeEsho()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}