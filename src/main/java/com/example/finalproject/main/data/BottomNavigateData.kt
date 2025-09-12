package com.example.finalproject.main.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(val route: String, val label: String, val icon: ImageVector) {
    object Calendar : BottomNavScreen("month", "Calendar", Icons.Default.DateRange)
    object Study : BottomNavScreen("study", "Study", Icons.Default.School)
    object Chat : BottomNavScreen("chat", "Chat", Icons.Default.Chat)
    object Account : BottomNavScreen("account", "Account", Icons.Default.Person)

    companion object {
        val items = listOf(Calendar, Study, Chat, Account)
    }
}
