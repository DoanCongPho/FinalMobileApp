# Quiz System Implementation

## Overview
This module implements a complete quiz system that fetches quizzes from your backend server and displays them in a user-friendly interface.

## Features
- **Quiz List Screen**: Shows all user quizzes with statistics
  - Total number of quizzes
  - Total number of questions across all quizzes
  - Individual quiz titles with question counts and creation dates
- **Quiz Detail Screen**: Shows detailed view of a specific quiz
  - All questions and answers
  - Explanations (when available)
  - Quiz metadata (creation/update dates)

## Navigation Flow
1. After login → QuizMainScreen (Quiz List)
2. Click on any quiz → Quiz Detail Screen
3. Back button returns to Quiz List

## API Integration
- **Endpoint**: `GET /users/me/quizzes`
- **Authentication**: Bearer token (automatically handled)
- **Base URL**: `https://studymate.beerpsi.cc/api/v1/`
- **Error Handling**: Shows empty list on API errors

## Architecture (MVVM)
- **Model**: `Quiz.kt`, `QuizQuestion.kt` - Data classes with LocalDateTime support
- **View**: `QuizListScreen.kt`, `QuizDetailScreen.kt` - UI screens
- **ViewModel**: `QuizViewModel.kt` - Business logic and state management
- **Repository**: `QuizRepository.kt` - Data access layer
- **API**: `QuizApi.kt` - Retrofit interface

## Key Features
- **Automatic token management** via TokenManager and AuthInterceptor
- **JSON date parsing** for ISO 8601 format ("2025-09-11T02:22:18Z")
- **Error handling** with retry functionality
- **Loading states** with proper UI feedback
- **Navigation** integrated with app-wide navigation system

## Usage
The quiz system is automatically available after user login. No additional setup required.
