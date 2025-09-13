package com.example.finalproject.navigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.finalproject.AuthenPage
import com.example.finalproject.auth.login.data.LoginRepository
import com.example.finalproject.auth.login.viewmodel.LoginViewModel
import com.example.finalproject.auth.login.viewmodel.LoginViewModelFactory
import com.example.finalproject.auth.login.ui.LoginScreen
import com.example.finalproject.auth.register.data.RegisterRepository
import com.example.finalproject.auth.register.ui.RegisterScreen
import com.example.finalproject.auth.register.ui.SuccessRegistrationScreen
import com.example.finalproject.auth.register.viewmodel.RegisterViewModel
import com.example.finalproject.auth.register.viewmodel.RegisterViewModelFactory

import com.example.finalproject.core.network.api.ApiClient

import com.example.finalproject.core.DataStore.TokenManager
import com.example.finalproject.main.ui.MainScreen
import com.google.common.base.Defaults.defaultValue

sealed class Screen(val route: String) {
    object Authen: Screen("authen")
    object Register : Screen("register")
    object SuccessRegister: Screen("successRegister")
    object Login: Screen("login")
    object Calendar: Screen("calendar")
    object Study: Screen("study")
    object Chat: Screen("chat")
    object Account: Screen("account")
    object DailySchedule : Screen("daily_schedule/{date}")
    object TaskActionSelection : Screen("task_action_selection/{date}")
    object AddTask : Screen("add_task/{date}")
    object AddTasklist : Screen("add_tasklist")
    object EditTask : Screen("edit_task/{taskId}")
    object TaskDetail : Screen("task_detail/{taskId}")
    object CustomRecurrence : Screen("custom_recurrence/{frequency}")
    object Ends : Screen("ends")
    object Month: Screen("month")
    object Pomodoro: Screen("pomodoro")
    object Review: Screen ("review")
    object Quiz: Screen("quiz")
    object CreateQuizRoot : Screen("create_quiz")
    object CreateQuizChoose : Screen("create_quiz/choose")
    object CreateQuizSuccess : Screen("create_quiz/success?quizId={quizId}")
    object CreateQuizMode : Screen("create_quiz/mode")
    object CreateQuizManual : Screen("create_quiz/manual")
    object Main: Screen("main")
    object NewMessage: Screen("newMessage")
    object ChatGpt: Screen("chatGpt")

}


@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val tokenManager = TokenManager.getInstance(context)
    val apiHolder = ApiClient.create(tokenManager)



    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Authen.route) {
            AuthenPage(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Register.route) {
            val vm: RegisterViewModel =
                viewModel(factory = RegisterViewModelFactory(RegisterRepository(apiHolder)))
            RegisterScreen(
                vm = vm,
                onFinish = { navController.navigate(Screen.SuccessRegister.route) },
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Screen.SuccessRegister.route) {
            SuccessRegistrationScreen(
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Authen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val vm: LoginViewModel =
                viewModel(factory = LoginViewModelFactory(LoginRepository(apiHolder), tokenManager))
            LoginScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
//                    navController.navigate(Screen.Quiz.route) {
                    navController.navigate("main") {
                        popUpTo(Screen.Authen.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable("main") {
            MainScreen()
        }
    }
}
