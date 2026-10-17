# QuizzyWizzy — Kids' Learning Quiz App for Android

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-minSdk%2021%20%7C%20targetSdk%2034-3DDC84?logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Realtime%20Database-FFCA28?logo=firebase&logoColor=black)
![Gradle](https://img.shields.io/badge/Gradle-Kotlin%20DSL-02303A?logo=gradle&logoColor=white)

**QuizzyWizzy** is a colourful, kid-friendly educational quiz app for preschoolers and early primary students. Children pick a category, answer multiple-choice questions, and get their score at the end. All questions are served from **Firebase Realtime Database**, so you can add new content **without shipping an app update**.

---

## Table of Contents

- [Features](#features)
- [Screens & Flow](#screens--flow)
- [Architecture](#architecture)
- [Firebase Data Model](#firebase-data-model)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Roadmap](#roadmap)

---

## Features

| Category | What children practise |
|---|---|
| 🔢 **Math** | Counting, addition, subtraction, number patterns |
| 🔤 **Alphabet** | Letter recognition and phonics |
| 📖 **Words** | Vocabulary, spelling, word recognition |
| 💡 **Trivia** | Simple general knowledge at a child's comprehension level |

- **Content in the cloud.** Questions come from Firebase at runtime, and edits made in the Firebase console appear in the app straight away.
- **Instant feedback** on every answer ("Correct!" or "Wrong Answer!") and a final score summary (`Your Score: x/n`).
- **Grid category picker** with large, illustrated tiles made for small hands.
- **Splash screen**, edge-to-edge layout, and light and dark themes.
- Supports **Android 5.0 (API 21)** and later.

---

## Screens & Flow

```text
SplashActivity ──(3 s)──▶ MainActivity ──(tap category)──▶ QuizActivity ──▶ score
                             2-column grid                  loads quiz_categories/{category}
                             of categories                  from Firebase, one question at a time
```

| Activity | Responsibility |
|---|---|
| `SplashActivity` | Branded launch screen. Opens `MainActivity` after 3 seconds. |
| `MainActivity` | `RecyclerView` + `GridLayoutManager(2)` showing the categories through `CategoriesAdapter` |
| `QuizActivity` | Subscribes to `quiz_categories/{category}`, renders questions and options, checks answers, tracks the score |
| `ResultActivity` | Results screen layout (edge-to-edge insets) |

---

## Architecture

```text
┌──────────────────── UI layer (Activities + XML layouts) ───────────────────┐
│ SplashActivity   MainActivity ── CategoriesAdapter   QuizActivity   Result │
└───────────────────────────────────────┬────────────────────────────────────┘
                                        │ ValueEventListener
                                        ▼
                      Firebase Realtime Database  (quiz_categories/*)
                                        │
                                        ▼
                      Question (data class) ◀── DataSnapshot.getValue()
```

Models:

```kotlin
data class Category(val name: String, val iconRes: Int)

data class Question(
    val question: String = "",
    val options: List<String> = listOf(),
    val answer: String = ""
)
```

---

## Firebase Data Model

Questions are stored under `quiz_categories/<category>`, where the category key is the **lowercase** category name:

```json
{
  "quiz_categories": {
    "math": {
      "q1": { "question": "What is 2 + 3?", "options": ["4", "5", "6", "7"], "answer": "5" }
    },
    "alphabet": {
      "q1": { "question": "Which letter comes after B?", "options": ["A", "C", "D", "E"], "answer": "C" }
    },
    "words":  { "...": "..." },
    "trivia": { "...": "..." }
  }
}
```

> Each question needs **exactly four** `options`, and `answer` must exactly match one of them.

Suggested security rules (read-only for clients):

```json
{
  "rules": {
    "quiz_categories": { ".read": true, ".write": false }
  }
}
```

---

## Tech Stack

| Area | Library / Tool |
|---|---|
| Language | Kotlin 1.9 (JVM target 11) |
| UI | AndroidX AppCompat, Material Components, ConstraintLayout, RecyclerView |
| Backend | Firebase Realtime Database (KTX), Firebase Analytics (BoM 33.7.0) |
| Build | Gradle Kotlin DSL, version catalog (`gradle/libs.versions.toml`), AGP 8.7 |
| SDK | `compileSdk 35`, `targetSdk 34`, `minSdk 21` |
| Testing | JUnit 4, AndroidX Test, Espresso |

---

## Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2) or newer
- JDK 17, which is bundled with Android Studio
- A Firebase project

### Setup

1. **Clone the repository.**

   ```bash
   git clone https://github.com/RehenumaIlman/QuizzyWizzy.git
   ```

2. **Connect Firebase.**
   - In the [Firebase console](https://console.firebase.google.com/), add an Android app with package name `com.example.quizzywizzy`.
   - Download `google-services.json` and place it at `app/google-services.json`, replacing the existing file if you use your own project.
   - Enable **Realtime Database** and import data that follows the [data model](#firebase-data-model).

3. **Build and run.**

   ```bash
   ./gradlew assembleDebug        # build
   ./gradlew installDebug         # install on a connected device or emulator
   ./gradlew test                 # unit tests
   ./gradlew connectedAndroidTest # instrumented tests
   ```

   Or open the project in Android Studio and press ▶️ **Run**.

---

## Project Structure

```text
QuizzyWizzy/
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/quizzywizzy/
│       │   │   ├── SplashActivity.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── CategoriesAdapter.kt
│       │   │   ├── QuizActivity.kt
│       │   │   ├── ResultActivity.kt
│       │   │   ├── Catagory.kt        # Category model
│       │   │   └── Question.kt        # Question model
│       │   └── res/
│       │       ├── layout/            # activity_*.xml, item_catagory.xml
│       │       ├── drawable/          # category illustrations
│       │       └── values/, values-night/
│       ├── test/                      # JVM unit tests
│       └── androidTest/               # Instrumented tests
├── gradle/libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Roadmap

- [ ] Send the final score to `ResultActivity` with stars and confetti feedback
- [ ] Add audio narration (Text-to-Speech) for pre-readers
- [ ] Add difficulty levels and per-category progress tracking
- [ ] Cache questions offline with Firebase disk persistence
- [ ] Move to MVVM with ViewModel + LiveData/Flow and Jetpack Compose
- [ ] Localise into Bangla and other languages

---

<sub>Made with 💜 for curious little minds.</sub>
