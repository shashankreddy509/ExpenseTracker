package com.shashank.expense.tracker.utils

import kotlinx.datetime.*
import kotlin.time.Duration.Companion.days

object DateTimeUtils {
    private val systemTimeZone = TimeZone.currentSystemDefault()

    fun getCurrentDateTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(systemTimeZone)
    }

    fun formatDateTime(timestamp: Long): String {
        return try {
            val dateTime = Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(systemTimeZone)
            "${dateTime.date} ${formatTime(dateTime)}"
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    fun formatDate(timestamp: Long): String {
        return try {
            val date = Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(systemTimeZone).date
            formatDate(date)
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    fun formatDate(date: LocalDate): String {
        return "${date.month.name.lowercase().capitalize()} ${date.dayOfMonth}, ${date.year}"
    }

    fun formatTime(dateTime: LocalDateTime): String {
        val hour = if (dateTime.hour > 12) dateTime.hour - 12 else dateTime.hour
        val amPm = if (dateTime.hour >= 12) "PM" else "AM"
        val minute = dateTime.minute.toString().padStart(2, '0')
        return "$hour:$minute $amPm"
    }

    fun isValidDate(timestamp: Long): Boolean {
        return try {
            val dateTime = Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(systemTimeZone)
            
            // Check if date is not in future
            val currentDateTime = getCurrentDateTime()
            dateTime <= currentDateTime
        } catch (e: Exception) {
            false
        }
    }

    fun getRelativeTimeSpan(timestamp: Long): String {
        return try {
            val dateTime = Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(systemTimeZone)
            val currentDateTime = getCurrentDateTime()
            
            val days = currentDateTime.date.toEpochDays() - dateTime.date.toEpochDays()
            
            when {
                days == 0 -> "Today"
                days == 1 -> "Yesterday"
                days < 7 -> "${days} days ago"
                days < 30 -> "${days / 7} weeks ago"
                days < 365 -> "${days / 30} months ago"
                else -> "${days / 365} years ago"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun getMonthStartEnd(month: Month, year: Int = getCurrentDateTime().year): Pair<Long, Long> {
        val startDate = LocalDate(year, month, 1)
        val endDate = if (month.ordinal == 11) {
            LocalDate(year + 1, Month.JANUARY, 1)
        } else {
            LocalDate(year, Month.values()[(month.ordinal + 1)], 1)
        }.minus(DatePeriod(days = 1))

        return Pair(
            LocalDateTime(startDate, LocalTime(0, 0, 0))
                .toInstant(TimeZone.UTC)
                .toEpochMilliseconds(),
            LocalDateTime(endDate, LocalTime(23, 59, 59))
                .toInstant(TimeZone.UTC)
                .toEpochMilliseconds()
        )
    }

    fun isToday(timestamp: Long): Boolean {
        return try {
            val dateTime = Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(systemTimeZone)
            val currentDate = getCurrentDateTime().date
            dateTime.date == currentDate
        } catch (e: Exception) {
            false
        }
    }

    fun isYesterday(timestamp: Long): Boolean {
        return try {
            val dateTime = Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(systemTimeZone)
            val yesterday = getCurrentDateTime().date.minus(DatePeriod(days = 1))
            dateTime.date == yesterday
        } catch (e: Exception) {
            false
        }
    }
} 