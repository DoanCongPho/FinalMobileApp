package com.example.finalproject.navigation
import androidx.compose.runtime.Composable
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
import com.example.finalproject.auth.register.ui.MonthScreen
import com.example.finalproject.auth.register.ui.RegisterScreen
import com.example.finalproject.auth.register.ui.SuccessRegistrationScreen
import com.example.finalproject.auth.register.viewmodel.RegisterViewModel
import com.example.finalproject.auth.register.viewmodel.RegisterViewModelFactory
import com.example.finalproject.calendar.data.CalendarRepository
import com.example.finalproject.calendar.data.FakeCalendarApi
import com.example.finalproject.calendar.viewmodel.CalendarViewModel
import com.example.finalproject.calendar.viewmodel.CalendarViewModelFactory
import com.example.finalproject.calendar.ui.CalendarScreen
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1Factory
import com.example.finalproject.chatting.ui.ChatScreen
import com.example.finalproject.chatting.ui.MainChatList
import com.example.finalproject.core.DataStore.TokenManager
import java.time.LocalDate
import com.example.finalproject.Tasks.ui.DailyScheduleScreen
import com.example.finalproject.Tasks.ui.AddTaskScreen
import com.example.finalproject.Tasks.ui.TaskDetailScreen
import com.example.finalproject.Tasks.ui.EditTaskScreen
import com.example.finalproject.Tasks.ui.CustomRecurrenceScreen
import com.example.finalproject.Tasks.ui.EndsScreen
import com.example.finalproject.study.ui.StudyScreen
import com.example.finalproject.pomodoro.ui.PomodoroScreen
import com.example.finalproject.createquiz.data.CreateQuizRepository
import com.example.finalproject.createquiz.viewmodel.CreateQuizViewModel
import com.example.finalproject.core.network.api.ApiClient
import androidx.navigation.navigation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.createquiz.viewmodel.ManualQuizViewModel



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
    object AddTask : Screen("add_task/{date}")
    object EditTask : Screen("edit_task/{taskId}")
    object TaskDetail : Screen("task_detail/{taskId}")
    object CustomRecurrence : Screen("custom_recurrence/{frequency}")
    object Ends : Screen("ends")
    object Month: Screen("month")
    object Day: Screen("day")
    object Pomodoro: Screen("pomodoro")
    object Review: Screen ("review")
    object CreateQuizRoot : Screen("create_quiz")
    object CreateQuizChoose : Screen("create_quiz/choose")
    object CreateQuizSuccess : Screen("create_quiz/success?quizId={quizId}")
    object CreateQuizMode : Screen("create_quiz/mode")
    object CreateQuizManual : Screen("create_quiz/manual")
}

        
@Composable
fun AppNavigation(navController: NavHostController) {
    val calendarViewModel: CalendarViewModel1 = viewModel(
        factory = CalendarViewModel1Factory()
    )
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    class CreateQuizViewModelFactory(
        private val repo: CreateQuizRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CreateQuizViewModel(repo) as T
        }
    }


    NavHost(navController = navController, startDestination = Screen.Month.route) {
        composable(Screen.Authen.route) {
            AuthenPage (
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Register.route) {
            val vm: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(RegisterRepository())
            )
            RegisterScreen(
                vm = vm,
                onFinish = { navController.navigate(Screen.Authen.route) },
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
            val vm: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(LoginRepository(), tokenManager)
            )
            LoginScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(Screen.Month.route) {
                        popUpTo(Screen.Authen.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
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
            val dateString = backStackEntry.arguments?.getString("date") ?: ""
//          val taskViewModel: TaskViewModel = viewModel()
            val date = LocalDate.parse(dateString)
            val tasks = calendarViewModel.tasks.filter { it.date == date }
            DailyScheduleScreen(
                date = dateString,
                tasks = tasks,
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
                    // Task series management is now handled directly in AddTaskScreen
                    navController.popBackStack()
                },
                onCustomRecurrence = customRecurrenceCallback,
                calendarViewModel = calendarViewModel
            )
        }
        composable(Screen.TaskDetail.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val task = calendarViewModel.tasks.find { it.id == taskId }
            if (task != null) {
                TaskDetailScreen(
                    task = task,
                    onClose = { navController.popBackStack() },
                    onEdit = { taskToEdit ->
                        navController.navigate("edit_task/${taskToEdit.id}")
                    },
                    onDelete = { deletedTask ->
                        calendarViewModel.deleteTask(deletedTask)
                        navController.popBackStack()
                    },
                    onToggleState = { toggledTask ->
                        calendarViewModel.toggleTaskState(toggledTask)
                    }
                )
            } else {
                // fallback UI if task not found
            }
        }

        composable(Screen.EditTask.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val task = calendarViewModel.tasks.find { it.id == taskId }
            if (task != null) {
                val customRecurrenceCallback = remember {
                    { selectedFrequency: com.example.finalproject.Tasks.model.RepeatFrequency ->
                        navController.navigate("custom_recurrence/${selectedFrequency.name}")
                    }
                }
                
                EditTaskScreen(
                    task = task,
                    onCancel = { navController.popBackStack() },
                    onSave = { editedTask ->
                        // Task series management is now handled directly in EditTaskScreen
                        navController.popBackStack()
                    },
                    onCustomRecurrence = customRecurrenceCallback,
                    calendarViewModel = calendarViewModel
                )
            } else {
                // fallback UI if task not found
                navController.popBackStack()
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
            
            val draftTask = calendarViewModel.draftTask
            val currentRepeatEnd = draftTask?.repeatEnd ?: com.example.finalproject.Tasks.model.RepeatEnd.Never
            

            CustomRecurrenceScreen(
                initialFrequency = initialFrequency,
                initialMonthlyPattern = null,
                initialRepeatEnd = currentRepeatEnd,
                onDone = { freq, monthlyPattern, repeatEnd ->
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                },
                calendarViewModel = calendarViewModel,
                onNavigateToEnds = {
                    navController.navigate(Screen.Ends.route)
                }
            )
        }
        composable(Screen.Ends.route) {
            val draftTask = calendarViewModel.draftTask
            val currentRepeatEnd = draftTask?.repeatEnd ?: com.example.finalproject.Tasks.model.RepeatEnd.Never
            
            EndsScreen(
                initialRepeatEnd = currentRepeatEnd,
                onDone = { repeatEnd ->
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                },
                calendarViewModel = calendarViewModel
            )
        }
        composable(Screen.Month.route) {
            MonthScreen(viewModel = calendarViewModel, navController = navController)
        }
        composable(Screen.Study.route) {
            StudyScreen(navController = navController)
        }
        composable(Screen.Pomodoro.route) {
            PomodoroScreen(navController)
        }
        composable(Screen.Chat.route){
            MainChatList(navController)
        }

        composable(
            route = "chatScreen/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatScreen(chatId = chatId, navController = navController)
        }

        composable(Screen.Review.route) {
            val factory = com.example.finalproject.review.viewmodel.ReviewViewModelFactory(
                repo = com.example.finalproject.review.data.FakeReviewRepository()
            )
            com.example.finalproject.review.ui.ReviewRoute(
                onBack = { navController.popBackStack() },
                onCreateQuiz = { navController.navigate(Screen.CreateQuizRoot.route) },
                onFindFriends = { /* nav to friends */ },
                factory = factory
            )
        }
        navigation(
            startDestination = Screen.CreateQuizMode.route, // now starts at mode screen
            route = Screen.CreateQuizRoot.route
        ) {
            // Mode screen
            composable(Screen.CreateQuizMode.route) {
                com.example.finalproject.createquiz.ui.ChooseModeScreen(
                    onCreateByAI = { navController.navigate(Screen.CreateQuizChoose.route) },
                    onCreateManual = { navController.navigate(Screen.CreateQuizManual.route) }
                )
            }

            // Choose Source (AI path)
            composable(Screen.CreateQuizChoose.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.CreateQuizRoot.route)
                }
                val createQuizVm: CreateQuizViewModel = viewModel(
                    parentEntry,
                    factory = CreateQuizViewModelFactory(CreateQuizRepository(ApiClient.quizApi))
                )
                com.example.finalproject.createquiz.ui.ChooseSourceScreen(nav = navController, vm = createQuizVm)
            }

            composable(Screen.CreateQuizManual.route) {
                val vm: ManualQuizViewModel = viewModel()
                com.example.finalproject.createquiz.ui.ManualQuizScreen(
                    viewModel = vm,
                    onFinish = {
                        navController.navigate("create_quiz/success?quizId=manual_dev") {
                            popUpTo(Screen.CreateQuizRoot.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Success
            composable(
                Screen.CreateQuizSuccess.route,
                arguments = listOf(navArgument("quizId") {
                    type = NavType.StringType
                    defaultValue = ""
                })
            ) {
                com.example.finalproject.createquiz.ui.SuccessScreen(
                    nav = navController,
                    onGetThere = { navController.navigate(Screen.Study.route) },
                    onBackToMenu = { navController.popBackStack() }
                )
            }
        }
    }
}
