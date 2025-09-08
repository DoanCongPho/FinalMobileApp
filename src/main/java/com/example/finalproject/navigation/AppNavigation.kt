package com.example.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.finalproject.AuthenPage
import com.example.finalproject.auth.login.data.FakeLoginApi
import com.example.finalproject.auth.login.data.LoginRepository
import com.example.finalproject.auth.login.data.LoginViewModelFactory
import com.example.finalproject.auth.login.ui.LoginScreen
import com.example.finalproject.auth.login.viewmodel.LoginViewModel
import com.example.finalproject.auth.register.data.FakeRegisterApi
import com.example.finalproject.auth.register.data.RegisterRepository
import com.example.finalproject.auth.register.data.RegisterViewModelFactory
import com.example.finalproject.auth.register.ui.MonthScreen
import com.example.finalproject.auth.register.ui.RegisterScreen
import com.example.finalproject.auth.register.viewmodel.RegisterViewModel
import com.example.finalproject.calendar.data.CalendarRepository
import com.example.finalproject.calendar.data.CalendarRepository1
import com.example.finalproject.calendar.data.FakeCalendarApi
import com.example.finalproject.calendar.viewmodel.CalendarViewModel
import com.example.finalproject.calendar.viewmodel.CalendarViewModelFactory
import com.example.finalproject.calendar.ui.CalendarScreen
import com.example.finalproject.Tasks.model.CalendarTask
import com.example.finalproject.Tasks.viewmodel.TaskViewModel
import com.example.finalproject.calendar.ui.DayScreen
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1Factory
import java.time.LocalDate

import com.example.finalproject.Tasks.ui.DailyScheduleScreen
import com.example.finalproject.Tasks.ui.AddTaskScreen
import com.example.finalproject.Tasks.ui.TaskDetailScreen
import com.example.finalproject.Tasks.ui.CustomRecurrenceScreen
sealed class Screen(val route: String) {
    object Authen: Screen("authen")
    object Register : Screen("register")
    object Login: Screen("login")
    object Calendar: Screen("calendar")
    object Study: Screen("study")
    object Chat: Screen("chat")
    object Account: Screen("account")
    object DailySchedule : Screen("daily_schedule/{date}")
    object AddTask : Screen("add_task/{date}")
    object TaskDetail : Screen("task_detail/{taskId}")
    object CustomRecurrence : Screen("custom_recurrence/{frequency}")
    object Month: Screen("month")
    object Day: Screen("day")
}

        
@Composable
fun AppNavigation(navController: NavHostController) {
    val calendarViewModel: CalendarViewModel1 = viewModel(
        factory = CalendarViewModel1Factory()
    )
    NavHost(navController = navController, startDestination = Screen.Month.route) {
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
                onLoginSuccess = { 
                    // Clear the back stack up to Authen and navigate to Calendar
                    navController.navigate(Screen.Calendar.route) {
                        popUpTo(Screen.Authen.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) })
        }
        composable(Screen.Calendar.route) {
            val vm: CalendarViewModel = viewModel(
                factory = CalendarViewModelFactory(CalendarRepository(FakeCalendarApi))
            )
            CalendarScreen(
                viewModel = vm,
                navController = navController,
                onNavigateToStudy = { navController.navigate(Screen.Study.route) },
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToAccount = { navController.navigate(Screen.Account.route) }
            )
        }
        composable(Screen.DailySchedule.route) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val taskViewModel: TaskViewModel = viewModel()
            DailyScheduleScreen(
                date = date,
                tasks = taskViewModel.tasks,
                onTaskClick = { taskId ->
                    navController.navigate("task_detail/$taskId")
                },
                onAddClick = {
                    navController.navigate("add_task/$date")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.AddTask.route) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val taskViewModel: TaskViewModel = viewModel()
            
            val customRecurrenceCallback = remember {
                { selectedFrequency: com.example.finalproject.Tasks.model.RepeatFrequency ->
                    val route = "custom_recurrence/${selectedFrequency.name}"
                    navController.popBackStack("custom_recurrence/{frequency}", inclusive = true)
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            }
            
            AddTaskScreen(
                date = date,
                onCancel = { navController.popBackStack() },
                onSave = { newTask ->
                    taskViewModel.addTask(newTask)
                    navController.popBackStack()
                },
                onCustomRecurrence = customRecurrenceCallback
            )
        }
        composable(Screen.TaskDetail.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val taskViewModel: TaskViewModel = viewModel()
            val task = taskViewModel.tasks.find { it.id == taskId }
            if (task != null) {
                TaskDetailScreen(
                    task = task,
                    onClose = { navController.popBackStack() },
                    onEdit = { editedTask ->
                        // For edit, you may want to navigate to an edit screen or show a dialog
                        // Here, just update the task in the ViewModel
                        // Example: taskViewModel.updateTask(editedTask)
                    },
                    onDelete = { deletedTask ->
                        taskViewModel.deleteTask(deletedTask)
                        navController.popBackStack()
                    },
                        onToggleState = { toggledTask ->
                        taskViewModel.toggleTaskState(toggledTask)
                    }
                )
            } else {
                // fallback UI if task not found
            }
        }

        composable(
            route = "custom_recurrence/{frequency}",
            arguments = listOf(navArgument("frequency") { 
                type = NavType.StringType 
                defaultValue = "NONE"
            })
        ) { backStackEntry ->
            val freqName = backStackEntry.arguments?.getString("frequency") ?: "NONE"
            val initialFrequency = try {
                com.example.finalproject.Tasks.model.RepeatFrequency.valueOf(freqName)
            } catch (e: Exception) {
                com.example.finalproject.Tasks.model.RepeatFrequency.NONE
            }
            
            CustomRecurrenceScreen(
                initialFrequency = initialFrequency,
                initialMonthlyPattern = null,
                initialRepeatEnd = com.example.finalproject.Tasks.model.RepeatEnd.Never,
                onDone = { freq, monthlyPattern, repeatEnd ->
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Month.route) {
            MonthScreen(viewModel = calendarViewModel, navController = navController)
        }

        composable(
            route = "day/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date") ?: ""
            val date = LocalDate.parse(dateString)

            DayScreen(
                date = date,
                viewModel = calendarViewModel,
                onDateChange = { newDate ->
                    navController.navigate("day/$newDate") {
                        popUpTo("day/{date}") { inclusive = true }
                    }
                }
            )
        }


    }
}
