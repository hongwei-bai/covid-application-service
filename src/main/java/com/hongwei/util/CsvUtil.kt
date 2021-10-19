package com.hongwei.util

import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object CsvUtil {
    private val logger: Logger = LogManager.getLogger(CsvUtil::class.java)

    fun readCSVFromUrl(url: String): List<String> {
        val list = mutableListOf<String>()
        try {
            val rowData = URL(url)
            val data = rowData.openConnection()
            val input = Scanner(data.getInputStream())
            if (input.hasNext()) // remove
                input.nextLine() //remove
            while (input.hasNextLine()) {
                val line = input.nextLine()
                list.add(line)
            }
        } catch (e: Exception) {
            print(e)
            logger.error(e.localizedMessage)
            e.stackTrace.forEach {
                logger.error(it)
            }
        }
        return list
    }

    fun readCSVFromUrlBackupMethod(url: String): List<String> {
        val list = mutableListOf<String>()
        try {
            val rowData = URL(url)
            val data: HttpURLConnection = rowData.openConnection() as HttpURLConnection
            data.connectTimeout = 10_000
            val input = Scanner(data.inputStream)
            if (input.hasNext()) // remove
                input.nextLine() //remove
            while (input.hasNextLine()) {
                val line = input.nextLine()
                list.add(line)
            }
        } catch (e: Exception) {
            print(e)
            logger.error("[BackupCSVReader]${e.localizedMessage}")
            e.stackTrace.forEach {
                logger.error("[BackupCSVReader]$it")
            }
        }
        return list
    }
}