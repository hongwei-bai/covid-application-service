package com.hongwei.service

import com.hongwei.model.common.AuState
import com.hongwei.model.v1.covid19.vic.VicDataSetsSource
import com.hongwei.model.v2.jpa.au.StateLGADataV2
import com.hongwei.model.v2.jpa.au.LGADataV2
import com.hongwei.util.CsvUtil
import com.hongwei.util.DateTimeParseUtil.parseDateForSlashFormat
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.stereotype.Service


@Service
class VicDataSetsCovidService : StateDataSetsServiceInterface {
    private val logger: Logger = LogManager.getLogger(VicDataSetsCovidService::class.java)

    companion object {
        private const val CSV_DATA_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTwXSqlP56q78lZKxc092o6UuIyi7VqOIQj6RM4QmlVPgtJZfbgzv0a3X7wQQkhNu8MFolhVwMy4VnF/pub?gid=0&single=true&output=csv"
    }

    override fun parseCsv(): StateLGADataV2 {
        val dataVersion = TimeStampUtil.getTimeVersionWithHour()
        val lines = CsvUtil.readCSVFromUrl(CSV_DATA_URL)
        val sourceList = mutableListOf<VicDataSetsSource>()
        lines.forEach {
            val data = it.split(",")
            val dataItem = when (data.size) {
                9 -> VicDataSetsSource(
                        postcode = data.first().toInt(),
                        population = data[1].toLongOrNull() ?: 0,
                        active = data[2].toLongOrNull() ?: 0,
                        cases = data[3].toLongOrNull() ?: 0,
                        rate = data[4].toFloatOrNull() ?: 0f,
                        new = data[5].toLongOrNull() ?: 0,
                        band = data[6].toLongOrNull() ?: 0,
                        data_date = data[7],
                        file_processed_date = data[8]
                )
                else -> {
                    logger.debug("unrecognized record: $data")
                    null
                }
            }
            dataItem?.let { sourceList.add(dataItem) }
        }

        return StateLGADataV2(
                lastUpdate = null,
                lastRecordTimeStamp = sourceList.firstOrNull()?.data_date?.parseDate() ?: 0,
                state = AuState.Vic.name,
                lga = sourceList.map {
                    LGADataV2(it.postcode, it.cases)
                }.filter {
                    it.cases > 0
                }.sortedByDescending {
                    it.cases
                }
        )
    }

    private fun String?.parseDate(): Long? = this?.let {
        parseDateForSlashFormat(it)?.time
    }
}