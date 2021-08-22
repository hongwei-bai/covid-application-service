package com.hongwei.util

import java.lang.StringBuilder
import java.net.URL
import java.util.*

object OnlineJsonReaderUtil {
	fun readJSONFromUrl(url: String): String {
		val stringBuilder = StringBuilder()
		try {
			val rowdata = URL(url)
			val data = rowdata.openConnection()
			val input = Scanner(data.getInputStream())
			if (input.hasNext()) // remove
				input.nextLine() //remove
			while (input.hasNextLine()) {
				val line = input.nextLine()
				stringBuilder.append(line)
			}
		} catch (e: Exception) {
			print(e)
		}
		return stringBuilder.toString()
	}
}