package com.example.finalproject.navigation

import ChatListScreen
import ChatScreen
import androidx.compose.runtime.Composable
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
import com.example.finalproject.Tasks.ui.CustomRecurrenceScreen
import com.example.finalproject.Tasks.ui.DailyScheduleScreen
import com.example.finalproject.Tasks.ui.EditTaskScreen
import com.example.finalproject.Tasks.ui.EndsScreen
import com.example.finalproject.Tasks.ui.TaskDetailScreen
import com.example.finalproject.auth.register.ui.MonthScreen
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

import com.example.finalproject.chatting.viewmodel.ConversationViewModel
import com.example.finalproject.core.DataStore.TokenManager
import com.example.finalproject.core.network.api.ApiClient
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
import java.time.LocalDate


@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val calendarViewModel: CalendarViewModel1 = viewModel(factory = CalendarViewModel1Factory())
    val context = LocalContext.current
    val tokenManager = TokenManager.getInstance(context)
    val apiHolder = ApiClient.create(tokenManager)
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

            )
        }


        composable(Screen.Study.route) {
             StudyScreen(navController = navController)
        }

        composable(Screen.Account.route) {
            // ProfileScreen()
        }


        composable(Screen.Calendar.route) {
            val vm: CalendarViewModel =
                viewModel(factory = CalendarViewModelFactory(CalendarRepository(FakeCalendarApi)))
            CalendarScreen(
                viewModel = vm,
                navController,
                onNavigateToStudy = { navController.navigate(Screen.Study.route) },
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToAccount = { navController.navigate(Screen.Account.route) }
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
            StudyScreen(navController = navController)
        }
        composable(Screen.Pomodoro.route) {
            PomodoroScreen(navController)
        }





        composable(Screen.Review.route) {
            // supply your DI repo here; replace FakeReviewRepository with real one when ready
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

