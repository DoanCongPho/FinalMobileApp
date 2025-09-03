package com.example.finalproject.navigation
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finalproject.AuthenPage
import com.example.finalproject.auth.login.data.FakeLoginApi
import com.example.finalproject.auth.login.data.LoginRepository
import com.example.finalproject.auth.login.data.LoginViewModelFactory
import com.example.finalproject.auth.login.ui.LoginScreen
import com.example.finalproject.auth.login.viewmodel.LoginViewModel
import com.example.finalproject.auth.register.data.FakeRegisterApi
import com.example.finalproject.auth.register.data.RegisterRepository
import com.example.finalproject.auth.register.data.RegisterViewModelFactory
import com.example.finalproject.auth.register.ui.RegisterScreen
import com.example.finalproject.auth.register.viewmodel.RegisterViewModel


sealed class Screen(val route: String) {
    object Authen: Screen("authen")
    object Register : Screen("register")
    object Login: Screen("login")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Authen.route) {
        composable(Screen.Authen.route) {
            AuthenPage (
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Register.route) {
            val vm: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(RegisterRepository(FakeRegisterApi))
            )
            RegisterScreen(
                vm = vm,
                onFinish = { navController.navigate(Screen.Authen.route)},
                onBackPressed = { navController.popBackStack() }
            )
        }
        composable(Screen.Login.route){
            val vm: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(LoginRepository(FakeLoginApi))
            )
            LoginScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onLoginSuccess = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) })
        }
    }
}
