//package com.example.finalproject.createquiz.ui
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.*
//import androidx.navigation.compose.composable
//import com.example.finalproject.createquiz.viewmodel.CreateQuizViewModel
//import com.example.finalproject.createquiz.viewmodel.CreateQuizViewModelFactory
//
//fun NavGraphBuilder.createQuizGraph(navController: NavController) {
//    navigation(startDestination = "create_quiz/choose", route = "create_quiz") {
//
//        composable("create_quiz/choose") {
//            val context = LocalContext.current
//            val factory = remember { CreateQuizViewModelFactory(context) }
//            val vm: CreateQuizViewModel = viewModel(factory = factory)
//            ChooseSourceScreen(nav = navController, vm = vm)
//        }
//
//        composable("create_quiz/prompt") {
//            val context = LocalContext.current
//            val factory = remember { CreateQuizViewModelFactory(context) }
//            val vm: CreateQuizViewModel = viewModel(factory = factory)
//            PromptScreen(nav = navController, vm = vm)
//        }
//
//        composable(
//            route = "create_quiz/success?quizId={quizId}",
//            arguments = listOf(navArgument("quizId") { type = NavType.StringType; defaultValue = "" })
//        ) {
//            SuccessScreen(
//                nav = navController,
//                onGetThere = { /* TODO: navigate to your Quiz detail/list */ },
//                onBackToMenu = { navController.popBackStack(route = "home", inclusive = false) }
//            )
//        }
//    }
//}
