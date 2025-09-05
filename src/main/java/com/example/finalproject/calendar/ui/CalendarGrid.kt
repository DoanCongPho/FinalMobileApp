package com.example.finalproject.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarGrid(
    selectedDate: LocalDate,
    onDateSelect: (LocalDate) -> Unit,
    events: List<LocalDate> = emptyList()
) {
    val currentMonth = YearMonth.from(selectedDate)
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    
    // Days of week header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
        daysOfWeek.forEach { day ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }

    // Calendar days
    var currentDay = firstDayOfMonth
    while (currentDay.isBefore(lastDayOfMonth) || currentDay.isEqual(lastDayOfMonth)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            for (dayOfWeek in 0..6) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(2.dp)
                ) {
                    if (currentDay.monthValue == selectedDate.monthValue && 
                        !currentDay.isAfter(lastDayOfMonth)) {
                        
                        val isSelected = currentDay.isEqual(selectedDate)
                        val hasEvent = events.any { it.isEqual(currentDay) }
                        val isToday = currentDay.isEqual(LocalDate.now())
                        
                        CalendarDay(
                            day = currentDay.dayOfMonth,
                            isSelected = isSelected,
                            hasEvent = hasEvent,
                            isToday = isToday,
                            onClick = { onDateSelect(currentDay) }
                        )
                        
                        currentDay = currentDay.plusDays(1)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    hasEvent: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.toString(),
                color = when {
                    isSelected -> Color.White
                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> Color.Black
                },
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
            )
            if (hasEvent) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(
                            if (isSelected) Color.White
                            else MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                )
            }
        }
    }
}
