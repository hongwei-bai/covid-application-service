package com.hongwei.service

import com.hongwei.constants.NoContent
import com.hongwei.constants.ResetContent
import com.hongwei.model.covid19.CovidAuMapper
import com.hongwei.model.covid19.auGov.AuGovCovidSource
import com.hongwei.model.jpa.au.*
import com.hongwei.util.CsvUtil
import com.hongwei.util.TimeStampUtil
import com.hongwei.util.curl.CUrlWrapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AuGovCovidService {
	private val logger: Logger = LogManager.getLogger(AuGovCovidService::class.java)

	companion object {
		private const val AU_GOV_COVID_DATA_URL = "https://data.nsw.gov.au/data/dataset/nsw-covid-19-cases-by-location-and-likely-source-of-infection/resource/2776dbb8-f807-4fb2-b1ed-184a6fc2c8aa"

		private const val LOCATE_STRING_OPEN = "\"DCTERMS.Identifier\""
	}

	@Autowired
	private lateinit var mobileCovidAuRepository: MobileCovidAuRepository

	@Autowired
	private lateinit var mobileCovidAuCsvRepository: MobileCovidAuCsvRepository

	fun getAuCovidData(dataVersion: Long, inDays: Long?): MobileCovidAuEntity {
		val entityDb = mobileCovidAuRepository.findRecentRecord()
		entityDb?.let {
			if (dataVersion < entityDb.dataVersion) {
				entityDb.dataByDay = entityDb.dataByDay.filterIndexed { index, _ -> index < inDays ?: 1 }
				return entityDb
			} else throw ResetContent
		} ?: throw NoContent
	}

	fun parseCsv(): MobileCovidAuEntity? {
		val entityDb = mobileCovidAuRepository.findRecentRecord()
		val doc = CUrlWrapper.curl(AU_GOV_COVID_DATA_URL) ?: return entityDb
		val lastUpdate = getLastUpdateStringFromWeb(doc)
		val csvPath = getCSVUrl(doc.toString())
		val dataVersion = TimeStampUtil.getTimeVersionWithHour()
		val lines = CsvUtil.readCSVFromUrl(csvPath)
		val sourceList = mutableListOf<AuGovCovidSource>()
		lines.forEach {
			val data = it.split(",")
			when (data.size) {
				7 -> AuGovCovidSource(
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
		mobileCovidAuCsvRepository.save(
			MobileCovidAuCsvEntity(
				dataVersion = dataVersion,
				lastUpdate = lastUpdate,
				recordsCount = sourceList.size,
				lastRecordDate = sourceList.last().date,
				csvPath = csvPath
			)
		)

		val entity = CovidAuMapper.map(sourceList, lastUpdate, sourceList.last().date, sourceList.size)
		if (entityDb != entity) {
			if (mobileCovidAuRepository.findAll().isNotEmpty()) {
				mobileCovidAuRepository.deleteAll()
			}
			mobileCovidAuRepository.save(entity)
			return entity
		}
		return entityDb
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

	private fun getJsonPath(doc: Document): String? {
		val dropdownMenuElement = doc.body().getElementsByClass("dropdown-menu")
		val list = dropdownMenuElement.first().getElementsByTag("a")
		list.forEach {
			if (it.childNodes().firstOrNull()?.childNodes()?.firstOrNull()?.toString() == "JSON") {
				return it.attr("href")
			}
		}
		return null
	}

	private fun getCSVUrl(doc: String): String {
		val index0 = doc.indexOf(LOCATE_STRING_OPEN)
		val mid2 = doc.substring(index0 + LOCATE_STRING_OPEN.length)
		val index1 = mid2.indexOf("\">")
		val mid3 = mid2.substring(0, index1)
		val mid4 = mid3.replace("content=\"", "").trim()
		return mid4
	}
}