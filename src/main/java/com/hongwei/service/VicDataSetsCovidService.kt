package com.hongwei.service

import com.hongwei.model.common.AuState
import com.hongwei.model.v1.covid19.vic.VicDataSetsSource
import com.hongwei.model.v2.jpa.au.LGADataV2
import com.hongwei.model.v2.jpa.au.StateLGADataV2
import com.hongwei.util.CsvUtil
import com.hongwei.util.DateTimeParseUtil.parseDateForSlashFormat
import com.hongwei.util.curl.CUrlWrapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service


@Service
class VicDataSetsCovidService : StateDataSetsServiceInterface {
    private val logger: Logger = LogManager.getLogger(VicDataSetsCovidService::class.java)

    companion object {
        private const val VIC_DATASETS_URL = "https://discover.data.vic.gov.au/dataset/victorian-coronavirus-data/resource/e3c72a49-6752-4158-82e6-116bea8f55c8"
    }

    override fun parseCsv(): StateLGADataV2? {
        val doc = CUrlWrapper.curl(VIC_DATASETS_URL) ?: return null
        val csvPath = getCSVUrl(doc)
        val lines = CsvUtil.readCSVFromUrl(csvPath)
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
                    logger.error("unrecognized record: $data")
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

    private fun getCSVUrl(doc: Document): String =
            doc.getElementsByClass("resource-url-analytics").attr("href")

    private fun String?.parseDate(): Long? = this?.let {
        parseDateForSlashFormat(it)?.time
    }
}