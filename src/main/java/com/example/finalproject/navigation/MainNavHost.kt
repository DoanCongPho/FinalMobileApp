package com.example.finalproject.navigation

import ChatListScreen
import ChatScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.finalproject.auth.register.ui.MonthScreen
import com.example.finalproject.calendar.data.CalendarRepository
import com.example.finalproject.calendar.data.FakeCalendarApi
import com.example.finalproject.calendar.viewmodel.CalendarViewModel
import com.example.finalproject.calendar.viewmodel.CalendarViewModelFactory
import com.example.finalproject.calendar.ui.CalendarScreen
import com.example.finalproject.calendar.ui.DayScreen
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1Factory
import com.example.finalproject.chatting.data.ConversationRepository
import com.example.finalproject.chatting.data.MessageRepository
import com.example.finalproject.chatting.data.UserRepository
import com.example.finalproject.chatting.ui.AddConversationScreen
import com.example.finalproject.chatting.viewmodel.AddConversationViewModel
import com.example.finalproject.chatting.viewmodel.AddConversationViewModelFactory
import com.example.finalproject.chatting.viewmodel.ConversationViewModel
import com.example.finalproject.chatting.viewmodel.ConversationViewModelFactory
import com.example.finalproject.core.DataStore.TokenManager
import com.example.finalproject.core.network.api.ApiClient
import java.time.LocalDate



@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val calendarViewModel: CalendarViewModel1 = viewModel(factory = CalendarViewModel1Factory())
    val context = LocalContext.current
    val tokenManager = TokenManager.getInstance(context)
    val apiHolder = ApiClient.create(tokenManager)
    val conversationRepo = ConversationRepository(apiHolder.conversationApi)
    val messageRepo = MessageRepository(apiHolder.messageApi)
    val conversationViewModel: ConversationViewModel = viewModel(
        factory = ConversationViewModelFactory(conversationRepo, messageRepo)
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
                conversationViewModel = conversationViewModel,
                tokenManager,
                onConversationClick = { conversationId, displayName ->
                    navController.navigate("chatScreen/$conversationId/$displayName")
                },
                onNewMessageClick = { navController.navigate(Screen.NewMessage.route) },
                onCreateGroupClick = { navController.navigate(Screen.CreateGroup.route) }

            )
        }


        composable(Screen.Study.route) {
            // StudyScreen()
        }

        composable(Screen.Account.route) {
            // ProfileScreen()
        }

        composable(Screen.Calendar.route) {
            val vm: CalendarViewModel = viewModel(factory = CalendarViewModelFactory(CalendarRepository(FakeCalendarApi)))
            CalendarScreen(
                viewModel = vm,
                onNavigateToStudy = { navController.navigate(Screen.Study.route) },
                onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                onNavigateToAccount = { navController.navigate(Screen.Account.route) }
            )
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

        composable(
            route = "chatScreen/{chatId}/{displayName}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.IntType },
                navArgument("displayName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getInt("chatId") ?: 0
            val displayName = backStackEntry.arguments?.getString("displayName") ?: "Chat"
            ChatScreen(
                chatId = chatId,
                displayName = displayName,
                navController = navController,
                conversationViewModel = viewModel(
                    factory = ConversationViewModelFactory(
                        ConversationRepository(apiHolder.conversationApi),
                        MessageRepository(apiHolder.messageApi)
                    )
                ),
                tokenManager = tokenManager
            )
        }

        composable(Screen.NewMessage.route) {
            val userRepo = UserRepository(apiHolder.userApi)

            AddConversationScreen(
                viewModel = viewModel(
                    factory = AddConversationViewModelFactory(userRepo, conversationViewModel)
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





    }
}
