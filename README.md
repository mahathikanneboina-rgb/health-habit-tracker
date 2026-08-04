# Health Habit Tracker 💧🏃‍♂️😴

A premium, stable Android application built using Kotlin to track daily routines and wellness behaviors. The app features local persistence with a Room SQLite Database, online synchronization with Firebase Authentication and Cloud Firestore, rule-based AI Health Insights, daily WorkManager reminders, and analytics tracking with graphical representations.

---

## 🌟 Key Features

- **Secure Authentication**: Firebase Email & Password Signup, Login, and Password Reset integration.
- **Dynamic Habit Tracking**: Add, Edit, Delete, and Complete habits across pre-defined categories (*Water, Exercise, Sleep, Study, Meditation, Custom*).
- **Offline First**: Fully functional offline database powered by **Room DB**, which pre-populates default habits upon installation.
- **Firestore Sync**: Auto-syncs local data to Cloud Firestore once authenticated, maintaining data consistency.
- **Rule-Based AI Health Insights**: Custom-computed daily and weekly streaks, dynamic progress tracking, and rule-based insights about habits.
- **Daily Reminders**: WorkManager-scheduled push notifications that remind users to complete habits at their specified times.
- **Reports & Analytics**: Graphical visualization of daily, weekly, and monthly habit statistics using MPAndroidChart.
- **System Preference Persistence**: Preferences (Dark Mode, Notification Enable/Disable) persisted locally using Android DataStore.

---

## 🛠️ Technology Stack

- **Language**: Kotlin 2.0+
- **Architecture**: MVVM (Model-View-ViewModel) with repository mediation.
- **Local Database**: Room SQLite Database (using Flow for reactive UI updates).
- **Backend**: Firebase Auth & Firebase Cloud Firestore.
- **Task Scheduling**: WorkManager (OneTimeWorkRequest with custom initial delays).
- **Preferences**: Jetpack DataStore (Preferences).
- **UI Architecture**: View Binding with standard XML Layouts.
- **Charts**: MPAndroidChart library.
- **Threading**: Kotlin Coroutines & Flow.

---

## 📁 Folder Structure

```
habittracker/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/habittracker/
│   │   │   │   ├── adapter/
│   │   │   │   │   └── HabitAdapter.kt          # RecyclerView ListAdapter with optimized anims
│   │   │   │   ├── data/
│   │   │   │   │   ├── AuthRepository.kt        # User Auth handling
│   │   │   │   │   ├── Habit.kt                 # Room entity representing habits
│   │   │   │   │   ├── HabitDao.kt              # Room Database query operations
│   │   │   │   │   ├── HabitDatabase.kt         # Database instance & initial seed callback
│   │   │   │   │   ├── HabitRepository.kt       # Unified Local/Firestore Repository mediator
│   │   │   │   │   └── SettingsRepository.kt    # Persistence for System Settings (DataStore)
│   │   │   │   ├── model/
│   │   │   │   │   ├── FirestoreHabit.kt        # DTO matching Firebase document format
│   │   │   │   │   └── Insight.kt               # AI Insight data model
│   │   │   │   ├── repository/
│   │   │   │   │   └── FirestoreRepository.kt   # Low-level Firebase Firestore sync operations
│   │   │   │   ├── util/
│   │   │   │   │   ├── FirestoreHelper.kt       # Auth verification helper
│   │   │   │   │   ├── InsightGenerator.kt      # Rule-based insights calculation
│   │   │   │   │   ├── NotificationHelper.kt    # Permission safe builder for notifications
│   │   │   │   │   └── WorkManagerUtil.kt       # Handles alarm schedule/cancel operations
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── HabitViewModel.kt        # Binds habit flows and calculations to screens
│   │   │   │   │   ├── ProfileViewModel.kt      # Manages user accounts
│   │   │   │   │   └── SettingsViewModel.kt     # Persists application preferences
│   │   │   │   ├── work/
│   │   │   │   │   └── ReminderWorker.kt        # WorkManager Worker triggering notification
│   │   │   │   │
│   │   │   │   ├── AddHabitActivity.kt          # UI to create or update habits
│   │   │   │   ├── ForgotPasswordActivity.kt    # UI to request password reset
│   │   │   │   ├── HabitApplication.kt          # Global Application initialization context
│   │   │   │   ├── LoginActivity.kt             # Main entry point for unauthenticated users
│   │   │   │   ├── MainActivity.kt              # Main habit dashboard with Stats & Insights
│   │   │   │   ├── ProfileActivity.kt           # User Profile settings UI
│   │   │   │   ├── RegisterActivity.kt          # User registration UI
│   │   │   │   ├── ReportsActivity.kt           # Analytics dashboard (MPAndroidChart)
│   │   │   │   ├── SettingsActivity.kt          # App settings (Dark Mode / Alerts) UI
│   │   │   │   └── SplashActivity.kt            # Decides landing screen based on Auth state
│   │   │   │
│   │   │   └── res/                             # Layouts, drawable assets, string resources
│   │   └── test/                                # Local Unit Tests
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Android Studio Ladybug (or higher)
- JDK 11 or newer configured in Android Studio
- Android Device or Emulator running Android 7.0+ (API level 24+)

### Steps to Run
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/health-habit-tracker.git
   cd health-habit-tracker
   ```
2. **Setup Firebase**:
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Create a new project and add an Android application using package `com.example.habittracker`.
   - Download the `google-services.json` file and place it inside the `app/` directory of the project.
   - Enable **Email/Password Provider** in Authentication.
   - Enable **Cloud Firestore** and set up Database Rules to allow read/write operations under `/users/{userId}`.
3. **Build & Run**:
   - Open the project in Android Studio.
   - Sync the Gradle files.
   - Connect your device or start an emulator.
   - Click the green **Run** button to launch the application.

---

## 🏛️ Architecture Details

The app strictly follows the **MVVM Architecture Pattern**:
- **Model**: Room SQLite Database entity (`Habit`) and Firestore DTO (`FirestoreHabit`).
- **View**: XML Layouts bound to Activities (`MainActivity`, `ReportsActivity`, etc.) utilizing Android View Binding.
- **ViewModel**: Manages state transformations via LiveData/Flows, isolating view layers from database actions.
- **Repository**: Mediates transactions, coordinating local changes (Room) and remote synchronization (Firestore).

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author
- **Mahathi Kanneboina**