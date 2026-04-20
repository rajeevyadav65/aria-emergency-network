# ARIA Emergency — Mobile Setup Guide (iOS + Android)

## Prerequisites

| Tool | Version | Required |
|------|---------|----------|
| Node.js | 18+ | ✅ |
| React Native CLI | 0.73+ | ✅ |
| Xcode | 15+ | iOS only |
| Android Studio | Hedgehog+ | Android only |
| CocoaPods | 1.14+ | iOS only |
| JDK | 17 | Android only |
| Google Maps API Key | — | ✅ Both |

---

## 1. Get API Keys

### Google Maps (Required for map screen)
1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Enable: **Maps SDK for Android**, **Maps SDK for iOS**, **Places API**
3. Create two keys:
   - `ANDROID_MAPS_KEY` — restrict to Android app
   - `IOS_MAPS_KEY` — restrict to iOS app

---

## 2. Install Dependencies

```bash
cd mobile
npm install

# iOS only
cd ios && pod install && cd ..
```

---

## 3. Configure API Keys

### iOS — `ios/ARIA/Info.plist`
```xml
<key>GMSApiKey</key>
<string>YOUR_IOS_MAPS_KEY</string>
```

### Android — `android/app/src/main/res/values/strings.xml`
```xml
<string name="google_maps_key">YOUR_ANDROID_MAPS_KEY</string>
```

### Backend URL — `src/services/api.ts`
```ts
// Dev (emulator)
const BASE_URL = 'http://10.0.2.2:8080';    // Android emulator
const BASE_URL = 'http://localhost:8080';    // iOS simulator

// Production
const BASE_URL = 'https://api.aria-emergency.com';
```

---

## 4. iOS Setup

```bash
# Open in Xcode
open ios/ARIA.xcworkspace

# Or run directly
npx react-native run-ios --simulator="iPhone 15"
npx react-native run-ios --device  # real device
```

### iOS Capabilities (in Xcode → Signing & Capabilities)
- ✅ Background Modes → Location, Audio, Background fetch, Remote notifications
- ✅ Push Notifications
- ✅ Bluetooth

### iOS Permissions
All permission strings are pre-configured in `ios/ARIA/Info.plist`.

---

## 5. Android Setup

```bash
# Start emulator or connect device
npx react-native run-android

# Or with specific device
npx react-native run-android --deviceId=emulator-5554
```

### Android Permissions
All permissions are declared in `android/src/main/AndroidManifest.xml`.

### Google Services
Download `google-services.json` from Firebase console and place at:
`android/app/google-services.json`

---

## 6. Firebase Push Notifications

### iOS
1. Download `GoogleService-Info.plist` from Firebase
2. Add to `ios/ARIA/` in Xcode

### Android
1. Download `google-services.json` from Firebase
2. Place at `android/app/google-services.json`

---

## 7. Backend Connection

Start the Spring Boot backend:
```bash
cd ../backend
export ANTHROPIC_API_KEY=sk-ant-...
mvn spring-boot:run
```

The mobile app connects to:
- **Android emulator**: `http://10.0.2.2:8080`
- **iOS simulator**: `http://localhost:8080`
- **Real device**: Your machine's LAN IP, e.g., `http://192.168.1.100:8080`

Update `src/services/api.ts` `BASE_URL` accordingly.

---

## 8. Features By Platform

| Feature | iOS | Android |
|---------|-----|---------|
| Google Maps | ✅ | ✅ |
| Voice recognition | ✅ | ✅ |
| Text-to-speech | ✅ | ✅ |
| GPS tracking | ✅ | ✅ |
| Background location | ✅ | ✅ |
| BLE peer-to-peer | ✅ | ✅ |
| WiFi Direct | ❌ (no iOS support) | ✅ |
| Push notifications | ✅ (APNs) | ✅ (FCM) |
| Fall detection | ✅ | ✅ |
| Face detection | ✅ (Vision) | ✅ (ML Kit) |
| WebSocket alerts | ✅ | ✅ |
| Offline sync | ✅ | ✅ |

---

## 9. Screen Reference

| Screen | Route | Description |
|--------|-------|-------------|
| `AuthScreen` | `Auth` | Login / Register / Guest |
| `HomeScreen` | `Main → Home` | Dashboard + quick actions |
| `SOSScreen` | `SOS` | Emergency SOS + location sharing |
| `MapScreen` | `Map` | Live Google Map + emergency pins |
| `VoiceScreen` | `Voice` | AI voice assistant + secret keyword |
| `ChatScreen` | `Chat` | ARIA AI chat with emergency detection |
| `MedicalScreen` | `Medical` | Find nearby doctors/ambulance/police |
| `DisastersScreen` | `Disasters` | Live disaster alerts (USGS) |
| `DispatchScreen` | `Dispatch` | Role-specific dispatch panel |
| `AlertsScreen` | `Alerts` | Emergency feed + notifications |
| `ProfileScreen` | `Profile` | Settings + duty toggle + logout |

---

## 10. Building for Release

### iOS (App Store)
```bash
# In Xcode: Product → Archive
# Then distribute via App Store Connect
```

### Android (Play Store)
```bash
cd android
./gradlew bundleRelease

# APK at: android/app/build/outputs/bundle/release/app-release.aab
```

### Environment variables for CI/CD
```
MAPS_API_KEY_ANDROID=...
MAPS_API_KEY_IOS=...
FIREBASE_SERVER_KEY=...
ARIA_BACKEND_URL=https://api.aria-emergency.com
```

---

## Troubleshooting

### Metro bundler not starting
```bash
npx react-native start --reset-cache
```

### Pod install fails
```bash
cd ios
pod repo update
pod install
```

### Android build fails (duplicate classes)
```bash
cd android
./gradlew clean
```

### Voice not working on iOS simulator
Real device required for microphone access.

### Maps blank on Android
Ensure `google-services.json` is placed correctly and Maps SDK is enabled in GCP.
