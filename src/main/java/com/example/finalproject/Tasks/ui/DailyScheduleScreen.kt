
package com.example.dailyview.Tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Divider
import com.example.dailyview.Tasks.model.CalendarTask
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.example.dailyview.Tasks.ui.getDrawableId
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DailySchedule(
    tasks: List<CalendarTask>,
    date: LocalDate,
    modifier: Modifier = Modifier,
    onTaskClick: (CalendarTask) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedButton by remember { mutableStateOf("calendar") }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header section 1: Navigation bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = getDrawableId("back_button")),
                    contentDescription = "Back",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("dd-MM-yy")),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = getDrawableId("small_plane_button")),
                    contentDescription = "Small Plane",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = getDrawableId("search_button")),
                    contentDescription = "Search",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = getDrawableId("plane_button")),
                    contentDescription = "Plane",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Header section 2: All-day tasks
                val allDayTasks = tasks.filter { it.isAllDay && it.date == date }
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Day and date
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = date.dayOfWeek.name.take(3),
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF1976D2), shape = RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // All-day tasks list
                    Column(modifier = Modifier.weight(1f)) {
                        val showTasks = if (expanded || allDayTasks.size <= 2) allDayTasks else allDayTasks.take(2)
                        showTasks.forEach { task ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE3F2FD),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable { onTaskClick(task) }
                            ) {
                                Text(
                                    text = task.title,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(8.dp),
                                    textDecoration = if (task.state == 1) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                )
                            }
                        }
                        if (allDayTasks.size > 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = if (expanded) Arrangement.End else Arrangement.Start
                            ) {
                                if (!expanded) {
                                    Image(
                                        painter = painterResource(id = getDrawableId("expand_button")),
                                        contentDescription = "Expand",
                                        modifier = Modifier.size(20.dp).clickable { expanded = true }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${allDayTasks.size - 2} more",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "Show less",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Image(
                                        painter = painterResource(id = getDrawableId("show_less_button")),
                                        contentDescription = "Show less",
                                        modifier = Modifier.size(20.dp).clickable { expanded = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Main schedule
            Row(modifier = Modifier.fillMaxSize()) {
                // Vertical line on the left
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color.LightGray)
                )
                // The rest of the schedule
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items((0..23).toList()) { hour ->
                        HourRow(hour = hour, tasks = tasks, onTaskClick = onTaskClick)
                    }
                }
            }
        }

        // Floating add button above navigation bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 88.dp)
        ) {
            var isHovered by remember { mutableStateOf(false) }
            Image(
                painter = painterResource(id = getDrawableId(if (isHovered) "add_button_hover" else "add_button")),
                contentDescription = "Add Task",
                modifier = Modifier
                    .size(56.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onAddClick() }
            )
        }

        // Navigation bar at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-45).dp)
                .background(Color(0xFFF0F0F0)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val buttons = listOf("calendar", "study", "chat", "account")
            buttons.forEach { name ->
                val isSelected = name == selectedButton
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            selectedButton = name
                            // TODO: Handle navigation click
                        }
                ) {
                    val imageName = if (isSelected) "${name}_button_hover" else "${name}_button"
                    Image(
                        painter = painterResource(
                            id = getDrawableId(imageName)
                        ),
                        contentDescription = "$name navigation button",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

