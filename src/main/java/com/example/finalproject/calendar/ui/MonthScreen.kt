package com.example.finalproject.auth.register.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.finalproject.Tasks.model.CalendarTask
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.finalproject.calendar.viewmodel.CalendarViewModel1

import com.example.finalproject.R

@Composable
fun MonthScreen(
    viewModel: CalendarViewModel1,
    navController: NavController
) {
    val tasks = viewModel.tasks
    var selectedMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    Scaffold(
        topBar = {
            CustomTopAppBar(
                selectedMonth = selectedMonth,
                onPrevMonth = { selectedMonth = selectedMonth.minusMonths(1) },
                onNextMonth = { selectedMonth = selectedMonth.plusMonths(1) },
                onSearchClick = { /* TODO */ },
                onSendClick = { /* TODO */ }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
                }
                Text(
                    text = "${
                        selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
                    } ${selectedMonth.year}",
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = { selectedMonth = selectedMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
                }
            }

            val daysInMonth = YearMonth.from(selectedMonth).lengthOfMonth()
            val firstDayOfWeek = selectedMonth.dayOfWeek.value % 7 // để bắt đầu từ Monday
            val days = (1..daysInMonth).map { selectedMonth.withDayOfMonth(it) }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp)
            ) {
                // thêm header: Mon - Sun
                items(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")) { day ->
                    Text(
                        text = day,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // padding trước ngày 1
                items(firstDayOfWeek - 1) {
                    Box(modifier = Modifier.height(80.dp))
                }

                // render từng ngày
                items(days) { date ->
                    DayCell(
                        date,
                        tasks.filter { it.date == date },
                        onDayClick = { clickedDate ->
//                           navController.navigate("day/${clickedDate}")
                            navController.navigate("daily_schedule/${clickedDate}")
                        }
                    )
                }
            }
        }

    }
}


@Composable
fun DayCell(date: LocalDate, dayTasks: List<CalendarTask>, onDayClick: (LocalDate) -> Unit) {
    Column(
        modifier = Modifier
            .border(1.dp, Color.LightGray)
            .padding(4.dp)
            .height(80.dp)
            .clickable(onClick = { onDayClick(date) })
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        dayTasks.take(2).forEach { task ->
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
        if (dayTasks.size > 2) {
            Text(
                text = "+${dayTasks.size - 2} more",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Blue
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    selectedMonth: LocalDate,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSearchClick: () -> Unit,
    onSendClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Icon bên trái (grid)
                Icon(
                    imageVector = Icons.Default.GridView, // cần import 1 icon gần giống
                    contentDescription = "View",
                    tint = Color(0xFF1565C0) // xanh
                )

                Text(
                    text = "${
                        selectedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
                    } ${selectedMonth.year}",
                    style = MaterialTheme.typography.titleMedium
                )

                // Icon paper plane nhỏ cạnh text
                Icon(
                    painter = painterResource(id = R.drawable.paper_plane), // plane icon
                    contentDescription = "Send",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        actions = {
            // Search button
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, Color.LightGray, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Send button màu xanh
            IconButton(
                onClick = onSendClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF1565C0), shape = RoundedCornerShape(12.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.paper_plane),
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

        }
    )
}
