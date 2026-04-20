# ARIA Emergency Network — React Native Platform Setup

## iOS Setup (ios/ARIAEmergency/)

### Podfile additions
```ruby
target 'ARIAEmergency' do
  config = use_native_modules!
  use_react_native!(
    :path => config[:reactNativePath],
    :hermes_enabled => true,
  )

  # Required for maps
  pod 'GoogleMaps'
  pod 'Google-Maps-iOS-Utils'

  # Required for voice
  pod 'RNVoice', :path => '../node_modules/@react-native-community/voice'

  # Required for camera
  pod 'react-native-camera', :path => '../node_modules/react-native-camera'
end
```

### Info.plist required keys
```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>ARIA needs your location to find nearby help and share with responders</string>
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>ARIA monitors your location in background to share with emergency responders</string>
<key>NSLocationAlwaysUsageDescription</key>
<string>ARIA needs background location for live tracking</string>
<key>NSMicrophoneUsageDescription</key>
<string>ARIA uses the microphone for the voice assistant and secret SOS keyword detection</string>
<key>NSSpeechRecognitionUsageDescription</key>
<string>ARIA uses speech recognition to understand voice commands and detect your secret SOS keyword</string>
<key>NSCameraUsageDescription</key>
<string>ARIA uses the camera for face/panic detection and video consultations</string>
<key>UIBackgroundModes</key>
<array>
  <string>location</string>
  <string>fetch</string>
  <string>remote-notification</string>
  <string>audio</string>
  <string>voip</string>
</array>
<!-- Google Maps API key -->
<key>GMSApiKey</key>
<string>YOUR_GOOGLE_MAPS_IOS_KEY</string>
```

### AppDelegate.m / AppDelegate.swift additions
```swift
import GoogleMaps

// In application:didFinishLaunchingWithOptions:
GMSServices.provideAPIKey("YOUR_GOOGLE_MAPS_IOS_KEY")
```

---

## Android Setup

### android/app/build.gradle
```groovy
android {
    defaultConfig {
        // Google Maps
        manifestPlaceholders = [MAPS_API_KEY: "YOUR_GOOGLE_MAPS_ANDROID_KEY"]
    }
}
dependencies {
    implementation 'com.google.android.gms:play-services-maps:18.2.0'
}
```

### AndroidManifest.xml additions
```xml
<!-- Google Maps API key -->
<meta-data
    android:name="com.google.android.maps.v2.API_KEY"
    android:value="${MAPS_API_KEY}"/>
```

---

## Google Maps API Keys

1. Go to https://console.cloud.google.com
2. Create a project → Enable APIs:
   - Maps SDK for Android
   - Maps SDK for iOS
   - Places API
   - Geocoding API
3. Create API keys (one for Android, one for iOS)
4. Restrict Android key to your package name
5. Restrict iOS key to your bundle ID

---

## Running

```bash
cd mobile

# Install dependencies
npm install

# iOS
cd ios && pod install && cd ..
npx react-native run-ios

# Android
npx react-native run-android

# Metro bundler
npx react-native start
```

---

## Environment Variables (mobile/.env)
```
GOOGLE_MAPS_API_KEY_IOS=your_ios_key
GOOGLE_MAPS_API_KEY_ANDROID=your_android_key
API_BASE_URL_DEV=http://10.0.2.2:8080
API_BASE_URL_PROD=https://api.aria-emergency.com
```
