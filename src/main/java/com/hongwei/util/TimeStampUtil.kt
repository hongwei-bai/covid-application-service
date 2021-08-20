package com.hongwei.util

import java.util.*

object TimeStampUtil {
    fun getTimeVersionWithDayAndDataVersion(timeZone: String? = SYDNEY, dataVersion: Int? = null): Long {
        val time = Calendar.getInstance(TimeZone.getTimeZone(timeZone))
        val year = time.get(Calendar.YEAR)
        val month = time.get(Calendar.MONTH)
        val day = time.get(Calendar.DAY_OF_MONTH)
        return (year.toString() + (month + 1).toString().padStart(2, '0') +
                day.toString().padStart(2, '0') + (dataVersion ?: 0).toString().padStart(2, '0')).toLong()
    }

    fun getTimeVersionWithHour(timeZone: String? = SYDNEY): Long {
        val time = Calendar.getInstance(TimeZone.getTimeZone(timeZone))
        val year = time.get(Calendar.YEAR)
        val month = time.get(Calendar.MONTH)
        val day = time.get(Calendar.DAY_OF_MONTH)
        val hour = time.get(Calendar.HOUR_OF_DAY)
        return (year.toString() + (month + 1).toString().padStart(2, '0') +
                day.toString().padStart(2, '0') + hour.toString().padStart(2, '0')).toLong()
    }

    fun getTimeVersionWithMinute(timeZone: String? = SYDNEY): Long {
        val time = Calendar.getInstance(TimeZone.getTimeZone(timeZone))
        val year = time.get(Calendar.YEAR)
        val month = time.get(Calendar.MONTH)
        val day = time.get(Calendar.DAY_OF_MONTH)
        val hour = time.get(Calendar.HOUR_OF_DAY)
        val minute = time.get(Calendar.MINUTE)
        return (year.toString() + (month + 1).toString().padStart(2, '0') +
                day.toString().padStart(2, '0') + hour.toString().padStart(2, '0') + minute.toString().padStart(2, '0')).toLong()
    }

    const val UTC = "UTC"
    const val SYDNEY = "Australia/Sydney"
}