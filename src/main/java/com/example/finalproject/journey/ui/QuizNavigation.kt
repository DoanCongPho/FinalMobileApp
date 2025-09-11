//package com.example.finalproject.journey.ui
//
//import androidx.compose.runtime.*
//import androidx.navigation.NavController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.finalproject.journey.model.Quiz
//import com.google.gson.Gson
//
//@Composable
//fun QuizNavigation(
//    startDestination: String = "quiz_list"
//) {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = startDestination
//    ) {
//        composable("quiz_list") {
//            QuizListScreen(
//                onQuizClick = { quiz ->
//                    val quizJson = Gson().toJson(quiz)
//                    navController.navigate("quiz_detail/$quizJson")
//                }
//            )
//        }
//
//        composable("quiz_detail/{quizJson}") { backStackEntry ->
//            val quizJson = backStackEntry.arguments?.getString("quizJson")
//            val quiz = Gson().fromJson(quizJson, Quiz::class.java)
//
//            QuizDetailScreen(
//                quiz = quiz,
//                onBackClick = {
//                    navController.popBackStack()
//                }
//            )
//        }
//    }
//}
