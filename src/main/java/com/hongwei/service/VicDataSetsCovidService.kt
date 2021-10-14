package com.hongwei.service

import com.hongwei.model.common.AuState
import com.hongwei.model.v1.covid19.vic.VicDataSetsSource
import com.hongwei.model.v2.jpa.au.LGADataV2
import com.hongwei.model.v2.jpa.au.LGADayDataV2
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
        private const val CSV_DATA_URL = "https://discover.data.vic.gov.au/dataset/victorian-coronavirus-data/resource/e3c72a49-6752-4158-82e6-116bea8f55c8"
    }

    override fun parseCsv(): LGADataV2 {
        val dataVersion = TimeStampUtil.getTimeVersionWithHour()
        val lines = CsvUtil.readCSVFromUrl(CSV_DATA_URL)
        val sourceList = mutableListOf<VicDataSetsSource>()
        lines.forEach {
            val data = it.split(",")
            sourceList.add(VicDataSetsSource(
                    postcode = data.first().toInt(),
                    population = data[1].toLong(),
                    active = data[2].toInt(),
                    cases = data[3].toInt(),
                    rate = data[4].toFloat(),
                    new = data[5].toInt(),
                    band = data[6].toInt(),
                    data_date = data[5],
                    file_processed_date = data[6]
            ))
        }

        sourceList.sortByDescending { it.data_date }
        val lastCase = sourceList.first()
        val lastDayCases = sourceList.filter { it.data_date == lastCase.data_date }
        return LGADataV2(
                dataCollectTimeStamp = dataVersion,
                lastUpdate = null,
                lastRecordTimeStamp = sourceList.firstOrNull()?.data_date?.parseDate() ?: 0,
                state = AuState.Vic.name,
                data = LGADayDataV2(
                        sourceList.firstOrNull()?.data_date?.parseDate() ?: 0,
                        lastDayCases.size
                ),
                historyData = sourceList.groupBy { it.data_date }.map {
                    LGADayDataV2(
                            it.key.parseDate() ?: 0,
                            it.value.size
                    )
                }
        )
    }

    private fun String?.parseDate(): Long? = this?.let {
        parseDateForSlashFormat(it)?.time
    }
}