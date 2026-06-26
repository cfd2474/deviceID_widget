# Device Name Widget

A simple, modern Android app that provides a dynamic home screen and lock screen widget. The widget displays the current Device Name (as configured in Android Settings) and automatically resizes its font to perfectly fit the widget's bounds. 

## Features
- **Dynamic Text Scaling**: Uses a custom size calculation algorithm based on widget dimensions, bypassing native `autoSizeTextType` bugs to ensure complete compatibility with Samsung LockStar and other third-party lockscreen widget hosts.
- **Live Polling**: Integrates Android `WorkManager` with a `ContentUriTrigger` to actively monitor changes to `Settings.Global.DEVICE_NAME`, instantly updating the widget when the device is renamed.
- **Customizable Appearance**: Includes a Jetpack Compose settings screen (accessible via the app icon) to customize the widget's text color and background shading.
- **Lock Screen Support**: Fully compatible with the home screen and keyguard. Specifically engineered to work seamlessly with Samsung devices using the Good Lock/LockStar module without the infamous "Couldn't add widget" error.

## Tech Stack
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose (for the configuration screen), XML `RemoteViews` (for the widget)
- **Background Processing**: WorkManager

## Setup
1. Clone the repository.
2. Open the project in Android Studio.
3. Build and deploy to an Android device or emulator running API 26 or higher.
