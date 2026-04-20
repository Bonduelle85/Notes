package com.gorokhov.notes.presentation.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


object DateFormatter {

    private val millisInHour = TimeUnit.HOURS.toMillis(1)
    private val millisInDay = TimeUnit.DAYS.toMillis(1)
    private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

    fun formatDateToString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diffMillis = now - timestamp

        return when {
            diffMillis < millisInHour -> "Just now"
            diffMillis < millisInDay -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
                "$hours h ago"
            }
            else -> {
                formatter.format(timestamp)
            }
        }
    }
}