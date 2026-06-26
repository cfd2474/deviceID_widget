# Device Name Widget

A simple, modern Android app that provides a dynamic home screen and lock screen widget. The widget displays the current Device Name (as configured in Android Settings) and automatically resizes its font to perfectly fit the widget's bounds. 

## Features
- **Dynamic Text Scaling**: Utilizes Android's `autoSizeTextType="uniform"` to seamlessly scale text based on the widget dimensions.
- **Live Polling**: Integrates Android `WorkManager` with a `ContentUriTrigger` to actively monitor changes to `Settings.Global.DEVICE_NAME`, instantly updating the widget when the device is renamed.
- **Customizable Appearance**: Includes a Jetpack Compose settings screen (accessible via the app icon) to customize the widget's text color and background shading.
- **Lock Screen Support**: Configured to be placed on both the home screen and keyguard (Note: Lock screen widget availability depends on the device manufacturer; Samsung devices require the Good Lock/LockStar module).

## Tech Stack
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose (for the configuration screen), XML `RemoteViews` (for the widget)
- **Background Processing**: WorkManager

## Setup
1. Clone the repository.
2. Open the project in Android Studio.
3. Build and deploy to an Android device or emulator running API 26 or higher.
