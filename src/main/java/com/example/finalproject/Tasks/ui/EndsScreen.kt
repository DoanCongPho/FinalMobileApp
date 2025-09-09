package com.example.finalproject.Tasks.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.Tasks.model.RepeatEnd
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun EndsScreen(
    initialRepeatEnd: RepeatEnd,
    onDone: (RepeatEnd) -> Unit,
    onBack: () -> Unit,
    calendarViewModel: com.example.finalproject.calendar.viewmodel.CalendarViewModel1
) {
    var selectedOption by remember { mutableStateOf(initialRepeatEnd) }
    var selectedDate by remember { 
        mutableStateOf(
            when (initialRepeatEnd) {
                is RepeatEnd.UntilDate -> initialRepeatEnd.endDate
                else -> LocalDate.now()
            }
        )
    }
    var occurrenceCount by remember { 
        mutableStateOf(
            when (initialRepeatEnd) {
                is RepeatEnd.AfterOccurrences -> initialRepeatEnd.count.toString()
                else -> ""
            }
        )
    }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    
    // Update selected date when month changes (if not current month)
    LaunchedEffect(currentMonth) {
        if (currentMonth != YearMonth.now() && selectedOption is RepeatEnd.UntilDate) {
            selectedDate = currentMonth.atDay(1)
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button and text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onBack() }
                ) {
                    Image(
                        painter = painterResource(id = getDrawableId("back_button")),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Back",
                        color = Color(0xFF1976D2),
                        fontSize = 18.sp
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Title
                Text(
                    text = "Ends",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Done button
                Text(
                    text = "Done",
                    color = Color(0xFF1976D2),
                    fontSize = 18.sp,
                    modifier = Modifier.clickable {
                        // Update draft task with new repeat end
                        calendarViewModel.updateDraftTask { draft ->
                            draft.copy(repeatEnd = selectedOption)
                        }
                        onDone(selectedOption)
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Repeat end icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = getDrawableId("repeat_end")),
                    contentDescription = "Repeat End",
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Show all options first
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Option 1: Does not end
                OptionRow(
                    text = "Does not end",
                    isSelected = selectedOption is RepeatEnd.Never,
                    onClick = { selectedOption = RepeatEnd.Never }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Option 2: On a date
                val dateText = if (selectedOption is RepeatEnd.UntilDate) {
                    val formatter = if (selectedDate.year == LocalDate.now().year) {
                        DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.getDefault())
                    } else {
                        DateTimeFormatter.ofPattern("EEEE, d MMM yyyy", Locale.getDefault())
                    }
                    selectedDate.format(formatter)
                } else {
                    "On a date"
                }
                OptionRow(
                    text = dateText,
                    isSelected = selectedOption is RepeatEnd.UntilDate,
                    onClick = { selectedOption = RepeatEnd.UntilDate(selectedDate) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Option 3: After occurrences
                val occurrenceText = if (selectedOption is RepeatEnd.AfterOccurrences) {
                    "After $occurrenceCount occurrences"
                } else {
                    "After number of occurrences"
                }
                OptionRow(
                    text = occurrenceText,
                    isSelected = selectedOption is RepeatEnd.AfterOccurrences,
                    onClick = { 
                        selectedOption = RepeatEnd.AfterOccurrences(occurrenceCount.toIntOrNull() ?: 1)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Show specific UI based on selected option
            when (selectedOption) {
                is RepeatEnd.Never -> {
                    // No additional UI needed for "Does not end"
                }
                is RepeatEnd.UntilDate -> OnDateUI(
                    selectedDate = selectedDate,
                    currentMonth = currentMonth,
                    onDateSelected = { date ->
                        selectedDate = date
                        selectedOption = RepeatEnd.UntilDate(date)
                    },
                    onMonthChanged = { month -> currentMonth = month }
                )
                is RepeatEnd.AfterOccurrences -> AfterOccurrencesUI(
                    occurrenceCount = occurrenceCount,
                    onCountChanged = { count ->
                        occurrenceCount = count
                        val intCount = count.toIntOrNull() ?: 1
                        selectedOption = RepeatEnd.AfterOccurrences(intCount)
                    }
                )
            }
        }
    }
}

@Composable
private fun OptionRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        
        if (isSelected) {
            Image(
                painter = painterResource(id = getDrawableId("yes_button")),
                contentDescription = "Selected",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun OnDateUI(
    selectedDate: LocalDate,
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit
) {
    var totalDragAmount by remember { mutableStateOf(0f) }
    
    Column(
        modifier = Modifier
            .pointerInput(currentMonth) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        totalDragAmount = 0f
                    },
                    onDragEnd = {
                        if (totalDragAmount > 100) {
                            // Swipe right - go to previous month (only if not current month)
                            val previousMonth = currentMonth.minusMonths(1)
                            if (previousMonth >= YearMonth.now()) {
                                onMonthChanged(previousMonth)
                            }
                        } else if (totalDragAmount < -100) {
                            // Swipe left - go to next month (always allowed)
                            val nextMonth = currentMonth.plusMonths(1)
                            onMonthChanged(nextMonth)
                        }
                        totalDragAmount = 0f
                    }
                ) { _, dragAmount ->
                    totalDragAmount += dragAmount
                }
            }
    ) {
        // Month header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val monthText = if (currentMonth.year == LocalDate.now().year) {
                currentMonth.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault()))
            } else {
                currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
            }
            Text(
                text = monthText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Day headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S" ).forEach { day ->
                Text(
                    text = day,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar grid
        CalendarGrid(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun AfterOccurrencesUI(
    occurrenceCount: String,
    onCountChanged: (String) -> Unit
) {
    Column {
        // Show number input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "After ",
                fontSize = 16.sp,
                color = Color.Black
            )
            
            OutlinedTextField(
                value = occurrenceCount,
                onValueChange = onCountChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.width(80.dp)
            )
            
            Text(
                text = " occurrences",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Convert to 0-6 where Monday = 0
    val daysInMonth = currentMonth.lengthOfMonth()
    
    // Create list of all days to show (including padding)
    val calendarDays = mutableListOf<LocalDate?>()
    
    // Add empty days for padding at the beginning
    repeat(firstDayOfWeek) {
        calendarDays.add(null)
    }
    
    // Add all days of the month
    for (day in 1..daysInMonth) {
        calendarDays.add(currentMonth.atDay(day))
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(calendarDays) { date ->
            if (date != null) {
                val isToday = date == today
                val isSelected = date == selectedDate
                val isPastDate = date.isBefore(today)
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isToday -> Color(0xFF1976D2) // Dark blue for today
                                isSelected -> Color(0xFF90CAF9) // Light blue for selected
                                else -> Color.Transparent
                            }
                        )
                        .clickable(enabled = !isPastDate) {
                            onDateSelected(date)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = when {
                            isToday -> Color.White
                            isPastDate -> Color.Gray
                            else -> Color.Black
                        },
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Empty space for padding
                Spacer(modifier = Modifier.aspectRatio(1f))
            }
        }
    }
}
