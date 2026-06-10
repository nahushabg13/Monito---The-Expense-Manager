# Monito — Expense Manager

A minimalist personal expense tracker for Android.

## Features
- Add expenses with category, amount, note and date
- View total spending at a glance
- Expenses persist across app restarts

## Tech Stack
- Jetpack Compose — UI
- Room Database — local persistence
- MVVM + Repository pattern — architecture
- StateFlow — reactive state management
- Kotlin Coroutines — async operations
- Navigation Component — screen navigation

## Architecture
UI (Compose) → ViewModel → Repository → Room Database
