package com.example.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.example.finalproject.core.DataStore.TokenManager
import com.example.finalproject.main.ui.MainScreen

sealed class Screen(val route: String) {
    object Authen: Screen("authen")
    object Register : Screen("register")
    object SuccessRegister: Screen("successRegister")
    object Login: Screen("login")
    object Calendar: Screen("calendar")
    object Study: Screen("study")
    object Chat: Screen("chat")
    object Account: Screen("account")
    object Month: Screen("month")
    object Main: Screen("main")
}
@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)

    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Authen.route) {
            AuthenPage(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Register.route) {
            val vm: RegisterViewModel = viewModel(factory = RegisterViewModelFactory(RegisterRepository()))
            RegisterScreen(
                vm = vm,
                onFinish = { navController.navigate(Screen.SuccessRegister.route)},
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

        composable(Screen.Login.route){
            val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(LoginRepository(), tokenManager))
            LoginScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo(Screen.Authen.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // ✅ Sau khi login → chuyển sang main app flow
        composable("main") {
            MainScreen()
        }
    }
}
