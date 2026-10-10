# নিয়ে এসো (Niye Esho) 🛒

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-7f52ff?logo=kotlin&logoColor=white)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/compose-multiplatform)
[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![iOS](https://img.shields.io/badge/Platform-iOS-000000?logo=apple&logoColor=white)](https://developer.apple.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Niye Esho** (নিয়ে এসো — *"Bring it"*) is a modern, offline-first, cross-platform collaborative shopping list and contact connection application built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)**. It empowers families and friends to manage shared shopping lists in real time with data privacy anchored directly to the user's personal Google Drive and peer-to-peer Firebase Cloud Messaging (FCM).

---

## ✨ Key Features

- **🛒 Collaborative Shopping Lists**: Create, update, share, and archive shopping lists in real time. Check off items collaboratively with family or friends.
- **☁️ Google Drive-Backed Storage**: No external backend servers holding private data. User lists, profile data, and contact books are securely persisted directly in the user's private Google Drive application folder.
- **📷 P2P Contact Connection via QR**: Seamlessly connect with contacts by generating personal invite links and QR codes or scanning incoming peer QR codes.
- **🔔 Real-Time Messaging & Sync**: Built-in FCM push notifications and message handlers for instant synchronization of contact requests, list updates, and item changes.
- **🌙 Material 3 Design System**: Immersive dark-themed UI built with Compose Multiplatform, custom dynamic shapes, and semantic color roles.
- **⚡ Offline-First Architecture**: Robust local persistence layer ensuring lightning-fast reads and writes with background synchronization.

---

## 🏗️ Architecture & Tech Stack

Niye Esho follows a strict **Unidirectional Data Flow (UDF)** and **Clean Architecture** layered across shared and platform modules:

- **UI Layer**: Compose Multiplatform (`commonMain`), following stateless screen patterns and Material 3 design tokens.
- **Presentation Layer**: Multiplatform `ViewModel` exposing immutable `UiState` via `StateFlow` and handling user actions.
- **Domain Layer**: Pure Kotlin use cases and domain entities, completely decoupled from platform frameworks.
- **Data Layer**: Repositories acting as single sources of truth, bridging Ktor network clients (Google Drive API, FCM) and local data sources.
- **Dependency Injection**: Koin for multiplatform dependency injection.
- **Serialization**: `kotlinx.serialization` for type-safe JSON wire formats.

---

## 📁 Project Structure

```
D:/NewProject3/
├── androidApp/          # Android platform entry point & manifest configuration
├── iosApp/              # iOS SwiftUI entry point & runner
├── shared/
│   ├── src/
│   │   ├── commonMain/  # Shared domain, data, use cases, viewmodels, and CMP UI
│   │   ├── androidMain/ # Android platform actuals, FCM service, network observers
│   │   └── iosMain/     # iOS platform actuals
│   └── build.gradle.kts
├── build-logic/         # Convention plugins for Gradle build orchestration
└── gradle/              # Version catalog (libs.versions.toml)
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / IntelliJ IDEA (with Kotlin Multiplatform plugin installed)
- JDK 17 or higher
- Xcode (for running the iOS target on macOS)

### Running the Apps

- **Android**:
  ```bash
  ./gradlew :androidApp:assembleDebug
  ```
  Or run directly from Android Studio using the `androidApp` run configuration.
- **iOS**:
  Open the `iosApp` directory in Xcode and run on a simulator or physical device.

### Running Tests
- **Common & Android Unit Tests**:
  ```bash
  ./gradlew :shared:testAndroidHostTest
  ```

---

## 🛡️ Engineering Standards

The codebase adheres strictly to rigorous professional engineering standards:
- **Zero Hardcoded Values**: All colors, shapes, and spacing derive from centralized design system theme tokens (`Color.kt`, `Shape.kt`, `Spacing.kt`).
- **Strict KMP Purity**: `commonMain` contains zero platform-specific (`android.*`, `java.*`) imports.
- **Modular UI Anatomy**: Stateless screens and granular reusable components adhering to size and responsibility limits.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
