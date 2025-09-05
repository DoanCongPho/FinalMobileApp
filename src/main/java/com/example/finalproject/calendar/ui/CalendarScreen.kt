package com.example.finalproject.calendar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.calendar.model.CalendarEvent
import com.example.finalproject.calendar.model.EventType
import com.example.finalproject.calendar.viewmodel.CalendarViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToStudy: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToAccount: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Calendar") },
                    label = { Text("Calendar") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.School, contentDescription = "Study") },
                    label = { Text("Study") },
                    selected = false,
                    onClick = onNavigateToStudy
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
                    label = { Text("Chat") },
                    selected = false,
                    onClick = onNavigateToChat
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                    label = { Text("Account") },
                    selected = false,
                    onClick = onNavigateToAccount
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.selectedDate.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()) + 
                              " " + uiState.selectedDate.year,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                CalendarGrid(
                    selectedDate = uiState.selectedDate.toLocalDate(),
                    onDateSelect = { /* Handle date selection */ },
                    events = uiState.events.map { it.time.toLocalDate() }
                )
            }

            // Today's events section
            item {
                Text(
                    text = "Today",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(uiState.events.filter { it.time.dayOfYear == LocalDateTime.now().dayOfYear }) { event ->
                EventItem(event)
            }

            // Tomorrow's events section
            item {
                Text(
                    text = "Tomorrow",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(uiState.events.filter { it.time.dayOfYear == LocalDateTime.now().plusDays(1).dayOfYear }) { event ->
                EventItem(event)
            }

            // Vacations section
            item {
                Text(
                    text = "Vacations",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(uiState.events.filter { it.type == EventType.VACATION }) { event ->
                EventItem(event)
            }
        }
    }
}

@Composable
fun EventItem(event: CalendarEvent) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM")
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFEFEBF2)  // Lighter grayish-lavender color
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = event.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    if (event.type == EventType.VACATION) {
                        Text(
                            text = "${event.time.format(DateTimeFormatter.ofPattern("dd-MM"))} to ${event.time.plusDays(14).format(DateTimeFormatter.ofPattern("dd-MM"))}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    } else {
                        Text(
                            text = String.format("%02d:%02d", event.time.hour, event.time.minute),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            when (event.type) {
                EventType.DAILY_STANDUP -> Icon(
                    Icons.Default.Group,
                    contentDescription = "Standup",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Blue
                )
                EventType.MEETING -> Icon(
                    Icons.Default.Business,
                    contentDescription = "Meeting",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Red
                )
                EventType.VACATION -> Icon(
                    Icons.Default.FlightTakeoff,
                    contentDescription = "Vacation",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Blue
                )
                else -> Icon(
                    Icons.Default.Event,
                    contentDescription = "Event",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Blue
                )
            }
        }
    }
}
