# System Architecture and App Flow Documentation

This document explains the architecture design, package structures, core classes, and system flows of the Health Habit Tracker application.

---

## 🏛️ MVVM Architectural Flow

The application follows the clean architectural pattern of Model-View-ViewModel (MVVM). Communication flows one-way from the View (UI) to the ViewModel, down to the repository and database layers, and reactive updates flow back to the UI via LiveData streams and Kotlin Flows.

```mermaid
graph TD
    %% Define Nodes
    subgraph UI Layer
        View[Activities / Layout XMLs]
    end

    subgraph Presentation Layer
        VM[ViewModels - LiveData / Flows]
    end

    subgraph Data Layer
        Repo[HabitRepository / AuthRepository]
        RoomDB[Room Database / HabitDao]
        Firestore[Cloud Firestore / FirestoreRepository]
    end

    %% Flow Connections
    View -->|User Events| VM
    VM -->|Observe State| View
    VM -->|Data Transactions| Repo
    Repo -->|Local Read/Write| RoomDB
    Repo -->|Remote Sync| Firestore
    RoomDB -->|Flow Updates| Repo
    Repo -->|Live Updates| VM
```

---

## 🗺️ App Navigation Flow

The navigation workflow represents how users journey through authentication and primary pages.

```mermaid
graph TD
    Splash[SplashActivity]
    Login[LoginActivity]
    Register[RegisterActivity]
    Forgot[ForgotPasswordActivity]
    Dashboard[MainActivity]
    AddEdit[AddHabitActivity]
    Reports[ReportsActivity]
    Profile[ProfileActivity]
    Settings[SettingsActivity]

    %% Navigation Flow
    Splash -->|Is Authenticated?| Dashboard
    Splash -->|No Auth| Login
    Login -->|Click Register| Register
    Login -->|Click Forgot Password| Forgot
    Forgot -->|Back| Login
    Register -->|Successful Signup| Login
    Login -->|Successful Auth| Dashboard
    
    %% Tab Navigation
    Dashboard -->|Bottom Nav| Reports
    Dashboard -->|Bottom Nav| Profile
    Dashboard -->|Bottom Nav| Settings
    Dashboard -->|Click FAB / Edit Item| AddEdit

    Reports -->|Bottom Nav| Dashboard
    Reports -->|Bottom Nav| Profile
    Reports -->|Bottom Nav| Settings
    
    Settings -->|Back Press| Dashboard
    Profile -->|Back Press| Dashboard
```

---

## 🔄 Room Database & Cloud Firestore Sync Flow

Data is cached locally first. Offline mutations are synced to Firestore when an internet connection/auth state is verified, matching user accounts.

```mermaid
sequenceDiagram
    autonumber
    actor User as User Interaction
    participant View as MainActivity / AddHabitActivity
    participant VM as HabitViewModel
    participant Repo as HabitRepository
    participant Room as Room SQLite Database
    participant Firestore as Cloud Firestore

    User->>View: Add/Edit/Complete Habit
    View->>VM: Call operation (e.g. toggleHabitCompletion)
    VM->>Repo: Perform background execution
    Repo->>Room: Execute Local Transaction
    Room-->>Repo: Return Local Database Status
    Repo->>Firestore: Check auth & upload change (set with habit.id)
    Firestore-->>Repo: Confirm remote write receipt
    Room->>View: Reactive Flow update streams new data to adapter
```

---

## 📦 Package & Class Guide

### Package: `com.example.habittracker`
- **`SplashActivity`**: First screen. Decides whether to direct the user to authentication or straight to the home dashboard.
- **`LoginActivity`**: Processes sign-in actions, validates user credentials, and directs to signup or password recovery.
- **`RegisterActivity`**: Manages sign-up workflow.
- **`ForgotPasswordActivity`**: Requests recovery emails for user accounts.
- **`MainActivity`**: Dashboard listing active habits, circular stats progress, and AI insights.
- **`ReportsActivity`**: Generates bar charts and pie charts of habit metrics.
- **`SettingsActivity`**: Persists preferences like dark mode and alerts.
- **`ProfileActivity`**: Manages current user display names, credentials, and sign-outs.
- **`AddHabitActivity`**: Screen to add or update habits.

### Package: `com.example.habittracker.data`
- **`Habit`**: Room entity class representing individual habits.
- **`HabitDao`**: Query interface implementing standard SQLite statements.
- **`HabitDatabase`**: SQLite database wrapper. Uses a callback to populate initial starter habits.
- **`HabitRepository`**: Coordinates local Room writes and Firestore remote replication.
- **`SettingsRepository`**: Interacts with Android Jetpack DataStore to store system toggles.

### Package: `com.example.habittracker.repository`
- **`FirestoreRepository`**: Low-level network handler for Firebase Cloud Firestore operations.

### Package: `com.example.habittracker.util`
- **`InsightGenerator`**: Evaluates active habits and returns rule-based recommendations.
- **`NotificationHelper`**: Builds push reminders and performs API level checks.
- **`WorkManagerUtil`**: Handles scheduling logic for recurring alarms.
