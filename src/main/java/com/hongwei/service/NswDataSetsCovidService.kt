package com.hongwei.service

import com.hongwei.model.common.AuState
import com.hongwei.model.v1.covid19.nsw.NswDataSetsSource
import com.hongwei.model.v2.jpa.au.StateLGADataV2
import com.hongwei.model.v2.jpa.au.LGADataV2
import com.hongwei.util.CsvUtil
import com.hongwei.util.DateTimeParseUtil
import com.hongwei.util.TimeStampUtil
import com.hongwei.util.curl.CUrlWrapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service


@Service
class NswDataSetsCovidService : StateDataSetsServiceInterface {
    private val logger: Logger = LogManager.getLogger(NswDataSetsCovidService::class.java)

    companion object {
        private const val CSV_DATA_URL = "https://data.nsw.gov.au/data/dataset/nsw-covid-19-cases-by-location-and-likely-source-of-infection/resource/2776dbb8-f807-4fb2-b1ed-184a6fc2c8aa"

        private const val LOCATE_STRING_OPEN = "\"DCTERMS.Identifier\""
    }

    override fun parseCsv(): StateLGADataV2? {
        val doc = CUrlWrapper.curl(CSV_DATA_URL) ?: return null
        val lastUpdate = getLastUpdateStringFromWeb(doc)
        val csvPath = getCSVUrl(doc.toString())
        val lines = CsvUtil.readCSVFromUrl(csvPath)
        val sourceList = mutableListOf<NswDataSetsSource>()
        lines.forEach {
            val data = it.split(",")
            when (data.size) {
                7 -> NswDataSetsSource(
                        date = data.first(),
                        postcode = data[1].toLongOrNull() ?: 0L,
                        likelySourceOfInfection = data[2],
                        lhd2010Code = data[3],
                        lhd2010Name = data[4],
                        lgaCode19 = data[5].toLongOrNull() ?: 0L,
                        lgaName19 = data[6]
                )
                else -> {
                    logger.debug("unrecognized record: $data")
                    null
                }
            }?.let { record ->
                sourceList.add(record)
            }
        }

        sourceList.sortByDescending { it.date }
        val lastCase = sourceList.first()
        val lastDayCases = sourceList.filter { it.date == lastCase.date }
        return StateLGADataV2(
                lastUpdate = lastUpdate,
                lastRecordTimeStamp = sourceList.firstOrNull()?.date?.parseDate() ?: 0,
                state = AuState.Nsw.name,
                lga = lastDayCases.groupBy {
                    it.postcode
                }.mapNotNull {
                    it.key?.let { postcode ->
                        LGADataV2(postcode.toInt(), it.value.size.toLong())
                    }
                }.sortedByDescending {
                    it.cases
                }
        )
    }

    private fun getLastUpdateStringFromWeb(doc: Document): String {
        val list = doc.body().getElementsByTag("tr")
        list.forEach {
            if (it.toString().contains("Last updated")) {
                return it.getElementsByTag("td")[0].childNodes().first().toString()
            }
        }
        return ""
    }

    private fun getCSVUrl(doc: String): String {
        val index0 = doc.indexOf(LOCATE_STRING_OPEN)
        val mid2 = doc.substring(index0 + LOCATE_STRING_OPEN.length)
        val index1 = mid2.indexOf("\">")
        val mid3 = mid2.substring(0, index1)
        val mid4 = mid3.replace("content=\"", "").trim()
        return mid4
    }

    private fun String?.parseDate(): Long? = this?.let {
        DateTimeParseUtil.parseDateForSlashFormat(it)?.time
    } ?: this?.let {
        DateTimeParseUtil.parseDate(it)?.time
    }
}