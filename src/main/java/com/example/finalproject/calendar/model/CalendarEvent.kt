package com.example.finalproject.calendar.model

import java.time.LocalDateTime

data class CalendarEvent(
    val id: String,
    val title: String,
    val time: LocalDateTime,
    val type: EventType,
    val color: Long? = null
)

enum class EventType {
    DAILY_STANDUP,
    MEETING,
    VACATION,
    OTHER
}
