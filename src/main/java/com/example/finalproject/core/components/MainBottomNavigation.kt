package com.example.finalproject.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun MainBottomNavigation(
    currentRoute: String,
    onCalendarClick: () -> Unit,
    onStudyClick: () -> Unit,
    onChatClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.CalendarToday, contentDescription = "Calendar") },
            label = { Text("Calendar") },
            selected = currentRoute == "calendar",
            onClick = onCalendarClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.School, contentDescription = "Study") },
            label = { Text("Study") },
            selected = currentRoute == "study",
            onClick = onStudyClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Chat, contentDescription = "Chat") },
            label = { Text("Chat") },
            selected = currentRoute == "chat",
            onClick = onChatClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Account") },
            label = { Text("Account") },
            selected = currentRoute == "account",
            onClick = onAccountClick
        )
    }
}
