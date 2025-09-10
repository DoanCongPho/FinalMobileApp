package com.example.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.finalproject.chatting.ui.ChatScreen
import com.example.finalproject.chatting.ui.MainChatList
import java.time.LocalDate



@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val calendarViewModel: CalendarViewModel1 = viewModel(factory = CalendarViewModel1Factory())

    NavHost(
        navController = navController,
        startDestination = Screen.Month.route,
        modifier = modifier
    ) {
        composable(Screen.Month.route) {
            MonthScreen(viewModel = calendarViewModel, navController = navController)
        }

        composable(Screen.Chat.route) {
            MainChatList(navController)
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
            route = "chatScreen/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatScreen(chatId = chatId, navController = navController)
        }
    }
}
