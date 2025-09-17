# StudyMate - Final Mobile App

![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-757575?logo=material-design&logoColor=white)

A comprehensive productivity and study companion app built with modern Android development practices. StudyMate combines task management, study tools, and social features to enhance the learning experience.

## 🎯 Features

### 📅 Calendar & Task Management
- **Monthly Calendar View**: Interactive calendar with task visualization
- **Daily Schedule**: Detailed daily view with time-based task organization
- **Task Lists**: Organize tasks with custom categories and tags
- **Recurring Tasks**: Support for daily, weekly, monthly, and custom repeat patterns
- **Task Details**: Rich task information with descriptions, due dates, and priorities
- **All-day Events**: Support for both timed and all-day tasks

### 📚 Study Tools
- **Quiz System**: Create, manage, and take quizzes with multiple question types
- **Quiz Review**: Track quiz performance with detailed statistics
- **Flashcard Mode**: Review quiz content in flashcard format
- **Filter & Search**: Time-based filtering (daily, weekly, monthly, yearly)
- **Progress Tracking**: Monitor study progress and quiz statistics

### 🍅 Pomodoro Timer
- **Customizable Sessions**: Adjustable focus and break durations
- **Audio Support**: Optional calm music during sessions
- **Session History**: Track daily focus sessions
- **Settings**: Personalized timer configurations
- **Progress Monitoring**: Daily target tracking

### 💬 Chat & Social
- **Real-time Messaging**: Chat with other users
- **Group Conversations**: Create and manage group chats
- **File Sharing**: Share documents and media
- **ChatGPT Integration**: Direct access to ChatGPT for study assistance
- **User Profiles**: Manage your profile and view others

### 🔐 Authentication
- **User Registration**: Multi-step account creation
- **Secure Login**: Token-based authentication
- **Profile Management**: User settings and preferences
- **Session Management**: Automatic token handling

## 🏗️ Technical Architecture

### Built With
- **Kotlin**: 100% Kotlin codebase
- **Jetpack Compose**: Modern declarative UI framework
- **Material 3**: Latest Material Design components
- **MVVM Pattern**: Clean architecture with ViewModels
- **Coroutines**: Asynchronous programming
- **Retrofit**: HTTP client for API communication
- **DataStore**: Modern data storage solution
- **Navigation Compose**: Type-safe navigation
- **ExoPlayer**: Audio playback for Pomodoro

### API Integration
- **Base URL**: `https://studymate.beerpsi.cc/api/v1/`
- **Authentication**: Bearer token with automatic refresh
- **RESTful Design**: Standard HTTP methods and status codes
- **Error Handling**: Comprehensive error management with retry logic

### Project Structure
```
com.example.finalproject/
├── auth/                 # Authentication modules
├── calendar/            # Calendar and task management
├── chatting/           # Chat and messaging
├── core/               # Shared components and utilities
├── createquiz/         # Quiz creation features
├── journey/            # Quiz system and learning journey
├── main/               # Main app structure and navigation
├── navigation/         # App navigation logic
├── pomodoro/           # Pomodoro timer functionality
├── review/             # Flashcard and review system
├── study/              # Study dashboard and tools
└── tasks/              # Task management components
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Giraffe or later
- Android SDK 26+
- Kotlin 1.9.0+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/DoanCongPho/FinalMobileApp.git
   cd FinalMobileApp
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync the project**
   - Android Studio will automatically sync Gradle files
   - Wait for the build to complete

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`

### Configuration

The app connects to a remote backend by default. For local development:

1. Update the base URL in `ApiClient.kt`
2. Ensure the backend server is running
3. Configure appropriate network security settings if needed

## 📱 App Structure

### Main Navigation
The app features a bottom navigation with four main sections:

1. **Calendar**: Task and event management
2. **Study**: Learning tools and quiz system
3. **Chat**: Messaging and social features
4. **Account**: User profile and settings

### Key Screens

#### Study Dashboard
- Time-based greeting system
- Daily motivational quotes
- Quick access to main features:
  - Review (Quiz flashcards)
  - Create Quiz (Multiple creation modes)
  - Pomodoro Timer
  - Learning Journey (Quiz management)

#### Calendar System
- Monthly overview with task indicators
- Daily schedule view
- Task creation and editing
- Support for recurring events
- Task list management

#### Quiz System
- Quiz creation from prompts or manual input
- Multiple question types support
- Time-based filtering
- Performance statistics
- Flashcard review mode

#### Pomodoro Timer
- Customizable work/break intervals
- Background music support
- Session tracking and history
- Daily goal setting

## 🔧 Key Dependencies

```kotlin
// UI & Core
implementation("androidx.compose.ui:ui:1.6.7")
implementation("androidx.compose.material3:material3:1.2.1")
implementation("androidx.activity:activity-compose:1.8.2")
implementation("androidx.navigation:navigation-compose:2.8.0")

// Networking
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// Data & Storage
implementation("androidx.datastore:datastore-preferences:1.1.1")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

// Media & Images
implementation("androidx.media3:media3-exoplayer:1.3.1")
implementation("io.coil-kt:coil-compose:2.4.0")

// Lifecycle & Architecture
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
```

## 🎨 Design System

The app follows Material 3 design principles with:
- **Primary Color**: Purple theme (`#6B5BFF`)
- **Typography**: Roboto font family
- **Components**: Material 3 components throughout
- **Dark/Light Theme**: System-aware theming
- **Responsive Layout**: Adaptive to different screen sizes

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run all tests
./gradlew check
```

## 📄 API Documentation

The app integrates with several API endpoints:

- **Authentication**: `/auth/login`, `/auth/register`
- **Users**: `/users/me`, `/users/profile`
- **Quizzes**: `/users/me/quizzes`, `/quizzes/{id}`
- **Tasks**: `/tasks`, `/tasklists`
- **Chat**: `/conversations`, `/messages`

All endpoints require Bearer token authentication, handled automatically by the app.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -m 'Add feature'`
4. Push to the branch: `git push origin feature-name`
5. Submit a pull request

## 📝 License

This project is part of an academic assignment for Mobile Application Development coursework.

## 👥 Team Members

This project was developed collaboratively by:

- **Le Tan Nguyen Dat** - *Project Development*
- **Doan Cong Pho** - *Project Development* - [GitHub](https://github.com/DoanCongPho)
- **Nguyen Duc Thinh** - *Project Development*
- **Vay Thuong Gia Lac** - *Project Development*

## 📧 Support

For support and questions, please open an issue in the GitHub repository.

---

**Note**: This is a group project developed as part of the Mobile Application Development curriculum. It demonstrates modern Android development practices, collaborative software development, and comprehensive app functionality.