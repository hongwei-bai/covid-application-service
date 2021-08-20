package com.hongwei.util

import java.net.URL
import java.util.*

object CsvUtil {
    fun readCSVFromUrl(url: String): List<String> {
        val list = mutableListOf<String>()
        try {
            val rowdata = URL(url)
            val data = rowdata.openConnection()
            val input = Scanner(data.getInputStream())
            if (input.hasNext()) // remove
                input.nextLine() //remove
            while (input.hasNextLine()) {
                val line = input.nextLine()
                list.add(line)
//                println(line)
            }
        } catch (e: Exception) {
//            print(e)
        }
        return list
    }
}