package com.example.finalproject.createquiz.ui

import androidx.navigation.*
import androidx.navigation.compose.composable

fun NavGraphBuilder.createQuizGraph(navController: NavController) {
    navigation(startDestination = "create_quiz/choose", route = "create_quiz") {

        composable("create_quiz/choose") {
            ChooseSourceScreen(nav = navController)
        }

        composable("create_quiz/prompt") {
            PromptScreen(nav = navController)
        }

        composable(
            route = "create_quiz/success?quizId={quizId}",
            arguments = listOf(navArgument("quizId") { type = NavType.StringType; defaultValue = "" })
        ) {
            val quizId = it.arguments?.getString("quizId") ?: ""
            SuccessScreen(
                nav = navController,
                onGetThere = { /* TODO: navigate to your Quiz detail/list */ },
                onBackToMenu = { navController.popBackStack(route = "home", inclusive = false) },
                quizId = quizId,
                quizViewModel = null // No QuizViewModel available in this context
            )
        }
    }
}