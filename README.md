# Truth or Dare Android

Android app project for a **Truth or Dare** party game, built with Kotlin and Jetpack Compose.

## Features

- Truth and dare question set stored in local assets (`questions.json`)
- Multiple language entries in question data (EN, VI, FR, ES, DE)
- Translation helper using Google ML Kit with local cache
- AdMob and Google Play Billing dependencies already integrated

## Tech Stack

- Kotlin
- Android Gradle Plugin + Gradle Kotlin DSL
- Jetpack Compose
- AndroidX Navigation, Lifecycle, DataStore
- Retrofit + Gson + OkHttp
- Google ML Kit Translation

## Project Structure

```text
app/
  src/main/
    assets/questions.json
    java/com/ninstudio/truthordare/data/local/TranslationManager.kt
    res/
```

## Getting Started

### Requirements

- Android Studio (latest stable recommended)
- JDK 11
- Android SDK (minSdk 24, targetSdk 36)

### Build

```bash
./gradlew assembleDebug
```

### Test

```bash
./gradlew test
```

## Notes

- The app uses `INTERNET` permission for network-based features.
- `questions.json` includes both normal and 18+ (`adult_18`) prompts.
