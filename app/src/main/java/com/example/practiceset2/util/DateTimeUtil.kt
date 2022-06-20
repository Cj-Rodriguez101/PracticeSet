package com.example.practiceset2.util

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

object DateTimeUtil {

    fun toLocalDateString(date: Double): String {
        return Instant.fromEpochMilliseconds(date.toLong())
            .toLocalDateTime(TimeZone.UTC).toString()
    }

    fun toEpochMilliseconds(date: String): Double {
        val trimmedData = date.split("+")[0]
        return LocalDateTime.parse(trimmedData)
            .toInstant(TimeZone.UTC).toEpochMilliseconds().toDouble()
    }

    fun toEpochMilliMovieDate(date: String): Double{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDateTime =  java.time.LocalDateTime
            .of(java.time.LocalDate.parse(date, formatter),
                java.time.LocalDateTime.MIN.toLocalTime())
        return localDateTime.toKotlinLocalDateTime()
            .toInstant(TimeZone.UTC).toEpochMilliseconds().toDouble()
    }
}