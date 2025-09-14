package com.example.finalproject.navigation

import ChatListScreen
import ChatScreen
import ProfileScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.finalproject.Tasks.ui.AddTaskScreen
import com.example.finalproject.Tasks.ui.AddTasklistScreen
import com.example.finalproject.Tasks.ui.CustomRecurrenceScreen
import com.example.finalproject.Tasks.ui.DailyScheduleScreen
import com.example.finalproject.Tasks.ui.EditTaskScreen
import com.example.finalproject.Tasks.ui.EndsScreen
import com.example.finalproject.Tasks.ui.TaskActionSelectionScreen
import com.example.finalproject.Tasks.ui.TaskDetailScreen
import com.example.finalproject.Tasks.viewmodel.TaskViewModel
import com.example.finalproject.calendar.data.CalendarRepository
import com.example.finalproject.calendar.data.FakeCalendarApi
import com.example.finalproject.calendar.viewmodel.CalendarViewModel
import com.example.finalproject.calendar.viewmodel.CalendarViewModelFactory
import com.example.finalproject.calendar.ui.CalendarScreen
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1Factory
import com.example.finalproject.chatting.data.ConversationRepository
import com.example.finalproject.chatting.data.MessageRepository
import com.example.finalproject.chatting.data.UserRepository
import com.example.finalproject.chatting.ui.AddConversationScreen
import com.example.finalproject.chatting.viewmodel.AddConversationChatRoomViewModelFactory
import com.example.finalproject.chatting.viewmodel.ChatRoomManagerViewModel
import com.example.finalproject.chatting.viewmodel.ChatRoomManagerViewModelFactory
import com.example.finalproject.core.DataStore.TokenManager
import com.example.finalproject.core.network.api.ApiClient
import com.example.finalproject.study.viewmodel.StudyViewModel
import com.example.finalproject.study.viewmodel.StudyViewModelFactory
import com.example.finalproject.createquiz.data.CreateQuizRepository
import com.example.finalproject.createquiz.data.CreateQuizViewModelFactory
import com.example.finalproject.createquiz.viewmodel.CreateQuizViewModel
import com.example.finalproject.createquiz.viewmodel.ManualQuizViewModel
import com.example.finalproject.journey.data.QuizRepository
import com.example.finalproject.journey.ui.QuizMainScreen
import com.example.finalproject.journey.viewmodel.QuizViewModel
import com.example.finalproject.journey.viewmodel.QuizViewModelFactory
import com.example.finalproject.pomodoro.ui.PomodoroScreen
import com.example.finalproject.study.ui.StudyScreen
import com.example.finalproject.calendar.ui.MonthScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import openCustomTab
import java.time.LocalDate


@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier,  onLogout: () -> Unit) {
    val calendarViewModel: CalendarViewModel1 = viewModel(factory = CalendarViewModel1Factory())
    val context = LocalContext.current
    val tokenManager = TokenManager.getInstance(context)
    val apiHolder = ApiClient.create(tokenManager)

    // Initialize TaskListApi in the repository
    LaunchedEffect(Unit) {
        com.example.finalproject.calendar.data.CalendarRepository1.setTaskListApi(apiHolder.taskListApi)
        calendarViewModel.loadTasks()
    }

    val conversationRepo = ConversationRepository(apiHolder.conversationApi)
    val messageRepo = MessageRepository(apiHolder.messageApi)

    val userRepo = UserRepository(apiHolder.userApi)
    val chatRoomManager: ChatRoomManagerViewModel = viewModel(
        factory = ChatRoomManagerViewModelFactory(
            conversationRepo,
            messageRepo,
            tokenManager,
            userRepo
        )
    )
    NavHost(
        navController = navController,
        startDestination = Screen.Chat.route,
        modifier = modifier
    ) {
        composable(Screen.Month.route) {
            MonthScreen(viewModel = calendarViewModel, navController = navController)
        }

        composable(Screen.Chat.route) {
            ChatListScreen(
                chatRoomManager = chatRoomManager,
                tokenManager,
                onConversationClick = { conversationId, displayName ->
                    navController.navigate("chatScreen/$conversationId/$displayName")
                },
                onNewMessageClick = { navController.navigate(Screen.NewMessage.route) },
                onChatGpt = {
                    // mở ChatGPT ngoài browser
                    openCustomTab(context, "https://chat.openai.com/")
                }
            )
        }



        composable(Screen.Study.route) {
            val context = LocalContext.current
            val tokenManager = remember { TokenManager.getInstance(context) }
            val studyViewModel: StudyViewModel = viewModel(
                factory = StudyViewModelFactory(tokenManager = tokenManager)
            )
            StudyScreen(navController = navController, vm = studyViewModel)
        }


        composable(Screen.Account.route) {
            val context = LocalContext.current
            val tokenManager = remember { TokenManager.getInstance(context) }

            val fullNameState = tokenManager.userName.collectAsState(initial = "")
            val emailState = tokenManager.userEmail.collectAsState(initial = "")
            val phoneState = tokenManager.userPhone.collectAsState(initial = "")
            ProfileScreen(
                fullName = fullNameState.value ?: "",
                email = emailState.value ?: "",
                phoneNumber = phoneState.value ?: "",
                onLogout = {
                    CoroutineScope(Dispatchers.IO).launch {
                        tokenManager.clear()
                    }
                    onLogout()
                }
            )
        }


        composable(Screen.Calendar.route) {
            CalendarScreen(
                viewModel = calendarViewModel,
                navController = navController
            )
        }
        composable(
            route = "chatScreen/{chatId}/{displayName}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.IntType },
                navArgument("displayName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getInt("chatId") ?: 0
            val displayName = backStackEntry.arguments?.getString("displayName") ?: "Chat"

            // Tạo ChatRoomManagerViewModel một lần


            ChatScreen(
                chatId = chatId,
                displayName = displayName,
                navController = navController,
                chatRoomManager = chatRoomManager,
                tokenManager = tokenManager
            )
        }


        composable(Screen.NewMessage.route) {
            val userRepo = UserRepository(apiHolder.userApi)


            AddConversationScreen(
                viewModel = viewModel(
                    factory = AddConversationChatRoomViewModelFactory(userRepo, chatRoomManager)
                ),
                navController = navController, // <-- pass navController
                onConversationCreated = { convo ->
                    // Navigate to chat screen after creation

                    navController.navigate("chatScreen/${convo.id}/${convo.name ?: "Chat"}") {
                        popUpTo(Screen.Chat.route) { inclusive = false }
                    }
                }
            )
        }


        composable(Screen.DailySchedule.route) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date") ?: ""
//          val taskViewModel: TaskViewModel = viewModel()
            val date = LocalDate.parse(dateString)
            val tasks = calendarViewModel.tasks.filter { it.date == date }
            val taskLists = calendarViewModel.taskLists
            DailyScheduleScreen(
                date = dateString,
                tasks = tasks,
                taskLists = taskLists,
                onTaskClick = { taskId ->
                    navController.navigate("task_detail/$taskId")
                },
                onAddClick = {
                    navController.navigate("task_action_selection/$dateString")
                },
                onAddTaskListClick = {
                    navController.navigate(Screen.AddTasklist.route)
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
                calendarViewModel = calendarViewModel,
                onNavigateToAddTaskList = {
                    navController.navigate(Screen.AddTasklist.route)
                }
            )
        }

        composable(Screen.TaskActionSelection.route) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date") ?: ""

            TaskActionSelectionScreen(
                onAddTask = {
                    navController.navigate("add_task/$dateString")
                },
                onAddTaskList = {
                    navController.navigate(Screen.AddTasklist.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AddTasklist.route) {
            val taskLists = calendarViewModel.taskLists

            AddTasklistScreen(
                taskLists = taskLists,
                calendarViewModel = calendarViewModel,
                onBack = { navController.popBackStack() }
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
                    calendarViewModel = calendarViewModel,
                    onNavigateToAddTaskList = {
                        navController.navigate(Screen.AddTasklist.route)
                    }
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
            val currentRepeatEnd =
                draftTask?.repeatEnd ?: com.example.finalproject.Tasks.model.RepeatEnd.Never


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
            val currentRepeatEnd =
                draftTask?.repeatEnd ?: com.example.finalproject.Tasks.model.RepeatEnd.Never

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

        composable(Screen.Study.route) {
            val context = LocalContext.current
            val tokenManager = remember { TokenManager.getInstance(context) }
            val studyViewModel: StudyViewModel = viewModel(
                factory = StudyViewModelFactory(tokenManager = tokenManager)
            )
            StudyScreen(navController = navController, vm = studyViewModel)
        }
        composable(Screen.Pomodoro.route) {
            PomodoroScreen(navController)
        }

        composable(Screen.Review.route) {
            
            // Create QuizViewModel - store it in a remember to persist across recompositions
            val quizRepository = remember { com.example.finalproject.journey.data.QuizRepository(apiHolder.quizApi) }
            val quizViewModel: com.example.finalproject.journey.viewmodel.QuizViewModel = 
                viewModel(factory = com.example.finalproject.journey.viewmodel.QuizViewModelFactory(quizRepository))
            
            com.example.finalproject.review.ui.ReviewRoute(
                onBack = { navController.popBackStack() },
                onCreateQuiz = { navController.navigate(Screen.CreateQuizRoot.route) },
                onFindFriends = { /* nav to friends */ },
                onShowQuiz = { quiz -> 
                    // Store quiz in the navigation holder
                    com.example.finalproject.review.ui.QuizNavigationHolder.selectedQuiz = quiz
                    navController.navigate("flashcard/${quiz.id}")
                },
                quizViewModel = quizViewModel
            )
        }
        
        composable(
            route = Screen.Flashcard.route,
            arguments = listOf(navArgument("quizId") { type = NavType.IntType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getInt("quizId") ?: 0
            
            // Get quiz from the navigation holder
            val selectedQuiz = com.example.finalproject.review.ui.QuizNavigationHolder.selectedQuiz
            
            com.example.finalproject.review.ui.FlashcardScreen(
                quiz = selectedQuiz,
                onBack = { 
                    // Clear the selected quiz when navigating back
                    com.example.finalproject.review.ui.QuizNavigationHolder.selectedQuiz = null
                    navController.popBackStack() 
                }
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
                    onCreateManual = { navController.navigate(Screen.CreateQuizManual.route) },
                    onBack = { navController.popBackStack() }
                )
            }

            // Choose Source (AI path)
            composable(Screen.CreateQuizChoose.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.CreateQuizRoot.route)
                }
                // ✅ use the ApiClient INSTANCE you created above, not the type name
                val createQuizVm: CreateQuizViewModel = viewModel(
                    parentEntry,
                    factory = CreateQuizViewModelFactory(
                        CreateQuizRepository(apiHolder.quizApi)
                    )
                )
                com.example.finalproject.createquiz.ui.ChooseSourceScreen(
                    nav = navController,
                    vm = createQuizVm
                )
            }

            // Prompt Screen (AI configuration)
            composable(Screen.CreateQuizPrompt.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.CreateQuizRoot.route)
                }
                val createQuizVm: CreateQuizViewModel = viewModel(
                    parentEntry,
                    factory = CreateQuizViewModelFactory(
                        CreateQuizRepository(apiHolder.quizApi)
                    )
                )
                com.example.finalproject.createquiz.ui.PromptScreen(
                    nav = navController,
                    vm = createQuizVm
                )
            }


            composable(Screen.CreateQuizManual.route) {
                val vm: ManualQuizViewModel = viewModel()
                com.example.finalproject.createquiz.ui.ManualQuizScreen(
                    viewModel = vm,
                    onFinish = {
                        // Call the real API instead of hardcoded navigation
                        vm.submitQuiz(title = "Manual Quiz") { realQuizId ->
                            navController.navigate("create_quiz/success?quizId=$realQuizId") {
                                popUpTo(Screen.CreateQuizRoot.route) { inclusive = false }
                                launchSingleTop = true
                            }
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

        composable(Screen.Quiz.route) {
            val quizViewModel: QuizViewModel = viewModel(
                factory = QuizViewModelFactory(QuizRepository(apiHolder.quizApi))
            )
            QuizMainScreen(
                parentNavController = navController,
                viewModel = quizViewModel
            )
        }
        
        // Temporarily disabled API test screen due to compilation issues
        // composable("api_test") {
        //     com.example.finalproject.test.QuizApiTestScreen(
        //         tokenManager = tokenManager,
        //         onBack = { navController.popBackStack() }
        //     )
        // }

    }
}



