package com.example.finalproject.Tasks.model
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime



enum class RepeatFrequency {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

sealed class RepeatEnd {
    object Never : RepeatEnd()
    data class UntilDate(val endDate: LocalDate) : RepeatEnd()
    data class AfterOccurrences(val count: Int) : RepeatEnd()
}

sealed class MonthlyPattern {
    data class SameDay(val dayOfMonth: Int) : MonthlyPattern()
    data class NthWeekday(val weekOfMonth: Int, val dayOfWeek: DayOfWeek) : MonthlyPattern()
}

        data class CalendarTask(
    val id: String,
    val title: String,
    val details: String? = null,
    val isAllDay: Boolean = false,
    val date: LocalDate,
    val time: LocalTime? = null,                           // Only used if not all-day
    val repeatFrequency: RepeatFrequency = RepeatFrequency.NONE,
    val monthlyPattern: MonthlyPattern? = null,             // Only if frequency is MONTHLY
    val repeatEnd: RepeatEnd = RepeatEnd.Never,
    val tag: String? = null
)

