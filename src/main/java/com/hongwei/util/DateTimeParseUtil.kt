package com.hongwei.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeParseUtil {
	//2021-04-07
	fun parseDate(dateString: String): Date? = try {
		val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
		simpleDateFormat.parse(dateString)
	} catch (e: Exception) {
		null
	}

	fun toDisplay(date: Date?): String = SimpleDateFormat("yyyy-MM-dd").format(date)
}