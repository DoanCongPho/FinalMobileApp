package com.example.finalproject.navigation
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finalproject.LoginPage
import com.example.finalproject.auth.register.data.FakeRegisterApi
import com.example.finalproject.auth.register.data.RegisterRepository
import com.example.finalproject.auth.register.data.RegisterViewModelFactory
import com.example.finalproject.auth.register.ui.RegisterScreen
import com.example.finalproject.auth.register.viewmodel.RegisterViewModel


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginPage(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            val vm: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(RegisterRepository(FakeRegisterApi))
            )
            RegisterScreen(
                vm = vm,
                onFinish = { navController.popBackStack() },
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}
