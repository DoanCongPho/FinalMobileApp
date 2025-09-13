package com.example.finalproject.journey.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.finalproject.journey.data.QuizRepository
import com.example.finalproject.journey.viewmodel.QuizViewModel
import com.example.finalproject.journey.viewmodel.QuizViewModelFactory

@Composable
fun QuizMainScreen(
    parentNavController: NavHostController? = null,
    viewModel: QuizViewModel
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "overall_stats"
    ) {
        composable("overall_stats") {
            val quizzes by viewModel.quizzes
            
            LaunchedEffect(Unit) {
                viewModel.loadQuizzes()
            }
            
            OverallStatsScreen(
                quizzes = quizzes,
                onClose = {
                    // Use parent navController to go back to the previous screen in MainNavHost
                    parentNavController?.popBackStack()
                },
                onSwitchToDetail = {
                    navController.navigate("quiz_list")
                }
            )
        }
        
        composable("quiz_list") {
            QuizListScreen(
                onQuizClick = { quiz ->
                    navController.navigate("quiz_detail/${quiz.id}")
                },
                onSwitchToStats = {
                    navController.navigate("overall_stats")
                },
                onBack = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
        
        composable(
            route = "quiz_detail/{quizId}",
            arguments = listOf(navArgument("quizId") { type = NavType.IntType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getInt("quizId") ?: return@composable
            val quiz = viewModel.getQuizById(quizId)
            
            if (quiz != null) {
                QuizDetailScreen(
                    quiz = quiz,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                // Handle case where quiz is not found - navigate back
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}