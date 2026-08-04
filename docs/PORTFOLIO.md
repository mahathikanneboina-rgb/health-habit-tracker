# Health Habit Tracker - Developer Portfolio Presentation

## 📝 Project Summary

The **Health Habit Tracker** is an offline-first Android application developed in Kotlin. It is designed to assist users in building healthy habits through interactive scheduling, visual reports, and rule-based insights. The app combines SQLite local speed with Firebase cloud backup to deliver a reliable user experience.

---

## 🚀 Key Technologies & APIs

- **Kotlin & Coroutines**: Leveraged structured concurrency to perform background database and sync operations smoothly.
- **Room SQLite ORM**: Embedded SQLite database with Flow stream mapping.
- **Firebase Auth & Firestore**: Integrates Google Firebase SDK to handle user accounts and cloud synchronization.
- **Android Jetpack WorkManager**: Custom OneTimeWorkRequests to schedule notifications without draining device batteries.
- **Jetpack DataStore (Preferences)**: Modern replacement for SharedPreferences to store settings like Dark Mode.
- **MPAndroidChart**: Third-party charting library to generate pie and bar charts of user achievements.
- **Material Design 3 & View Binding**: Provides clean XML animations and views.

---

## 💡 Engineering Challenges Solved

### 1. Duplicated Cloud Sync Entities
* **Problem**: Originally, the database interface added Firestore documents using Firebase's auto-generated IDs (`.add(item)`). However, update/delete calls expected the document ID to match the local Room UUID (`habit.id`), leading to duplicate records.
* **Solution**: Refactored the network interface (`FirestoreRepository.kt`) to create documents using the Room UUID: `.document(habit.id).set(firestoreHabit)`. This maintains 1:1 ID consistency across offline and online environments.

### 2. Immediate Alarms Execution (1970 Epoch Bug)
* **Problem**: In the reminder scheduling utility, parsing the time values set the Calendar's epoch time to January 1, 1970, resulting in negative initial delays and triggering alarm notifications immediately.
* **Solution**: Rewrote the Calendar logic in `WorkManagerUtil.kt` to overlay the parsed hours/minutes onto today's date, correcting scheduling offsets.

### 3. RecyclerView Updating Flashes
* **Problem**: The dashboard list re-triggered entrance animations every time a habit's completion checkbox was clicked and rebound.
* **Solution**: Overrode `submitList` inside `HabitAdapter.kt` and tracked a `lastPosition` counter. Entrance animations are only triggered when rendering new list items.

---

## 📈 Future Enhancements

- **Push Notifications via FCM**: Transition from local WorkManager reminders to Firebase Cloud Messaging for centralized, push-triggered tips and reminders.
- **Flexible Recurrence Scheduling**: Support weekly, bi-weekly, or specific weekday constraints instead of daily-only goals.
- **Social Accountability**: Implement a "Habit Buddy" system allowing users to share and complete goals together.
